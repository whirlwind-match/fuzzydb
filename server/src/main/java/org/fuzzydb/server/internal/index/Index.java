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
package org.fuzzydb.server.internal.index;


import org.fuzzydb.client.exceptions.KeyCollisionException;
import org.fuzzydb.client.internal.RefImpl;


public interface Index<T> {

	/**
	 * Was 'build' in DBv1.  
	 * This should ensure that the index is valid and built.
	 * When a new index has been created on an existing table, it is responsible
	 * for iterating over all the relevant objects to initialise the index.
	 */
	void initialise();

	/**
	 * Check if we can insert this object. If we can't we throw {@link KeyCollisionException}
	 * 
	 * @throws KeyCollisionException
	 */
	public void testInsert(RefImpl<T> ref, T o) throws KeyCollisionException;

	/**
	 * Add the supplied object to the index
	 */
	void insert(RefImpl<T> ref, T o);

	/**
	 * Remove supplied object from the index
	 */
	 void remove(RefImpl<T> ref, T obj);

	 
	/**
	 * Permanently delete all persistent data being managed by this IndexManager
	 * @return true if succeeded
	 */
	boolean deletePersistentData();


	 
	
	
}
