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
package org.fuzzydb.server.internal.server;

import org.fuzzydb.server.internal.server.ServerTransaction.Mode;

public interface TransactionControl {

	void setMode(Mode mode);

	/**
	 * The version of the database when this transaction was created.
	 * Allows this transaction to see a static database.
	 */
	long getVisibleVersion();

	/**
	 * The version the database will be after this transactions commit phase.
	 * This field is null until the transaction enters commit phase.
	 * @return The vew version, or null if not in commit phase
	 */
	Long getCommitVersion();

	/**
	 * Determines if this transaction is in the commit phase.
	 * This method also determines if the getCommitVersion() method will return null.
	 * @return true if this transaction is committing. 
	 */
	boolean isInCommitPhase();
	
	
	/**
	 * Get the oldest visible database version.  Used within the database to
	 * support timely purging of stale versions.
	 */
	long getOldestDbVersion();
}