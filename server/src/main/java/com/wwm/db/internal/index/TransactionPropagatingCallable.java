package com.wwm.db.internal.index;

import java.util.concurrent.Callable;

import com.wwm.db.internal.server.CurrentTransactionHolder;
import com.wwm.db.internal.server.TransactionControl;

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
