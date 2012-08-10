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
package org.fuzzydb.attrs.util;

public class GenericPoint3D {
	public Comparable<Object> x;
	public Comparable<Object> y;
	public Comparable<Object> z;

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public GenericPoint3D(Comparable<Object> x, Comparable<Object> y, Comparable<Object> z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	
	@Override
	public String toString(){
	    return x + "," + y + "," + z;
	}
	
}
