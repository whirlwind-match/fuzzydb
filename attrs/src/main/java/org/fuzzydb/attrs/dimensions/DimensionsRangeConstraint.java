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
package org.fuzzydb.attrs.dimensions;


import org.fuzzydb.attrs.internal.BranchConstraint;
import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeConstraint;
import org.fuzzydb.dto.dimensions.IDimensions;



/**
 * A range of n-Dimension (e.g. 3D) values
 */
public class DimensionsRangeConstraint extends BranchConstraint { // implements IRange3D {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3544951047949793584L;
	DimensionsRange bounds;

	//================ CONSTRUCTORS, FINALISERS AND INITIALISERS ==================
	/**
	 * Default constructor.
	 */
	public DimensionsRangeConstraint(){
	    this( 0 );
	}

	public DimensionsRangeConstraint(int attrId){
	    super( attrId );
	    bounds = null;
	}
	
	/**
	 * @param min
	 * @param max
	 */
	public DimensionsRangeConstraint(int attrId, IDimensions min, IDimensions max) {
		super( attrId );
		bounds = new DimensionsRange(min, max);
	}

    public DimensionsRangeConstraint(DimensionsRangeConstraint clonee) {
		super(clonee);
		this.bounds = (DimensionsRange)clonee.bounds.clone();
	}


    //========================= GETTERS AND SETTERS ==============================
    /**
	 * @return Returns the max.
	 */
	public IDimensions getMax() {
		return bounds.getMax();
	}
	/**
	 * @return Returns the min.
	 */
	public IDimensions getMin() {
		return bounds.getMin();
	}

	public boolean contains(IDimensions point) {
		return bounds.contains(point);
	}
	
	
	@Override
	public boolean consistent(IAttribute val) {
		if (val == null){
			return isIncludesNotSpecified();
		}
		return bounds.contains( (IDimensions) val );
	}

	/**
	 * Array based version of consistent, where the IDimensions attribute can be found at the offset within floats
	 */
	public boolean consistent(float[] floats, int offset) {
		return bounds.contains( floats, offset );
	}
	
	
	
    /**
     * Get the lower box that is formed from splitting this box along the specified axis 
     * @param splitVal 
     * @param dimension e.g. 0 = X, 1 = Y, 2 = Z
     * @return
     */
    public IAttributeConstraint getSubBoxLow(float splitVal, int axis) {
        DimensionsRangeConstraint newBox;
        newBox = this.clone();
        
        // Set the dimension we're splitting on
        newBox.getMax().setDimension( axis, splitVal );

        return newBox;
    }
    
    
    /**
     * Get the higher box that is formed from splitting this box along the specified axis 
     * @param splitVal 
     * @param axis 0 = X, 1 = Y, 2 = Z
     * @return
     */
    public IAttributeConstraint getSubBoxHigh(float splitVal, int axis) {
        DimensionsRangeConstraint newBox;
        newBox = this.clone();
    
        // Set the dimension we're splitting on
        newBox.getMin().setDimension( axis, splitVal );

        return newBox;
    }

    /**
     * Get distance from point to nearest point in the box, in miles
     * @param point
     * @return
     */
    public float getDistance(EcefVector point) {
    	
        float delta;
        float sumSquared = 0f;
    	int numDimensions = point.getNumDimensions();
    	assert(numDimensions == 3);
    	for (int i = 0; i < numDimensions; i++) {
        	float p = point.getDimension(i);
        	float rMin = getMin().getDimension(i); 
        	float rMax = getMax().getDimension(i); 
        	if ( p < rMin ) { 
        	    delta = rMin - p;
        	}
        	else if ( p > rMax ) {
        	    delta = p - rMax;
        	}
        	else {
        	    delta = 0f;
        	}
        	sumSquared += delta * delta;
        }
    	
    	return EcefVector.ecefToMiles((float)Math.sqrt(sumSquared));	// ecef units are a cube of 2, convert to miles
    }

