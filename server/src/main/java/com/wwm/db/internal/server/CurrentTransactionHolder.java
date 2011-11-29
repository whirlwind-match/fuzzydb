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
package com.wwm.db.internal.server;

import com.wwm.db.internal.server.ServerTransaction.Mode;

/**
 * Provides access to the current transaction associated with the running thread
 */
public class CurrentTransactionHolder {

	static private ThreadLocal<TransactionControl> transaction = new ThreadLocal<TransactionControl>();

	public static boolean isInCommitPhase() {
		return transaction.get().isInCommitPhase();
	}

	public static long getCommitVersion() {
		Long dbversion = transaction.get().getCommitVersion();
		assert(dbversion != null);
		return dbversion;
	}

	public static long getVisibleVersion() {
		long dbversion = transaction.get().getVisibleVersion();
		return dbversion;
	}

	public static void setTransactionMode(Mode mode) {
		transaction.get().setMode(mode);
	}

	static public TransactionControl getTransaction() {
		return transaction.get();
	}

	/**
	 * Set the transaction for the current thread
	 */
	static public void setTransaction(TransactionControl t) {
		transaction.set(t);
	}

}
