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
package com.wwm.attrs.simple;


import com.wwm.attrs.internal.BranchConstraint;
import com.wwm.db.whirlwind.internal.IAttribute;


public class FloatConstraint extends BranchConstraint /* implements IRange */ {
	
	private static final long serialVersionUID = 3833744374088347700L;
	protected float min;
	protected float max;

	
	public FloatConstraint(){
		super(0);
		min = -Float.MAX_VALUE;
		max = Float.MAX_VALUE;
	}
	
	
	/**
	 * @return Returns the max.
	 */
	public final float getMax() {
		return max;
	}
	
	
	/**
	 * @return Returns the min.
	 */
	public final float getMin() {
		return min;
	}

    
	/**
     * @param attrId - attribute id of the attribute that we are constraining
     * @param min
     * @param max
     */
	public FloatConstraint(int attrId, float min, float max) {
		super(attrId);
		this.min = min;
		this.max = max;
	}
	
	
	public FloatConstraint(FloatConstraint clonee) {
		super(clonee);
		this.min = clonee.min;
		this.max = clonee.max;
	}


	public FloatConstraint(int attrId, float min, float max, boolean nullFlag) {
        super(attrId, nullFlag);
        this.min = min;
        this.max = max;  
    }


    public final boolean contains (float val) {
		return ( min <= val && val < max );
	}
	
	
	public final boolean contains (float min, float max) {
		return contains(min) && contains(max);
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.BranchConstraint#consistent(likemynds.db.indextree.attributes.Have)
	 */
	@Override
	public final boolean consistent (IAttribute val) {
		if (val == null){
			return isIncludesNotSpecified();
		}
		return contains( ((IFloat) val).getValue() );
	}

	
	/**
	 * TODO: Change to lookup a Translator for this ID (Will need to differentiate between haves and wants)
	 */
    @Override
	public String toString(){
        return new FloatHave(attrId, getMin() ).toString() + "-" 
        + new FloatHave(attrId, getMax() ).toString(); 
    }


	@Override
	protected boolean expandNonNull(IAttribute value) {
		boolean expanded = false;
		float v = ((FloatHave) value).getValue();
		if (v < min) {
			min = v;
			expanded = true;
		}
		if (v > max) {
			max = v;
			expanded = true;
		}
		return expanded;
	}

	@Override
	public boolean equals(Object rhs) {
        if (!(rhs instanceof FloatConstraint)) {
            return false;
        }
        FloatConstraint val = (FloatConstraint) rhs;
		return super.equals(val) &&
        this.min == val.min && this.max == val.max;
	}

	@Override
	public FloatConstraint clone() {
		return new FloatConstraint(this);
	}

	@Override
	public boolean isExpandedByNonNull(IAttribute value) {
		boolean rval = false;
		float v = ((FloatHave) value).getValue();
		if (min > v) {
			rval = true;
		}
		if (max < v) {
			rval = true;
		}
		return rval;
	}

}
