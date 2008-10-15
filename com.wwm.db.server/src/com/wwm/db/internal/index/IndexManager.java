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

import java.io.Serializable;

import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.exceptions.KeyCollisionException;
import com.wwm.db.internal.MetaObject;
import com.wwm.db.internal.search.Search;
import com.wwm.db.whirlwind.SearchSpec;



/**
 * An IndexManager is responsible for building and maintaining a set of indexes on
 * a given class of object.
 * 
 * Typically an IndexManager associated (1 to 1) with an IndexedTable.
 * 
 * A given IndexManager may be configured to manage the indexing of various items in
 * different ways, perhaps for performance reasons, perhaps because for architectural
 * reasons - one server in a cluster may not be required to index items that a
 * different server is expected to index.
 * 
 * It is possible that there will be a configurable IndexManagerFactory which
 * instantiates the appropriate index based on the server configuration.
 * 
 * @author Neale
 *
 */
public abstract class IndexManager<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Establishes what types of index are required for the supplied
	 * ClassTable (mmm perhaps this should be an abstract so we can supply the
	 * constructor), and indexes whatever instances the ClassTable has available.
	 */
	abstract public void initialise(); // For in memory.. this ensures that the index is built and available
	abstract public void detectNewIndexes();
	abstract public void doMaintenance(); // i.e. deleteExpired();

	/**
	 * Permanently delete all persistent data being managed by this IndexManager
	 * @return true if succeeded
	 */
	abstract public boolean deletePersistentData();

	abstract public void testAddToIndexes(MetaObject<T> mo) throws KeyCollisionException;
	abstract public void addToIndexes(MetaObject<T> mo);
	abstract public void removeFromIndexes(MetaObject<T> mo);
	abstract public Search getSearch(SearchSpec searchSpec, boolean wantNominee) throws ArchException;


	//	public MetaObject retrieve(String field, Sortable sortable, long dbversion);

	//	public TreeMap<Sortable, DbObjectWrapper> retrieve(String field, Set<Sortable> keys, long dbversion);
	//
	//	public Iterator<ObjectVersion> iteratorEqual(String field, Sortable sortable, long version);
	//	public Iterator<ObjectVersion> iteratorGtr(String field, Sortable sortable, long version);
	//	public Iterator<ObjectVersion> iteratorGtrEqual(String field, Sortable sortable, long version);
	//	public Iterator<ObjectVersion> iteratorLess(String field, Sortable sortable, long version);
	//	public Iterator<ObjectVersion> iteratorLessEqual(String field, Sortable sortable, long version);
	//	public Iterator<ObjectVersion> iteratorRange(Sortable low, Sortable high, String field, boolean lowInclusive, boolean hiInclusive, long version);
	//	public void rebuildAttributeCache();
	//    public Search search(SearchSpec searchSpec, DataTransaction transaction, boolean nominee);
}