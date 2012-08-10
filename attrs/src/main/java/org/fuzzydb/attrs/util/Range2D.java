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


public class Range2D {
	private Point2D min;
	private Point2D max;
	/**
	 * @param min
	 * @param max
	 */
	public Range2D(Point2D min, Point2D max) {
		super();
		this.min = min;
		this.max = max;
		assert(min.x.compareTo(max.x) <= 0);
		assert(min.y.compareTo(max.y) <= 0);
	}
	
	/**
	 * Check if val is in this range.
	 * @param val
	 * @return true if min <= val < max
	 */
	public boolean contains(Point2D point) {
		return (point.x.compareTo(min.x) >= 0 && point.x.compareTo(max.x) < 0)
			&& (point.y.compareTo(min.y) >= 0 && point.y.compareTo(max.y) < 0);
	}
	/**
	 * @return Returns the max.
	 */
	public Point2D getMax() {
		return max;
	}
	/**
	 * @return Returns the min.
	 */
	public Point2D getMin() {
		return min;
	}
}
