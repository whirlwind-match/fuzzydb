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
package org.fuzzydb.dto.dimensions;

import java.io.Serializable;


public interface IRange3D extends Serializable {
	public boolean contains(IPoint3D point);
	public IPoint3D getMax();
	public IPoint3D getMin();
}
