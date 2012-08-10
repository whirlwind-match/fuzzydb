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
package org.fuzzydb.core.dao;


public interface SimpleDAO {

	void begin();

	/**
	 * 
	 * @param object
	 * @param key
	 * @return ref - reference to be used when updating
	 */
	Object create( Object object, Object key );

	/**
	 * Update object into database against Db's reference (ref)
	 * @param object
	 * @param ref
	 */
	void update(Object object, Object ref);

	<T> T retrieve(Class<T> clazz, Object key);

	void commit() throws DaoWriteCollisionException;

	

}
