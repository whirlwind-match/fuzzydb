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
package org.fuzzydb.attrs.simple;


import org.fuzzydb.attrs.internal.BranchConstraint;
import org.fuzzydb.attrs.userobjects.IntegerAttribute;

import com.wwm.db.whirlwind.internal.IAttribute;


public class IntegerConstraint extends BranchConstraint /* implements IRange */ {
	
	private static final long serialVersionUID = 3833744374088347700L;
	protected int min;
	protected int max;

	
	public IntegerConstraint(){
		super(0);
		min = -Integer.MAX_VALUE;
		max = Integer.MAX_VALUE;
	}
	
	
	/**
	 * @return Returns the max.
	 */
	public final int getMax() {
		return max;
	}
	
	
	/**
	 * @return Returns the min.
	 */
	public final int getMin() {
		return min;
	}

    
	/**
     * @param attrId - attribute id of the attribute that we are constraining
     * @param min
     * @param max
     */
	public IntegerConstraint(int attrId, int min, int max) {
		super(attrId);
		this.min = min;
		this.max = max;
	}
	
	
	public IntegerConstraint(IntegerConstraint clonee) {
		super(clonee);
		this.min = clonee.min;
		this.max = clonee.max;
	}


	public final boolean contains (int val) {
		return ( min <= val && val < max );
	}
	
	
	public final boolean contains (int min, int max) {
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
		return contains( ((IntegerAttribute) val).getValue() );
	}

	
	/**
	 * TODO: Change to lookup a Translator for this ID (Will need to differentiate between haves and wants)
	 */
    @Override
	public String toString(){
        return new FloatValue(attrId, getMin() ).toString() + "-" 
        + new FloatValue(attrId, getMax() ).toString(); 
    }


	@Override
	protected boolean expandNonNull(IAttribute value) {
		boolean rval = false;
		int v = ((IntegerAttribute) value).getValue();
		if (min > v) {
			min = v;
			rval = true;
		}
		if (max < v) {
			max = v;
			rval = true;
		}
		return rval;
	}

	@Override
	public boolean equals(Object rhs) {
        if (!(rhs instanceof IntegerConstraint)) {
            return false;
        }
		IntegerConstraint val = (IntegerConstraint) rhs;
		return super.equals(val) &&
        min == val.min && this.max == val.max;
	}

	@Override
	public IntegerConstraint clone() {
		return new IntegerConstraint(this);
	}

	@Override
	public boolean isExpandedByNonNull(IAttribute value) {
		boolean rval = false;
		int v = ((IntegerAttribute) value).getValue();
		if (min > v) {
			rval = true;
		}
		if (max < v) {
			rval = true;
		}
		return rval;
	}

}
