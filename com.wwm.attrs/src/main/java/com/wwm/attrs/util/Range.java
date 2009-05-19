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
package com.wwm.attrs.util;

/**
 * An Range is between two attributes
 * @author Neale
 */

public class Range {

	private Comparable<Object> min;
	private Comparable<Object> max;

	/**
	 * @param min
	 * @param max
	 */
	public Range(Comparable<Object> min,
			Comparable<Object> max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Check if val is in this range.
	 * @param val
	 * @return true if min <= val < max
	 */
	public boolean contains(Comparable<Object> val) {

		return (val.compareTo(min) >= 0 && val.compareTo(max) < 0);
	}

	/**
	 * @return Returns the dobMax.
	 */
	public Comparable<Object> getMax() {
		return max;
	}

	/**
	 * @return Returns the dobMin.
	 */
	public Comparable<Object> getMin() {
		return min;
	}

	
	@Override
	public String toString() {
	    return "[" + min + " - " + max + "]";
	}

	/**
	 * Static version that compiler stands chance of inlining :O)
	 * Check if val is in this range.
	 * @param val
	 * @return true if min <= val < max
	 */
	public static final boolean contains(Comparable<Object> min, Comparable<Object> val, Comparable<Object> max) {

		return (val.compareTo(min) >= 0 && val.compareTo(max) < 0);
	}
	
	
}
