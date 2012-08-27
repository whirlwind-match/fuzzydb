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
package org.fuzzydb.server.internal.table;


import org.fuzzydb.client.exceptions.KeyCollisionException;
import org.fuzzydb.client.exceptions.ObjectExistsException;
import org.fuzzydb.client.exceptions.UnknownObjectException;
import org.fuzzydb.client.exceptions.WriteCollisionException;
import org.fuzzydb.client.internal.MetaObject;
import org.fuzzydb.client.internal.RefImpl;
import org.fuzzydb.server.internal.server.Namespace;


public interface UserTable<T> extends Iterable<MetaObject<T>>{

	/**
	 * Initialise anything that might not have been stored, if this
	 * is a type of table that gets stored.
	 * NOTE: This could probably be dropped, and done as a lazy-init
	 * by testing a transient variable within the table.
	 */
	void initialise();

	long allocNewIds(int count);

	void create(MetaObject<T> mo);

	void update(MetaObject<T> mo) throws UnknownObjectException;

	void delete(RefImpl<T> ref) throws UnknownObjectException;

	void testCanCreate(MetaObject<T> mo) throws ObjectExistsException, KeyCollisionException;

	void testCanUpdate(MetaObject<T> mo) throws UnknownObjectException, WriteCollisionException, KeyCollisionException;

	void testCanDelete(RefImpl<T> ref) throws UnknownObjectException;

	MetaObject<T> getObject(RefImpl<T> ref) throws UnknownObjectException;

	int getTableId();
	
	public Namespace getNamespace();

	/**
	 * Permanently delete this Table.  If this table has an on disk structure,
	 * then any associated files are also deleted.
	 * @return true if succeeded
	 */
	boolean deletePersistentData();

	/**
	 * Get the class of what is stored in this table
	 * @return Class
	 */
	Class<T> getStoredClass();
	
	long getElementCount();

}