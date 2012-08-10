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
package com.wwm.db.internal.table;

import java.io.Serializable;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.exceptions.UnknownObjectException;

import com.wwm.db.internal.common.InitializingBean;
import com.wwm.db.internal.common.YoofRepository;
import com.wwm.db.internal.server.Namespace;

/**
 * An interface that defines access to a table based on RefImpl<T> which is used to 
 * access an object of type <T>.
 * 
 * Implementations can be database transaction-aware... or we could have non-transactional
 * implementations that can always see the latest version.  This approach could be used where
 * data is entirely independent of other aspects of the database state, in the same way as
 * if they were stored in another database.  
 * 
 * @param <T> whatever is being stored.  In many cases it is VersionedObject<RT>
 * @param <RT>
 */ 
public interface Table<RT, T> extends Iterable<RefObjectPair<RT,T>>, Serializable, YoofRepository<RT, T>, InitializingBean {

	/**
	 * Gets an object without performing any IO. If the object is not available, returns null. The exception may or may
	 * not be thrown if the object does not exist. The version of the object is determined by the current transaction.
	 * The returned object is shared and must not be modified in any way.
	 * 
	 * @param ref
	 *            The ref of the object to get, or null if an IO operation would be required
	 * @return The object, or null if an IO operation would be required, or the object does not exist
	 * @throws UnknownObjectException
	 *             if it can be determined that the object does not exist without causing any IO
	 */
	T getObjectNonIO(Ref<RT> ref) throws UnknownObjectException;

	int getTableId();

	/**
	 * Pemanently this Table. If this table has an on disk structure, then any associated files are also deleted.
	 * 
	 * @return
	 */
	boolean deletePersistentData();

	/**
	 * Safely determines if the specified object exists for the current transaction. The object may have been deleted by
	 * a newer transaction, but this method will still return true.
	 * 
	 * @param ref
	 * @return
	 */
	boolean doesElementExist(Ref<RT> ref);

	/**
	 * Safely determines if the latest version of the specified object is visible to the current transaction. A
	 * successful call if this method is a prerequesite for update and delete operations.
	 * 
	 * @param ref
	 * @return
	 */
	boolean canSeeLatest(Ref<RT> ref) throws UnknownObjectException;

	Namespace getNamespace();

	/**
	 * Get the class of what is stored in this table
	 * @return Class
	 */
	Class<?> getStoredClass();

	long getElementCount();
	
}
