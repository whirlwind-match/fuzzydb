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

import java.io.Serializable;

import org.fuzzydb.dto.dimensions.IPoint3D;



/**
 * A box defined by 2 objects that implement IPoint3D
 */
public class Range3D implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3763093068256982070L;
	private IPoint3D min;
	private IPoint3D max;

    /**
	 * @return Returns the max.
	 */
	public IPoint3D getMax() {
		return max;
	}
	/**
	 * @return Returns the min.
	 */
	public IPoint3D getMin() {
		return min;
	}
	/**
	 * @param min
	 * @param max
	 */
	public Range3D(IPoint3D min, IPoint3D max) {
		super();
		this.min = min;
		this.max = max;
		assert(min.getX() <= max.getX() );
		assert(min.getY() <= max.getY() );
		assert(min.getZ() <= max.getZ() );
	}
	
	/**
	 * Check if val is in this range.
	 * @param val
	 * @return true if min <= val < max
	 */
	public boolean contains(IPoint3D point) {
	
		return min.getX() <= point.getX() && point.getX() < max.getX()
			&& min.getY() <= point.getY() && point.getY() < max.getY()
			&& min.getZ() <= point.getZ() && point.getZ() < max.getZ();
	}
	
	@Override
	public String toString() {
	    return min + " - " + max;
	}
	public boolean expand(IPoint3D point3D) {
		boolean rval = false;
		if (min.getX() > point3D.getX()) {
			min.setX(point3D.getX());
			rval = true;
		}
		if (min.getY() > point3D.getY()) {
			min.setY(point3D.getY());
			rval = true;
		}
		if (min.getZ() > point3D.getZ()) {
			min.setZ(point3D.getZ());
			rval = true;
		}
		if (max.getX() < point3D.getX()) {
			max.setX(point3D.getX());
			rval = true;
		}
		if (max.getY() < point3D.getY()) {
			max.setY(point3D.getY());
			rval = true;
		}
		if (max.getZ() < point3D.getZ()) {
			max.setZ(point3D.getZ());
			rval = true;
		}
		return rval;
	}
	
	@Override
	public boolean equals(Object o) {
		Range3D rhs = (Range3D) o;
		return min.equals(rhs.min) && max.equals(rhs.max); 
	}
	
	@Override
	public int hashCode() {
		return min.hashCode() + max.hashCode();
	}
	
	public boolean canExpand(IPoint3D point3D) {
		if (min.getX() > point3D.getX()) {
			return true;
		}
		if (min.getY() > point3D.getY()) {
			return true;
		}
		if (min.getZ() > point3D.getZ()) {
			return true;
		}
		if (max.getX() < point3D.getX()) {
			return true;
		}
		if (max.getY() < point3D.getY()) {
			return true;
		}
		if (max.getZ() < point3D.getZ()) {
			return true;
		}
		return false;
	}
}
