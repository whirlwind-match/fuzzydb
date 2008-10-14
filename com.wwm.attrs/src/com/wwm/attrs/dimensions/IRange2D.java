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
package com.wwm.attrs.dimensions;

import java.io.Serializable;

import com.wwm.attrs.util.Point2D;



public interface IRange2D extends Serializable {
	public boolean contains(Point2D point);
	public Point2D getMax();
	public Point2D getMin();
	
}