    /**
     * Calculate minimum distance *in miles* between two boxes
     * If they overlap, the distance is zero
     * @param otherBox
     * @return distance in miles
     */
    public float getDistance(DimensionsRangeConstraint otherBox) {
        float delta;
        float sumSquared = 0f;

        int numDimensions = otherBox.getMin().getNumDimensions();
        assert(numDimensions == 3); // for now
        
        for (int i = 0; i < numDimensions; i++) {
            float otherMin = otherBox.getMin().getDimension(i);
            float otherMax = otherBox.getMax().getDimension(i);
            float thisMin = getMin().getDimension(i); 
            float thisMax = getMax().getDimension(i); 
            if ( otherMax < thisMin ) {    // In this dimension, other is to the left of 'this'
                delta = thisMin - otherMax;
            }
            else if ( otherMin > thisMax ) { // ... other is to the right
                delta = otherMin - thisMax;
            }
            else { // .. else, the two overlap to some extent
                delta = 0f;
            }
            sumSquared += delta * delta;
        }
        
        return EcefVector.ecefToMiles((float)Math.sqrt(sumSquared));    // ecef units are a cube of 2, convert to miles
    }
    
    
	/**
	 * Find longest distance across the two boxes
	 * @param otherBox
	 * @return
	 */
	public float getMaxDistance(DimensionsRangeConstraint otherBox) {
        float delta;
        float sumSquared = 0f;

        int numDimensions = otherBox.getMin().getNumDimensions();
        assert(numDimensions == 3); // for now
        
        for (int i = 0; i < numDimensions; i++) {
            float otherMin = otherBox.getMin().getDimension(i);
            float otherMax = otherBox.getMax().getDimension(i);
            float thisMin = getMin().getDimension(i); 
            float thisMax = getMax().getDimension(i);
            // Find biggest distance between min of one and max of other ( does work for all overlap scenarios...) 
            delta = thisMax - otherMin;
            float delta2 = otherMax - thisMin;
            if (delta2 > delta) delta = delta2;
            
            assert(delta >= 0); // Should be +ve one way around
            sumSquared += delta * delta;
        }
        
        return EcefVector.ecefToMiles((float)Math.sqrt(sumSquared));    // ecef units are a cube of 2, convert to miles
	}
    
	/**
	 * Find the longest possible distance from this box to the point
	 * @param point
	 * @return
	 */
	public float getMaxDistance(EcefVector point) {
        float delta;
        float sumSquared = 0f;

        int numDimensions = point.getNumDimensions();
        assert(numDimensions == 3); // for now
        
        for (int i = 0; i < numDimensions; i++) {
            float other = point.getDimension(i);
            float thisMin = getMin().getDimension(i); 
            float thisMax = getMax().getDimension(i);
            // Find biggest distance on this dimension 
            delta = thisMax - other;
            float delta2 = other - thisMin;
            if (delta2 > delta) delta = delta2;
            
            assert(delta >= 0); // Should be +ve one way around
            sumSquared += delta * delta;
        }
        
        return EcefVector.ecefToMiles((float)Math.sqrt(sumSquared));    // ecef units are a cube of 2, convert to miles
	}
    
    @Override
	public DimensionsRangeConstraint clone() {
        return new DimensionsRangeConstraint( this );
    }

    
    @Override
	public String toString(){
        if (bounds == null) return null;
        return bounds.toString( this.attrId );
    }

	@Override
	protected boolean expandNonNull(IAttribute value) {
		IDimensions val = (IDimensions) value;
		return bounds.expand(val);
	}
	
	@Override
	public boolean equals(Object rhs) {
        if (!(rhs instanceof DimensionsRangeConstraint)) {
            return false;
        }
		DimensionsRangeConstraint val = (DimensionsRangeConstraint)rhs;
		return this.bounds.equals(val.bounds)
        && super.equals(val);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() + 31 * bounds.hashCode();
	}
	

	@Override
	public boolean isExpandedByNonNull(IAttribute value) {
		IDimensions val = (IDimensions) value;
		return bounds.canExpand(val);
	}


}
