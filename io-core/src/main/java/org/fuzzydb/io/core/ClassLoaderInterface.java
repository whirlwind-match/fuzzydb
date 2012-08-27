/******************************************************************************
 * Copyright (c) 2005-2011 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.io.core;

import java.io.Serializable;

import org.fuzzydb.io.core.exceptions.ClassRepeatedException;


/**
 * REname to ClassDefinitionRepository
 * @author Neale
 *
 */
public interface ClassLoaderInterface extends Serializable {

	Class<?> getClass(int storeId, String className) throws ClassNotFoundException;
	byte[] getClassBytecode(int storeId, String className) throws ClassNotFoundException;
	void addClass(int storeId, String className, byte[] bytecode) throws ClassRepeatedException;
	void waitForClass(int storeId, String className);
}
