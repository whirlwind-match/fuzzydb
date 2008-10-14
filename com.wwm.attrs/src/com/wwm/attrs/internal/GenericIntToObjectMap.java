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
package com.wwm.attrs.internal;

import java.io.Serializable;

/**
 * Generic map to hide implementation or otherwise
 * @author Neale Upstone
 */
public class GenericIntToObjectMap<T> implements Serializable {

    private static final long serialVersionUID = -3873999318510285246L;

    static private final int defaultSize = 200;

    private T[] objects;  
    
    public GenericIntToObjectMap() {
    	this(defaultSize);
    }

	@SuppressWarnings("unchecked") // What we're doing is safe, as we're enforcing the interface.
	public GenericIntToObjectMap(int size) {

		 objects = (T[])new Object[size]; 
	    // NOTE: The warning given by Eclipse on the above line is an error... It says that the JVM
	    // will be checking agains Object[] when casting to T[] which is what we are asking.
	    // Warning would be appropriate if Object[] above were replaced with String[]
	 	// And: Check out the comparable line in ArrayList.  It's the same.
	}


	/**
	 * Find the object for the given integer reference
	 * @return
	 */
    public T get(int attrId) {
        return objects[attrId];
    }
    public T[] getArray() {
        return objects;
    }

	public void set( int attrId, T object ){
		ensureCapacity(attrId);
		objects[attrId] = object;
	}

	
	@SuppressWarnings("unchecked") // see above for explanation
	private void ensureCapacity(int size) {
		// ensure capacity
		if (size >= objects.length){
			T[] newObjects = (T[]) new Object[size * 11/10];
			System.arraycopy(objects, 0, newObjects, 0, objects.length);
			objects = newObjects;
		}
	}
    
    
}
