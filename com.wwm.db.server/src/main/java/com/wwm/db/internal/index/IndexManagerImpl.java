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
package com.wwm.db.internal.index;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.springframework.data.annotation.Id;
import org.springframework.util.StringUtils;

import com.wwm.db.exceptions.KeyCollisionException;
import com.wwm.db.internal.MetaObject;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.search.Search;
import com.wwm.db.internal.server.Namespace;
import com.wwm.db.internal.table.UserTable;
import com.wwm.db.marker.IWhirlwindItem;
import com.wwm.db.whirlwind.SearchSpec;


/**
 * The IndexManager for a given table.
 * 
 * This is responsible for discovering and maintaining the association between a
 * UserTable and the various indexes associated with it.
 * 
 * Unlike the DBv1 implementation, it is also responsible for managing the lifecycle
 * of persistent indexes... so it is serialisable in itself.
 * 
 * It consults an OSGi service called IndexImplementationsService to find out what implementations
 * are available for indexing.
 * 
 * @author Neale
 *
 */
public class IndexManagerImpl<T> extends IndexManager<T> {

    private static final long serialVersionUID = 1L;

    /** set true when 	 */
    private boolean initialised = false;

    /** parent namespace ... for convenience */
    private Namespace namespace;

    private Map<String, Index<T>> indexes;

    private UserTable<T> table;

	private WhirlwindIndexManager<?> wwIndexMgr = null;


    /**
     * @param table - table being indexed
     * @param clazz
     */
//    @SuppressWarnings("unchecked") // Index (if anyone else can find correct notation then feel free)
    public IndexManagerImpl(UserTable<T> table, Class<T> clazz) {
        this.table = table;
        this.namespace = table.getNamespace();
        this.indexes = Collections.synchronizedMap(new TreeMap<String, Index<T>>());
    }

    /**
     * This is called after creation, and also after
     */
    @Override
    public synchronized void initialise() {
        if (!initialised){
            initIndexes();
            initialised = true;
        }

    }

    private Logger getLog(){
        return namespace.getLog();
    }

    @Override
    public boolean deletePersistentData() {
        boolean success = true;
        for (Index<T> index : indexes.values()) {
            success &= index.deletePersistentData();
        }
        return success;
    }

    @Override
    public void doMaintenance() {

    }

    @Override
    public void testAddToIndexes(MetaObject<T> mo) throws KeyCollisionException {
        for (Index<T> index : indexes.values()) {
            index.testInsert(mo.getRef(), mo.getObject());
        }
    }

    @Override
    public void addToIndexes(MetaObject<T> mo) {
        for (Index<T> index : indexes.values()) {
            index.insert(mo.getRef(), mo.getObject());
        }
    }

    @Override
    public void removeFromIndexes(MetaObject<T> mo) {
        RefImpl<T> ref = mo.getRef();
        T obj = mo.getObject();
        for (Index<T> index : indexes.values()) {
            index.remove(ref, obj);
        }
    }


    /**
     * detect any new indexes required and add them
     * then call initialise on all indexes.
     */
    private void initIndexes() {
        getLog().info(" Initialising All Indexes for '" + describeTable() + "'... ");

        // iterate over whole list calling initialise()
        // this will ensure that they are built
        for (Index<T> index : indexes.values()) {
            // the index is expected to be able to find all the data required, if it
            // needs to build itself as part of initialise call.
            index.initialise();
        }

        getLog().info(" Completed Indexes for '" + describeTable() + "' - ");
                //				+ table.size() + " item" + ((table.size()==1)?"":"s")
//                + " inserted");

    }


    /**
     * Looks at definition of clazz and all superclasses for changes to required
     * indexes for this class.
     * This currently involves inspecting annotations, but could involve looking at
     * manually configured indexes, such as indexes that span more than one class (!)
     * that have been configured manually.
     */
    @Override
	public void detectNewIndexes() {
        getLog().info(" Detecting Indexes for '" + describeTable() + "'... ");

        checkWWIndex();

        detectSimpleIndexes();
        getLog().info(" Completed Index detection for '" + describeTable() + "'... ");

    }

	private String describeTable() {
		String namespace = table.getNamespace().getName();
		if (!StringUtils.hasLength(namespace)) {
			namespace = "(default)";
		}
		return namespace + " : " + table.getStoredClass().getName();
	}

	private void detectSimpleIndexes() {
		// Detect fields on clazz and update index if not already in existence
        for (Field f: table.getStoredClass().getDeclaredFields()) {
            if ( f.isAnnotationPresent(com.wwm.db.annotations.Key.class)
            		|| f.isAnnotationPresent(Id.class)) {
                getLog().info(" - Simple Index '" + table.getStoredClass() + "$" + f.getName() + "(" + f.getType().getSimpleName() + ")'");
                //			createIndex(f);
            }
        }


        // Detect fields on base class and do namespace.getIndex( baseClass, field )

        // iterate over superclass heirarchy
        // FIXME: Adrian has this implemented elsewhere, which is why createIndex(f) above is commented out
        // ... we should integrate what he has done into this class.

        // indexes.put( key, namespace.getIndex( baseClass, field ) )
	}


    /**
     * Ensure that there is a WhirlwindIndex for each index strategy we have defined for this class.
     */
    @SuppressWarnings("unchecked")
	private void checkWWIndex() {
        // If we're not dealing with a WhirlwindItem, then we don't need to go any further
        if (!IWhirlwindItem.class.isAssignableFrom(table.getStoredClass())) {
            // We could check our class has not had wwIndex removed when previously was one.  Heck, if this
            // happens before we've got a team of 10 softies, I'll eat my (organic cotton) hat.
            return;
        }
        
        if (wwIndexMgr == null){
        	wwIndexMgr = new WhirlwindIndexManager(table, indexes);
        }
        
        wwIndexMgr.detectNewIndices();
    }


    @Override
    public Search getSearch(SearchSpec searchSpec, boolean wantNominee) {
    	return wwIndexMgr.getSearch(searchSpec, wantNominee);
    }
}
