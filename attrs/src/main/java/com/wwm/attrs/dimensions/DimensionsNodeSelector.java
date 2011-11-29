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



import com.wwm.attrs.internal.BranchConstraint;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.dimensions.IDimensions;


/**
 * Partially defined branch constraint to be used only for inserting into nodes.
 * This allows a node to be specified by the volume that is defined by a given dimension being
 * either lower than or higher than a given value.
 */
public class DimensionsNodeSelector extends BranchConstraint { // implements IRange3D {

    private static final long serialVersionUID = -7309618939553766954L;
    private int splitIndex;
    
    /**
     * True indicates that points where splitIndex value >= splitVal are considered to be
     * 'in' this constraint, otherwise values < splitVal are in.
     * i.e. true means the upper bounds box, and false means the lower bounds box.
     */
    private boolean greaterThanOrEqualTo;
    private float splitVal;



	//================ CONSTRUCTORS, FINALISERS AND INITIALISERS ==================
	/**
	 * Default constructor.
	 */
	public DimensionsNodeSelector(){
	    super( 0 );
	}
	
    public DimensionsNodeSelector(DimensionsNodeSelector clonee) {
		super(clonee);
        this.splitIndex = clonee.splitIndex;
        this.greaterThanOrEqualTo = clonee.greaterThanOrEqualTo;
        this.splitVal = clonee.splitVal;
	}


    //========================= GETTERS AND SETTERS ==============================

	
	public DimensionsNodeSelector(int splitAttrId, int splitIndex, boolean greaterThanOrEqualTo, float splitVal) {
	    super(splitAttrId);
        this.splitIndex = splitIndex;
        this.greaterThanOrEqualTo = greaterThanOrEqualTo;
        this.splitVal = splitVal;
    }

    @Override
	public boolean consistent(IAttribute val) {
		if (val == null){
			return isIncludesNotSpecified();
		}

		IDimensions pointVal = (IDimensions)val;
		float value = pointVal.getDimension(splitIndex);
		if (greaterThanOrEqualTo) {
		    return value >= splitVal;
        }
        else {
            return value < splitVal;
        }
	}

	public boolean consistent(float[] floats, int offset) {
		float value = floats[offset + splitIndex];
		if (greaterThanOrEqualTo) {
		    return value >= splitVal;
        }
        else {
            return value < splitVal;
        }
	}
	
	@Override
	public DimensionsNodeSelector clone() {
        return new DimensionsNodeSelector( this );
    }

    
    @Override
	public String toString(){
        String comparison = greaterThanOrEqualTo ? " >= " : " < ";
        return "point[" + splitIndex + "]" + comparison + splitVal;
    }

	@Override
	protected boolean expandNonNull(IAttribute value) {
        throw new UnsupportedOperationException("Cannot use DimensionsNodeSelector for expand");
        //return bounds.expand(val);
	}
	
	@Override
	public boolean equals(Object rhs) {
        if (!(rhs instanceof DimensionsNodeSelector)) {
            return false;
        }
		DimensionsNodeSelector val = (DimensionsNodeSelector)rhs;
		return super.equals(val) 
        && this.splitIndex == val.splitIndex
        && this.greaterThanOrEqualTo == val.greaterThanOrEqualTo
        && this.splitVal == val.splitVal;
	}

	@Override
	public boolean isExpandedByNonNull(IAttribute value) {
        throw new UnsupportedOperationException("Cannot use DimensionsNodeSelector for expand");
	}

}
