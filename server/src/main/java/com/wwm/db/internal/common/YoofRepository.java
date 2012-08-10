/******************************************************************************
 * Copyright (c) 2005-2012 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.db.internal.common;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.exceptions.UnknownObjectException;

/**
 * A repository where the items being persisted like to hang around with their friends.
 *
 * @param <RT>
 * @param <T>
 */
public interface YoofRepository<RT, T> {

	/**
	 * Allocate multiple refs. The starting ref is returned, the number allocated are as requested. The transaction must
	 * be in the commit phase and be holding the slice write lock.
	 * 
	 * @param count
	 *            The number of refs to allocate.
	 * @return the oid of the first ref.
	 */
	long allocNewIds(int count);

	/**
	 * Allocate a new RefImpl and return it. The transaction must be in the commit phase and be holding the slice write
	 * lock.
	 * 
	 * @return a new RefImpl
	 */
	Ref<RT> allocOneRef();

	/**
	 * Allocate one new Ref, but try to place it near to the supplied ref so that the cost of reading one object goes
	 * some way to mitigate the cost of reading the other. For paged tables this indicates the two objects should be on
	 * the same page, when possible. The transaction must be in the commit phase and be holding the slice write lock.
	 * 
	 * @param nearRef
	 *            the ref of the object the new ref should be close to
	 * @param ls 
	 * @return a new ref
	 */
	Ref<RT> allocOneRefNear(Ref<RT> nearRef, long[] ls);

	Ref<RT> allocOneRefNear(Ref<RT> nearRef);

	/**
	 * Create a new object. The RefImpl should have previously been allocated with one of the alloc methods. The object
	 * must not already exist otherwise an Error will be generated. The transaction must be in the commit phase and be
	 * holding the slice write lock.
	 * 
	 * @param ref
	 * @param object
	 */
	void create(Ref<RT> ref, T object);

	/**
	 * Update an existing object. The transaction must be able to see the latest version of the object otherwise an
	 * Error is generated. The transaction must be in the commit phase and be holding the slice write lock.
	 * 
	 * @param ref
	 * @param object
	 * @throws UnknownObjectException
	 */
	void update(Ref<RT> ref, T object) throws UnknownObjectException;

	/**
	 * Delete the object with the specified Ref. The transaction must be able to see the latest version of the object
	 * otherwise an Error is generated. The transaction must be in the commit phase and be holding the slice write lock.
	 * 
	 * @param ref
	 * @throws UnknownObjectException
	 */
	void delete(Ref<RT> ref) throws UnknownObjectException;

	/**
	 * Gets an object corresponding to the specified RefImpl. The version of the object is determined by the current
	 * transaction. The returned object is shared and must not be modified in any way.
	 * 
	 * @param ref
	 * @return
	 * @throws UnknownObjectException
	 */
	T getObject(Ref<RT> ref) throws UnknownObjectException;

	void createUpdate(Ref<RT> ref, T object) throws UnknownObjectException;

}