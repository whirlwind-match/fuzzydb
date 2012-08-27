package org.fuzzydb.server.internal.index;

import java.util.concurrent.Callable;

import org.fuzzydb.server.internal.server.CurrentTransactionHolder;
import org.fuzzydb.server.internal.server.TransactionControl;


public abstract class TransactionPropagatingCallable<S> implements Callable<S> {

	private TransactionControl transaction;

	public TransactionPropagatingCallable() {
		this.transaction = CurrentTransactionHolder.getTransaction();
	}

	public final S call() throws Exception {
		CurrentTransactionHolder.setTransaction(transaction);
		return callInternal();
	}

	abstract protected S callInternal() throws Exception;

}
