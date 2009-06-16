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

import com.wwm.model.dimensions.IDimensions;



/**
 * A box defined by 2 objects that implment IPoint3D
 * @author ac
 */
class DimensionsRange implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3760559793400001072L;
	private IDimensions min;
	private IDimensions max;

    /**
	 * @return Returns the max.
	 */
	public IDimensions getMax() {
		return max;
	}
	/**
	 * @return Returns the min.
	 */
	public IDimensions getMin() {
		return min;
	}
	/**
	 * @param min
	 * @param max
	 */
	public DimensionsRange(IDimensions min, IDimensions max) {
		super();
		this.min = min;
		this.max = max;
		assert(min.getDimension(0) <= max.getDimension(0) ); // Assert sensible for first dimension... hope to catch the rest.
		assert(min.getClass() == max.getClass());
	}
	
	public DimensionsRange(DimensionsRange clonee) {
		super();
		try {
		    this.min = (IDimensions) clonee.min.clone();
		    this.max = (IDimensions) clonee.max.clone();
		}
		catch( CloneNotSupportedException e ) {
		    assert false;
		    e.printStackTrace();
		}
	}
	/**
	 * Check if val is in this range.
	 * @param val
	 * @return true if min <= val < max
	 */
	public boolean contains(IDimensions point) {
	
	    // not any more assert ( point.getClass() == min.getClass() ); // Want them to be the same for now

	    for (int i = 0; i < point.getNumDimensions(); i++) {
            if ( min.getDimension(i) > point.getDimension(i) 
                    || point.getDimension(i) >= max.getDimension(i) ) {
                return false;
            }
        }
	    return true;
	}
	

	public boolean contains(float[] floats, int offset) {
	    for (int i = 0; i < min.getNumDimensions(); i++) {
            if ( min.getDimension(i) > floats[offset + i] 
                    || floats[offset + i] >= max.getDimension(i) ) {
                return false;
            }
        }
	    return true;
	}
	
	
	@Override
	public String toString() {
	    return min + " - " + max;
	}

	/**
     * @param attrId
     */
    public String toString( int attrId ) {
	    return min.toString(attrId) + " - " + max.toString(attrId);
    }
    
	public boolean expand(IDimensions val) {
		boolean down = min.expandDown(val);
		boolean up = max.expandUp(val);
		return up || down;
	}
	
	public boolean equals(DimensionsRange rhs) {
		return this.min.equals(rhs.min) && this.max.equals(rhs.max);
	}
	
	@Override
	public int hashCode() {
		return min.hashCode() + max.hashCode();
	}
	
	@Override
	public Object clone() {
		return new DimensionsRange(this);
	}
	public boolean canExpand(IDimensions value) {
		boolean down = min.canExpandDown(value);
		boolean up = max.canExpandUp(value);
		return up || down;
	}
}
