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
package org.fuzzydb.client.internal;

import org.fuzzydb.core.query.Result;
import org.fuzzydb.core.query.ResultIterator;
import org.fuzzydb.core.query.ResultSet;
import org.fuzzydb.core.whirlwind.SearchSpec;


class WWResultSet <T extends Object> implements ResultSet<Result<T>> {

	private Class<T> clazz;	// result class
	private SearchSpec search;
	private int tid;
	private TransactionImpl transaction;	// FIXME (nu->ac) (Old comment - is it correct for DBv2) Not needed for access, but it should be held alive to stop it being finalized while a result set is in use
	private int fetchSize;
	private boolean wantNominee;
	private boolean disposed = false;
	private StoreImpl store;
	
	public WWResultSet(Class<T> clazz, StoreImpl store, TransactionImpl transaction, int tid, SearchSpec search, int fetchSize, boolean wantNominee) {
		this.clazz = clazz;
		this.store = store;
		this.tid = tid;
		this.search = search;
		this.transaction = transaction;
		this.fetchSize = fetchSize;
		this.wantNominee = wantNominee;
	}
	
	public ResultIterator<Result<T>> iterator() {
		throwIfDisposed();
		return new WWResultSetIterator<T>(this, clazz, store, transaction, tid, search, fetchSize, wantNominee);
	}

	public void dispose() {
		disposed  = true;
	}
	
	void throwIfDisposed() {
		if (disposed) {
			throw new RuntimeException("Query used after dispose()");
		}
	}	
}
