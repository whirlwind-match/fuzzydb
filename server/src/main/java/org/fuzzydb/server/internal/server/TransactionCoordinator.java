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

public interface TransactionCoordinator {
	
	/**The transaction is about to begin writing. Only one transaction may be writing at a time,
	 * so this method acts as a synchronization mechanism. This method blocks until previous
	 * threads have finished writing.
	 * Calls to this method must be matched with calls to the releaseWritePrivilege() method.
	 * @param thread
	 * @return The version number the database will be after the write.
	 */
	public long acquireWritePrivilege();
	
	
	/**The transaction has finished committing. This method must be called after a call to the acquireWritePrivilege() method.
	 * The version of the database will be incremented by 1 by this method.
	 * @param thread
	 */
	public void releaseWritePrivilege();
}
