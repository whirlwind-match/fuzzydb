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


import org.fuzzydb.attrs.dimensions.Dimensions;
import org.fuzzydb.attrs.dimensions.DimensionsRangeConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttributeConstraint;

import com.wwm.model.dimensions.IDimensions;

/**
 * As compared to a DimensionsRangeConstraint, FloatPreferenceConstraint has certain things it knows:
 * min[0(min)] <= min [2(pref)] <= min[1(max)] and the same for max.
 * This being the case, when we get a subBox (or subconstraint), we can constrain more than one dimension
 * on that split.
 * @author Neale
 */
public class FloatPreferenceConstraint extends DimensionsRangeConstraint {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3258407339697910834L;
	/**
     * 
     */
    public FloatPreferenceConstraint() {
        super();
    }

    /**
     * @param attrId
     */
    public FloatPreferenceConstraint(int attrId) {
        super(attrId);
    }

    /**
     * @param attrId
     * @param min
     * @param max
     */
    public FloatPreferenceConstraint(int attrId, IDimensions min,
            IDimensions max) {
        super(attrId, min, max);
    }

    
    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.dimensions.DimensionsRangeConstraint#getSubBoxLow(float, int)
     */
    @Override
	public IAttributeConstraint getSubBoxLow(float splitVal, int axis) {
        FloatPreferenceConstraint newBox;
        newBox = this.clone();
        
        newBox.getMax().setDimension( axis, splitVal );
        
        return newBox;
    }

    
    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.dimensions.DimensionsRangeConstraint#getSubBoxHigh(float, int)
     */
    @Override
	public IAttributeConstraint getSubBoxHigh(float splitVal, int axis) {
        FloatPreferenceConstraint newBox;
        newBox = this.clone();
        
        newBox.getMin().setDimension( axis, splitVal );
        
        return newBox;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
	public FloatPreferenceConstraint clone() {
    	// Note defensive copies, as they can get modified
        return new FloatPreferenceConstraint( attrId, new Dimensions( getMin() ), new Dimensions( getMax() ) );
    }
}    
    
