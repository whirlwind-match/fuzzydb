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

public interface IPoint3D extends IDimensions {

    /** @return Returns the x value */
    public float getX();

    /** @return Returns the y value */
    public float getY();

    /** @return Returns the z value */
    public float getZ();

    /** @param x The x to set. */
    public void setX(float x);

    /** @param y The y to set. */
    public void setY(float y);

    /** @param z The z to set. */
    public void setZ(float z);

    public boolean equals(IPoint3D rhs);
    
//    public IPoint3D clone() throws CloneNotSupportedException;
}