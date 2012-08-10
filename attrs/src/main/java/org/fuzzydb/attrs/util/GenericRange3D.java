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

public class GenericRange3D {

    private GenericPoint3D min;
	private GenericPoint3D max;

	/**
	 * @return Returns the max.
	 */
	public GenericPoint3D getMax() {
		return max;
	}
	/**
	 * @return Returns the min.
	 */
	public GenericPoint3D getMin() {
		return min;
	}
	/**
	 * @param min
	 * @param max
	 */
	public GenericRange3D(GenericPoint3D min, GenericPoint3D max) {
		super();
		this.min = min;
		this.max = max;
		assert(min.x.compareTo(max.x) <= 0);
		assert(min.y.compareTo(max.y) <= 0);
		assert(min.z.compareTo(max.z) <= 0);
	}
	
	/**
	 * Check if val is in this range.
	 * @param val
	 * @return true if min <= val < max
	 */
	public boolean contains(GenericPoint3D point) {
	
		return (point.x.compareTo(min.x) >= 0 && point.x.compareTo(max.x) < 0)
			&& (point.y.compareTo(min.y) >= 0 && point.y.compareTo(max.y) < 0)
			&& (point.z.compareTo(min.z) >= 0 && point.z.compareTo(max.z) < 0);
	}
	
	
	@Override
	public String toString() {
	    return min + " - " + max;
	}
}
