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
 * Marker interface that causes a method to be executed when an object is deleted.
 */
public interface ExpirableExec extends Expirable {
	/**
	 * This method is called on deletion of the object.
	 * The commit() and rollback() methods of this transaction have no effect.
	 * If any writes are performed, and result in a write collision, the method will
	 * be retried with a fresh instance of the object.
	 * @param t
	 */
	public void onExpiry(Transaction t);
}
