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
package com.wwm.io.packet;

import java.io.Serializable;

import com.wwm.io.packet.exceptions.ClassRepeatedException;

public interface ClassLoaderInterface extends Serializable {

	public Class<?> getClass(int storeId, String className) throws ClassNotFoundException;
	public byte[] getClassBytecode(int storeId, String className) throws ClassNotFoundException;
	public void addClass(int storeId, String className, byte[] bytecode) throws ClassRepeatedException;
	public void waitForClass(int storeId, String className);
}
