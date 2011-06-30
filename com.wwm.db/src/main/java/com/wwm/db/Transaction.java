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
package com.wwm.db;

import com.wwm.db.core.exceptions.ArchException;

public interface Transaction extends DataOperations, Authority, Helper {
	
	/**
	 * Attempt to commit the transaction.
	 */
	void commit();
	
	/**Immediately marks this transaction as unusable. Further API calls will fail. The resources associated with this
	 * transaction will be released quickly rather than taking time to time out.
	 * Any queries and searches associated with this transaction are also disposed.
	 * On a write transaction, this is can be considered the same as 'rollback'.
	 */
	void dispose();
	
	Store getStore();
	
	//public boolean canCommit();
	
	/**Forces the transaction to start immediately. The transaction is guaranteed to have been created on the server when
	 * this method returns.
	 * Useful for unit testing to create overlapping transactions, as otherwise transaction start time is undefined.
	 * @throws ArchException
	 */
	void forceStart();
		
}
