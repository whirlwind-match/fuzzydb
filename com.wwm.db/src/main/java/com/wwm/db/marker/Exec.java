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
package com.wwm.db.marker;

import com.wwm.db.Transaction;

/**
 * Marker interface for user DB objects.
 * These objects gain a method that may be invoked remotely, both taking and returning a parameter.
 */
@Deprecated
public interface Exec {
	/**
	 * Implement this method for remote execution ('stored procedures'). 
	 * This method executes in the context of the client transaction that caused it.
	 * Comitting or rolling back the transaction is not allowed, these methods will have no effect
	 * if called from here. The ability of any writes to persist is dependant on the client sucessfully
	 * committing the associated transaction.
	 * @param t
	 * @param param
	 * @return
	 */
	public Object execute(Transaction t, Object param);
}
