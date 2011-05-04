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
package com.wwm.db.internal;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.exceptions.NotImplementedException;
import com.wwm.db.internal.comms.messages.QueryCmd;
import com.wwm.db.internal.comms.messages.QueryFetchCmd;
import com.wwm.db.internal.comms.messages.QueryRsp;
import com.wwm.db.query.ResultIterator;
import com.wwm.expressions.LogicExpr;
import com.wwm.io.core.messages.Command;

public class ResultIteratorImpl<E> implements ResultIterator<E> {

	private ArrayList<Object> results;
	private boolean moreResults;
	private boolean active = false;
	
	private final ResultSetImpl<E> parent;
	private final TransactionImpl transaction;
	private final Class<E> clazz;
	private final LogicExpr index;
	private final LogicExpr expr;
	private final int fetchSize;
	private final String namespace;
	private boolean disposed = false;
	private int qid;

	public ResultIteratorImpl(ResultSetImpl<E> parent, TransactionImpl transaction, String namespace, Class<E> clazz, LogicExpr index, LogicExpr expr, int fetchSize) {
		this.parent = parent;
		this.transaction = transaction;
		this.clazz = clazz;
		this.index = index;
		this.expr = expr;
		this.namespace = namespace;
		this.fetchSize = fetchSize;

	}

	@Override
	protected void finalize()
	{
		dispose();
	}
	
	private void activate()
	{
		if (active) return;
		StoreImpl store = transaction.getStore();
		active = true;
		int cid = store.getNextId();
		qid = store.getNextId();
		Command cmd = new QueryCmd(store.getStoreId(), namespace, cid, transaction.getTid(), qid, clazz, index, expr, fetchSize);
		
		QueryRsp rsp = (QueryRsp) transaction.execute(cmd);

		results = rsp.getResults();
		moreResults = rsp.isMoreResults();
	}
	
	public long count() {
		throw new NotImplementedException();
	}

	public synchronized void dispose() {
		// TODO Complete this by telling server about it
		if (disposed) return;
		parent.iteratorDisposed(this);
		disposed = true;
		throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
		try {
			activate();
		} catch (ArchException e) {
			return false;
		}
		return results.size() > 0 || moreResults;
	}

	@SuppressWarnings("unchecked")
	public E next() {
		try {
			activate();
			if (results.size()==0) {
				if (!moreResults) {
					throw new NoSuchElementException();
				} else {
					StoreImpl store = transaction.getStore();
					Command cmd = new QueryFetchCmd(store.getStoreId(), store.getNextId(), transaction.getTid(), qid);
					QueryRsp rsp = (QueryRsp) transaction.execute(cmd);
					results = rsp.getResults();
					moreResults = rsp.isMoreResults();
				}
			}
			MetaObject<E> mo = (MetaObject<E>)results.remove(0);
			transaction.getStore().addToMetaCache(mo);
			return mo.getObject();
		} catch (ArchException e) {
			throw new NoSuchElementException(e.toString());
		}			
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
