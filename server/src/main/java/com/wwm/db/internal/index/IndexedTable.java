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
import java.util.Iterator;

import com.wwm.db.exceptions.KeyCollisionException;
import com.wwm.db.exceptions.ObjectExistsException;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.exceptions.WriteCollisionException;
import com.wwm.db.internal.MetaObject;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.common.RuntimeContext;
import com.wwm.db.internal.search.Search;
import com.wwm.db.internal.server.CurrentTransactionHolder;
import com.wwm.db.internal.server.Namespace;
import com.wwm.db.internal.server.ServerTransaction.Mode;
import com.wwm.db.internal.table.UserTable;
import com.wwm.db.whirlwind.SearchSpec;


/**
 *
 * @param <T> The class of object being stored. e.g. SyncedAttrDefinitionMgr, TestWhirlwindItem 
 */
public class IndexedTable<T> implements UserTable<T>, Serializable {

	private static final long serialVersionUID = 1L;
	private final UserTable<T> table;
	private final IndexManager<T> indexManager;


	public IndexedTable(UserTable<T> table, IndexManager<T> indexManager) {
		this.table = table;
		this.indexManager = indexManager;
	}


	public void initialise( RuntimeContext context) {
		table.initialise(context);
		indexManager.initialise();
	}

	public boolean deletePersistentData() {
		return table.deletePersistentData()
		&& indexManager.deletePersistentData();
	}

	public int getTableId() {
		return table.getTableId();
	}

	public void testCanCreate(MetaObject<T> mo) throws ObjectExistsException, KeyCollisionException {
		indexManager.testAddToIndexes(mo);
		table.testCanCreate(mo);
	}

	public void testCanUpdate(MetaObject<T> mo) throws UnknownObjectException, WriteCollisionException, KeyCollisionException {
		indexManager.testAddToIndexes(mo); // Throw a collection of colliding objects so we can test against objects being deleted in same transaction
		table.testCanUpdate(mo);
	}

	public void testCanDelete(RefImpl<T> ref) throws UnknownObjectException {
		table.testCanDelete(ref);
	}

	public long allocNewIds(int count) {
		return table.allocNewIds(count);
	}


	public void create(MetaObject<T> mo) {
		table.create(mo);
		CurrentTransactionHolder.setTransactionMode(Mode.IndexWrite);
		indexManager.addToIndexes(mo);
		CurrentTransactionHolder.setTransactionMode(Mode.Normal);
	}

	public void update(MetaObject<T> mo) throws UnknownObjectException {
		table.update(mo);
		MetaObject<T> older = table.getObject(mo.getRef()); // yes, this is the old one we can see
		CurrentTransactionHolder.setTransactionMode(Mode.IndexWrite);
		indexManager.removeFromIndexes(older);
		indexManager.addToIndexes(mo);
		CurrentTransactionHolder.setTransactionMode(Mode.Normal);

	}

	public void delete(RefImpl<T> ref) throws UnknownObjectException {
		table.delete(ref);
		MetaObject<T> older = table.getObject(ref); // this is the old version
		CurrentTransactionHolder.setTransactionMode(Mode.IndexWrite);
		indexManager.removeFromIndexes(older);
		CurrentTransactionHolder.setTransactionMode(Mode.Normal);
	}


	public MetaObject<T> getObject(RefImpl<T> ref) throws UnknownObjectException {
		return table.getObject(ref);
	}


	public Namespace getNamespace() {
		return table.getNamespace();
	}


	public Iterator<MetaObject<T>> iterator() {
		return table.iterator();
	}

	public Class<T> getStoredClass() {
		return table.getStoredClass();
	}


	public Search getSearch(SearchSpec searchSpec, boolean wantNominee) {
		return indexManager.getSearch( searchSpec, wantNominee );
	}


	public long getElementCount() {
		return table.getElementCount();
	}
}
