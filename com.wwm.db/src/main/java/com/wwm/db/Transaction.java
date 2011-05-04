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
	
	//public void rollback();
	public void commit() throws ArchException;
	/**Immediately marks this transaction as unusable. Further API calls will fail. The resources associated with this
	 * transaction will be released quickly rather than taking time to time out.
	 * Any queries and searches associated with this transaction are also disposed.
	 */
	public void dispose();
	
	public Store getStore();
	
	//public boolean canCommit();
	
	/**Forces the transaction to start immediately. The transaction is guaranteed to have been created on the server when
	 * this method returns.
	 * Useful for unit testing to create overlapping transactions, as otherwise transaction start time is undefined.
	 * @throws ArchException
	 */
	public void forceStart() throws ArchException;
	
		
//	This has been removed becuase its useless. Even if we could support switching from a non-auth to an auth transaction, the database view (version) would be the same so it would have no effect.	
//	/**Returns an Authoritative version of this Transaction.
//	 * This is guaranteed to be a low cost operation, the intended use is for applications to toggle between authoritative and non-authoritative Transaction views with this function.
//	 * If this Transaction is already Authoritative, it safely returns a reference to this Transaction.
//	 * @return the Authoritative store view.
//	 */
//	public Store getAuthTransaction();
//	
//	/**Returns a Non-Authoritative version of this Transaction.
//	 * This is guaranteed to be a low cost operation, the intended use is for applications to toggle between authoritative and non-authoritative Transaction views with this function.
//	 * If this Transaction is already Non-Authoritative, it safely returns a reference to this Transaction.
//	 * @return the Non-Authoritative store view.
//	 */
//	public Store getNonAuthTransaction();
	
		
}
