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

import com.wwm.db.exceptions.UnknownObjectException;

public interface Helper {
	
	/**Get the database Ref for the specified Object.
	 * The Ref for an object never changes so this method works the same ragardless of where it is called.
	 * Throws an exception if the Object was not retrieved (or created) on the database.
	 * @param obj
	 * @return a Ref for the Object
	 * @throws UnknownObjectException
	 */
	<E> Ref getRef(E obj) throws UnknownObjectException;
	
	/**Gets the version number of the specified object.
	 * This returns the version of the presented object, not the version of the newest variant in the database.
	 * The version is unknown if the object has not been added to the database, or it is a clone of a database object. An exception is thrown in this case.
	 * The version is 0 if the object has been passed to a create method before commit.
	 * The version is 1 after a successful commit.
	 * The version is 1 if the object is retrieved from the database.
	 * The version stays as 1 after a call to an update method.
	 * The version is 2 after the object has been sucessfully updated by a successful commit.
	 * @param obj
	 * @return the version number
	 * @throws UnknownObjectException
	 */
	int getVersion(Object obj) throws UnknownObjectException;

}
