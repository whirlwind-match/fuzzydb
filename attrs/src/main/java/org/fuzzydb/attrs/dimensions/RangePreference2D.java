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
package org.fuzzydb.attrs.dimensions;

import org.fuzzydb.attrs.internal.Attribute;
import org.fuzzydb.attrs.util.Point2D;
import org.fuzzydb.attrs.util.Range2D;


public abstract class RangePreference2D extends Attribute implements IRange2D {
	/**
	 * @return Returns the max.
	 */
	public Point2D getMax() {
		return range2d.getMax();
	}
	/**
	 * @return Returns the min.
	 */
	public Point2D getMin() {
		return range2d.getMin();
	}

	/**
	 * @param min
	 * @param max
	 */
	public RangePreference2D(int attrId, Point2D min, Point2D max) {
		super( attrId );
		range2d = new Range2D (min, max);
	}
	public boolean contains(Point2D point) {
		return range2d.contains(point);
	}
	private Range2D range2d;
}
