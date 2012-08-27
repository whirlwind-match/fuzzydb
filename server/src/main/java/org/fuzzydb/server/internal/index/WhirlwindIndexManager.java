/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.server.internal.index;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.fuzzydb.attrs.IScoreConfiguration;
import org.fuzzydb.attrs.WhirlwindConfiguration;
import org.fuzzydb.attrs.internal.ScoreConfigurationManager;
import org.fuzzydb.attrs.search.SearchSpecImpl;
import org.fuzzydb.client.Store;
import org.fuzzydb.client.exceptions.UnknownObjectException;
import org.fuzzydb.client.internal.MetaObject;
import org.fuzzydb.client.internal.RefImpl;
import org.fuzzydb.client.marker.IWhirlwindItem;
import org.fuzzydb.core.exceptions.ArchException;
import org.fuzzydb.core.whirlwind.SearchSpec;
import org.fuzzydb.server.internal.common.ServiceRegistry;
import org.fuzzydb.server.internal.search.DumbOrderedSearch;
import org.fuzzydb.server.internal.search.Search;
import org.fuzzydb.server.internal.server.Database;
import org.fuzzydb.server.internal.server.Namespace;
import org.fuzzydb.server.internal.table.UserTable;
import org.fuzzydb.server.internal.whirlwind.ScoreConfigOptimiser;
import org.fuzzydb.server.services.IndexImplementationsService;
import org.slf4j.Logger;



/**
 * Provides support to manage fuzzy indices
 */
