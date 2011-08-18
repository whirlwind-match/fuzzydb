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


import com.wwm.attrs.dimensions.Dimensions;
import com.wwm.attrs.dimensions.DimensionsRangeConstraint;
import com.wwm.attrs.internal.Attribute;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.dimensions.IDimensions;

/**
 * As RangePreference, but using float instead of Comparable (i.e. lighter weight)
 * @author Neale
 */
public class FloatRangePreference extends Attribute implements IFloatRangePreference, Comparable<FloatRangePreference> {

	private static final long serialVersionUID = 3256725065550083385L;

    
	protected float minDiff;  // better named lowToPrefDiff, but we can't rename cos of serialisation 
	protected float maxDiff;  // better named prefToHighDiff, but we can't rename cos of serialisation
	protected float preferred;

	/**
	 * Default constructor
	 */
	public FloatRangePreference( int attrId ){
		this( attrId, -1, 0, 1);
	}
	
	/**
	 * @param low float Lowest value in preference range
	 * @param high float Highest value in preference range
     * @param preferred float Preferred value in preference range
     * 
     *     Pref
     *      /\
     *     /  \
     *    /    \
     *   /      \
     *  Lo      Hi
	 */       
	public FloatRangePreference(int attrId, float low, float preferred, float high) {
		super( attrId );
		this.minDiff = preferred - low;
		this.maxDiff = high - preferred;
		this.preferred = preferred;
		assert( this.minDiff >= 0f);
		assert( this.maxDiff >= 0f);
	}

	
    public FloatRangePreference(FloatRangePreference clonee) {
		super(clonee);
		this.minDiff = clonee.minDiff;
		this.maxDiff = clonee.maxDiff;
		this.preferred = clonee.preferred;
	}

	/* (non-Javadoc)
	 * @see com.wwm.attrs.simple.IFloatRangePreference#getMax()
	 */
	public final float getMax() {
		return preferred + maxDiff;
	}
	/* (non-Javadoc)
	 * @see com.wwm.attrs.simple.IFloatRangePreference#getMin()
	 */
    
	public final float getMin() {
		return preferred - minDiff;
	}

    /* (non-Javadoc)
	 * @see com.wwm.attrs.simple.IFloatRangePreference#getPreferred()
	 */
    public float getPreferred() {
        return preferred;
    }
    
	/**
	 * @param val float
	 * @return boolean
	 */
	public final boolean contains(float val) {
		return ( minDiff <= val && minDiff < maxDiff);
	}
	
    /* (non-Javadoc)
     * @see com.wwm.attrs.dimensions.IDimensions#getDimension(int)
     */
    public float getDimension(int dimension) {
        switch (dimension) {
        case PREF:
            return preferred;
        case LOW_TO_PREF_DIFF:
            return minDiff;
        case PREF_TO_HIGH_DIFF:
            return maxDiff;

        default:
            throw new RuntimeException("Illegal dimension");
        }
    }

    /* (non-Javadoc)
     * @see com.wwm.attrs.dimensions.IDimensions#getNumDimensions()
     */
    public int getNumDimensions() {
        return DIMENSIONS;
    }
    
    /* (non-Javadoc)
     * @see com.wwm.attrs.dimensions.IDimensions#setDimension(int, float)
     */
    public void setDimension(int dimension, float val) {
        switch (dimension) {
        case PREF:
	        preferred = val;
	        break;
        case LOW_TO_PREF_DIFF:
            minDiff = val;
            break;
        case PREF_TO_HIGH_DIFF:
            maxDiff = val;
            break;

        default:
            throw new RuntimeException( "Illegal dimension");
        }
    }

    /* (non-Javadoc)
     * @see com.wwm.attrs.dimensions.IDimensions#setDimensionIfLower(int, float)
     */
    public void setDimensionIfLower(int dimension, float val) {
        if (val < getDimension(dimension)) {
            setDimension( dimension, val );
        }
    }

    /* (non-Javadoc)
     * @see com.wwm.attrs.dimensions.IDimensions#setDimensionIfHigher(int, float)
     */
    public void setDimensionIfHigher(int dimension, float val) {
        if (val > getDimension(dimension)) {
            setDimension( dimension, val );
        }
    }

    
    /**
     * Inefficient.  ONLY FOR DEBUGGING
     */
    @Override
	public String toString(){
//      return AttributeIdMap.instance.getName(attrId) + ":" 
        return "AttrId(" + attrId + "):" 
        + new FloatValue( attrId, getMin() ) 
        + " -> " + new FloatValue( attrId, getPreferred() ) + " <- " 
        + new FloatValue( attrId, getMax() );
    }
    

    public String toString( int attrId ){
        return toString();
    }

    
	public int compareAttribute(IAttribute rhs) {
		return compareTo((FloatRangePreference)rhs);
	}

	
	public int compareTo(FloatRangePreference rhs) {
		if (minDiff < rhs.minDiff) return -1;
		if (minDiff > rhs.minDiff) return 1;
		if (maxDiff < rhs.maxDiff) return -1;
		if (maxDiff > rhs.maxDiff) return 1;
		if (preferred < rhs.preferred) return -1;
		if (preferred > rhs.preferred) return 1;
		return 0;
	}

	public boolean expandDown(IDimensions val) {
		assert(false);	// shouldn't be used as a constraint/annotation
		return false;
	}

	public boolean expandUp(IDimensions val) {
		assert(false);	// shouldn't be used as a constraint/annotation
		return false;
	}

	@Override
	public DimensionsRangeConstraint createAnnotation() {
		// defensive copies as we don't want DRC modifying 'this'
		Dimensions point1 = new Dimensions(this);
		Dimensions point2 = new Dimensions(this);
		return new DimensionsRangeConstraint(getAttrId(), point1, point2);
	}

	public boolean equals(IDimensions rhs) {
		FloatRangePreference val = (FloatRangePreference) rhs;
		
		return this.maxDiff == val.maxDiff && this.minDiff == val.minDiff && this.preferred == val.preferred;
	}
	
	@Override
	public int hashCode() {
		return Float.floatToIntBits(maxDiff) + Float.floatToIntBits(minDiff) + Float.floatToIntBits(preferred);
	}
	

	@Override
	public FloatRangePreference clone() {
		return new FloatRangePreference(this);
	}

	public boolean canExpandDown(IDimensions value) {
		assert(false);	// shouldn't be used as a constraint/annotation
		return false;
	}

	public boolean canExpandUp(IDimensions value) {
		assert(false);	// shouldn't be used as a constraint/annotation
		return false;
	}
}
