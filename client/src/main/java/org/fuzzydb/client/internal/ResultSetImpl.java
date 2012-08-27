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

import java.util.HashSet;

import org.fuzzydb.core.query.ResultIterator;
import org.fuzzydb.core.query.ResultSet;
import org.fuzzydb.expressions.LogicExpr;


public class ResultSetImpl <E> implements ResultSet<E> {

	private final TransactionImpl transaction;
	private final Class<E> clazz;
	private final LogicExpr index;
	private final LogicExpr expr;
	private final int fetchSize;
	private final String namespace;

	private HashSet<ResultIteratorImpl<E>> iterators = new HashSet<ResultIteratorImpl<E>>();

	private boolean disposed = false;
	
	public ResultSetImpl(TransactionImpl transaction, Class<E> clazz, LogicExpr index, LogicExpr expr, int fetchSize) {
		this.transaction = transaction;
		this.clazz = clazz;
		this.index = index;
		this.expr = expr;
		this.fetchSize = fetchSize;
		this.namespace = transaction.getNamespace();
	}

	public synchronized void dispose() {
		while (!iterators.isEmpty()) {
			iterators.iterator().next().dispose();	// dispose first one
		}
		disposed = true;
	}

	public synchronized ResultIterator<E> iterator() {
		assert(!disposed);
		ResultIteratorImpl<E> ri = new ResultIteratorImpl<E>(this, transaction, namespace, clazz, index, expr, fetchSize);
		iterators.add(ri);
		return ri;
	}
	
	synchronized void iteratorDisposed(ResultIteratorImpl<E> ri) {
		iterators.remove(ri);
	}
	
	synchronized boolean isDisposed() {
		return disposed;
	}

}
