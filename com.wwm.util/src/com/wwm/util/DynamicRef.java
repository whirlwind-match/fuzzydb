/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.util;

/**
 * A simple wrapper to allow objects that share a resource to conveniently get the latest
 * version of that resource.
 * 
 * @param <T>
 */
public class DynamicRef<T> {
	
	volatile private T object;
	
	public T getObject() {
		return object;
	}
	
	public synchronized void setObject(T object) {
		this.object = object;
	}
}
