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


public abstract class Value2D extends Attribute {
	/**
	 * @return Returns the val.
	 */
	public Point2D getValue() {
		return val;
	}
	/**
	 * @param val
	 */
	public Value2D(int attrId, Point2D value) {
		super(attrId);
		this.val = value;
	}
	private Point2D val;
}
