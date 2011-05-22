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

import java.util.HashSet;

import com.wwm.db.internal.pager.Page.PagePurgedException;


/**
 * Defines support required by {@linkplain FileSerializingPagePersister} to allow the data of a large object, such as a database table,
 * to be paged in and out from a persistent storage mechanism such as disk or remote memory.
 */
public interface PersistentPagedObject {

	/**
	 * Save, but don't purge a page
	 * @param pageId
	 */
	public abstract void savePage(Long pageId) throws PagePurgedException;

	/**
	 * Try to purge the specified page for this table.  Calling code should check the return value depending on whether it is expecting
	 * this page to be in memory (i.e. when saving all, we can expect that some pages have already been flushed to disk, and we don't need them back)
	 * 
	 * @param pageId
	 * @return true if it existed and was purged successfully, false if it does not exist in memory, or failed to purge.
	 */
	public abstract boolean tryPurgePage(long pageId) throws PagePurgedException;

	public abstract boolean flushOldVersions(HashSet<Long> pageIds) throws PagePurgedException;

	public float calculatePurgeCost(long pageId);

	/**
	 * Permanently delete this object and all it's pages
	 * @return true if it succeeded
	 */
	public abstract boolean deleteFromStorage();

}