public class WhirlwindIndexManager<T extends IWhirlwindItem> implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Ref to the related config - transient to allow specific one to be discovered on restart, if
	 * previously only had global (not a great solution, admittedly) */
    transient private RefImpl<WhirlwindConfiguration> wwConfigRef = null;
    transient private Namespace wwConfigNamespace;

	private final UserTable<T> table;

	private final Map<String, Index<T>> allIndexes;


    
	/**
	 * 
	 * @param table
	 * @param allIndexes - ref to Map of all indexes to which this should add indexes that should be updated
	 */
	public WhirlwindIndexManager(UserTable<T> table, Map<String, Index<T>> allIndexes) {
        this.table = table;
        this.allIndexes = allIndexes;
	}



	public void detectNewIndices() {
        WhirlwindConfiguration conf = getWhirlwindConfig();

        if (conf == null) {
            return;
        }

    	IndexImplementationsService implService = getIndexImplementationsService();
    	
    	if (implService != null) {
			Collection<IndexImplementation> impls = implService
					.getIndexImplementations();
			for (IndexImplementation impl : impls) {
				impl.detectIndex(this, conf);
			}
		}
		// Export of decorators will be interesting.  a toString() function will probably
        // end up checking it's thread to see what the current namespace is
        conf.exportDecorators( table.getNamespace().getClass().getName() );
		
	}
    
    public Search getSearch(SearchSpec searchSpec, boolean wantNominee) {
        WhirlwindConfiguration wwConfig = getWhirlwindConfig();
        if (wwConfig == null){
            throw new ArchException("Cannot search. No ScoreConfigs have been defined");
        }
        ScoreConfigurationManager mgr = wwConfig.getScoreConfigManager();
        IScoreConfiguration config = mgr.getConfig( searchSpec.getScorerConfig() );
        if (config == null){
            throw new ArchException("Cannot search. Undefined Scorer Config:" +  searchSpec.getScorerConfig() );
        }

        // Eliminate unneeded scorers, based on what is in searchSpec, and also create scorers where we can
        // turn a two way into a one way scorer based on the constant value in searchSpec.
        IScoreConfiguration mergedScorers = ScoreConfigOptimiser.getMergedScorers( searchSpec, config );

        // Now get the best index based on what the 'net' search is.  mergedScorers is likely to be the decider.
    	IndexImplementationsService implService = getIndexImplementationsService();

    	if (implService != null) {
			Collection<IndexImplementation> impls = implService
					.getIndexImplementations();
			Search search; // iterate over implementations to find one that can fulfil our search
			for (IndexImplementation impl : impls) {
				search = impl.getSearch(searchSpec, mergedScorers, config,
						wantNominee, this);
				if (search != null)
					return search;
			}
		}
		return new DumbOrderedSearch<T>((SearchSpecImpl) searchSpec, config, wantNominee, table);
    }



	private IndexImplementationsService getIndexImplementationsService() {
		return ServiceRegistry.getService(Database.class).getIndexImplementationsService();
	}
	
    /**
     * FIXME: Prob don't want to do this for every search .. but it's probably not a big issue.
     * We need to cache RefImpl for more than just fallback option.  All others hit the index.
     * 
     * @return
     */
    private WhirlwindConfiguration getWhirlwindConfig() {

        // Try using the cached ref to go straight to the object
        Namespace namespace = wwConfigNamespace == null ? table.getNamespace() : wwConfigNamespace;
		if (wwConfigRef != null) {
            try {
                return namespace.getObject(wwConfigRef).getObject();
            } catch (UnknownObjectException e) {
                // failed, so reset ref, and fall back (might have been deleted)
                wwConfigRef = null;
            }
        }

        // try to get the configuration for this type
        WhirlwindConfiguration conf = namespace.retrieve(WhirlwindConfiguration.class,
                WhirlwindConfiguration.CLASSNAME_FIELD, table.getClass().getName() );

        // Fall back to default if got null
        if (conf == null) {
            conf = namespace.retrieve(WhirlwindConfiguration.class,
                    WhirlwindConfiguration.CLASSNAME_FIELD, WhirlwindConfiguration.GLOBAL_NAME );
        }

        if (conf == null){
            getLog().error("No WhirlwindConfiguration found for namespace:" + namespace.toString() + ". GETTING FIRST IN TABLE");
            // Ignore trying to do anything via index, and just see if we have one
            Iterator<MetaObject<WhirlwindConfiguration>> iterator;
            try {
                iterator = namespace.retrieveAll(WhirlwindConfiguration.class);
                if (iterator.hasNext()){
                    MetaObject<WhirlwindConfiguration> metaObject = iterator.next();
                    conf = metaObject.getObject();
                    wwConfigRef = metaObject.getRef();
                }
            } catch (UnknownObjectException e) {
                conf = null;
            }
        }
        if (conf == null){
            Namespace defaultNamespace = table.getNamespace().getNamespaces().getNamespace(Store.DEFAULT_NAMESPACE);
            getLog().debug("Trying first default namespace for WhirlwindConfiguration");
            Iterator<MetaObject<WhirlwindConfiguration>> iterator;
            try {
                iterator = defaultNamespace.retrieveAll(WhirlwindConfiguration.class);
                if (iterator.hasNext()){
                    MetaObject<WhirlwindConfiguration> metaObject = iterator.next();
                    conf = metaObject.getObject();
                    wwConfigRef = metaObject.getRef();
                    wwConfigNamespace = defaultNamespace;
                }
            } catch (UnknownObjectException e) {
                conf = null;
            }
        }
        if (conf == null){
            getLog().error("No WhirlwindConfiguration found for namespace:" + namespace.toString() + ". Aborting");
            // Allows to boot database and then insert a config later.
        }
        return conf;
    }

    private Logger getLog(){
        return table.getNamespace().getLog();
    }

	public Index<T> getWWIndex(String name) {
		return allIndexes.get("@@" + name);
	}

	public UserTable<T> getTable() {
		return table;
	}

	public void addWWIndex(String name, Index<T> index){
        allIndexes.put("@@" + name , index); // Add Whirlwind Index to master map, so that testAdd/add/remove all get called
	}
	
	public Map<String, Index<T>> getIndexes(){
		return allIndexes;
	}
}
