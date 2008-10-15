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
package com.wwm.db.internal.pager;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.wwm.db.exceptions.UnknownObjectException;

public interface ElementReadOnly<T> {

	/**
	 * Serialises this element to an ObjectStream in a reasonably efficient way
	 * 
	 * @param oos
	 * @throws IOException
	 */
	public void writeToStream(ObjectOutputStream oos) throws IOException;

	/**
	 * Determine if the transaction associated with the calling thread can see the head 
	 * of the chain. This is a prerequisite for adding new versions or deleting.
	 * 
	 * @return true if the transaction can see the head of the chain. Returns false if the 
	 * transaction is older than the head, or if the object has been deleted.
	 */
	public boolean canSeeLatest();

	/**
	 * Gets the version of the object visible to the transaction associated with the calling 
	 * thread. Returns null if the object has been deleted.
	 * 
	 * @return The version of the object applicable to the transaction
	 * @throws UnknownObjectException
	 */
	public T getVersion() throws UnknownObjectException;

	/**
	 * Determines if all live versions of the object have been deleted. Used by the page manager 
	 * when saving pages to disk, deleted elements can be omitted. flushOldVersions() should be 
	 * called first to perform the deletions.
	 * 
	 * @return true if the version chain has been completely deleted.
	 * @see flushOldVersions()
	 */
	public boolean isDeleted();

	/**
	 * Get the oid of this object.
	 * 
	 * @return the oid.
	 */
	public long getOid();

	public long getLatestVersion();

}