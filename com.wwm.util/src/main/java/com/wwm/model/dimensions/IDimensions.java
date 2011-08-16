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
package com.wwm.model.dimensions;

import java.io.Serializable;

/**
 * @author Neale
 */
public interface IDimensions extends Serializable {
	static public int DIMENSIONS = 0;
    /**
     * @param dimension Which dimension
     */
    public float getDimension(int dimension);

    /**
     * Get the number of dimensions this item has
     */
    public int getNumDimensions();
    
    public void setDimension(int dimension, float val);
    
    /**
     * Get string representation of this value, given the attribute ID so that it can find the Decorator.
     */
    public String toString( int attrId );

    public void setDimensionIfLower(int dimension, float val);

    public void setDimensionIfHigher(int dimension, float val);

	public boolean expandDown(IDimensions val);

	public boolean expandUp(IDimensions val);
	
	public boolean equals(IDimensions rhs);
	
	public Object clone() throws CloneNotSupportedException;

	public boolean canExpandDown(IDimensions value);

	public boolean canExpandUp(IDimensions value);
}
