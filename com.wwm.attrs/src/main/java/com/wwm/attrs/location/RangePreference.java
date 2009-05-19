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
package com.wwm.attrs.location;



import com.wwm.attrs.dimensions.Dimensions;
import com.wwm.attrs.dimensions.DimensionsRangeConstraint;
import com.wwm.attrs.internal.Attribute;
import com.wwm.attrs.simple.IFloat;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.dimensions.IDimensions;


/**
 * 4-dimensional attribute... this'll be an interesting one!!
 * @author ac
 * @deprecated Use LocationAndRangeScorer
 */
@Deprecated
public class RangePreference extends Attribute implements IDimensions, IFloat /* for range */ {

    /**
     * 
     */
    private static final long serialVersionUID = 3833183627319392309L;

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    public static final int RANGE = 3;

    public static final int DIMENSIONS = 4;

    protected EcefVector centre;	// dimensions 0-2
    protected float range;	// range in miles, dimension 3
    protected boolean preferClose;

    /**
     * @param centre
     * @param range in miles
     * @param preferClose
     * @deprecated Please supply attrId
     */
    @Deprecated
    public RangePreference(EcefVector centre, float range, boolean preferClose) {
        super(centre.getAttrId()); // TODO Check this makes sense... I think it has to!
        this.centre = centre;
        this.range = range;
        this.preferClose = preferClose;
        assert(range>0.0f);
    }

    public RangePreference(int attrId, EcefVector centre, float range, boolean preferClose) {
        super(attrId);
        this.centre = centre;
        this.range = range;
        this.preferClose = preferClose;
        assert(range>0.0f);
    }

    public RangePreference(RangePreference clonee) {
        super(clonee);
        this.centre = new EcefVector(clonee.centre);
        this.range = clonee.range;
        this.preferClose = clonee.preferClose;
    }

    public @Override RangePreference clone() {
        return new RangePreference(this);
    }

    /**
     * @return Returns the centre.
     */
    public EcefVector getCentre() {
        return centre;
    }
    /**
     * @return Returns the preferClose.
     */
    public boolean isPreferClose() {
        return preferClose;
    }
    /**
     * @return Returns the range.
     */
    public float getRange() {
        return range;
    }


    @Override
    public String toString() {
        return "RangePref: " + centre + ", dist: " + range;
    }

    public int compareAttribute(IAttribute rhs) {
        RangePreference r = (RangePreference) rhs;
        int c = centre.compareAttribute(r.centre);
        if (c != 0) {
            return c;
        }
        if (range<r.range) {
            return -1;
        }
        if (range>r.range) {
            return 1;
        }
        if (preferClose != r.preferClose) {
            return preferClose ? -1 : 1;
        }
        return 0;
    }

    @Override
    public DimensionsRangeConstraint createAnnotation() {
        // defensive copies as we don't want DRC modifying 'this'
        Dimensions point1 = new Dimensions(this);
        Dimensions point2 = new Dimensions(this);
        return new DimensionsRangeConstraint(getAttrId(), point1, point2);
    }

    public float getDimension(int dimension) {
        assert(dimension < DIMENSIONS);
        if (dimension <= Z) { return centre.getDimension(dimension); }

        assert( dimension == RANGE);
        return EcefVector.milesToEcef(range);
    }

    public int getNumDimensions() {
        return DIMENSIONS;
    }

    public void setDimension(int dimension, float val) {
        if (dimension < Z) {
            centre.setDimension(dimension, val);
        }
        assert( dimension == RANGE);
        range = EcefVector.ecefToMiles(val);
    }

    public String toString(int attrId) {
        return "[RangePreference.toString() not implemented]";	// TODO: Implement
    }

    public void setDimensionIfLower(int dimension, float val) {
        if (val < getDimension(dimension)) {
            setDimension(dimension, val);
        }
    }

    public void setDimensionIfHigher(int dimension, float val) {
        if (val > getDimension(dimension)) {
            setDimension(dimension, val);
        }
    }

    public boolean expandDown(IDimensions val) {
        boolean rval = false;
        for (int i = 0; i < DIMENSIONS; i++) {
            if (val.getDimension(i) < getDimension(i)) {
                setDimension(i, val.getDimension(i));
                rval = true;
            }
        }
        return rval;
    }

    public boolean expandUp(IDimensions val) {
        boolean rval = false;
        for (int i = 0; i < DIMENSIONS; i++) {
            if (val.getDimension(i) > getDimension(i)) {
                setDimension(i, val.getDimension(i));
                rval = true;
            }
        }
        return rval;
    }

    public boolean equals(IDimensions rhs) {
        for (int i = 0; i < DIMENSIONS; i++) {
            if (rhs.getDimension(i) != getDimension(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean canExpandDown(IDimensions val) {
        boolean rval = false;
        for (int i = 0; i < DIMENSIONS; i++) {
            if (val.getDimension(i) < getDimension(i)) {
                setDimension(i, val.getDimension(i));
                rval = true;
            }
        }
        return rval;
    }

    public boolean canExpandUp(IDimensions val) {
        boolean rval = false;
        for (int i = 0; i < DIMENSIONS; i++) {
            if (val.getDimension(i) > getDimension(i)) {
                rval = true;
            }
        }
        return rval;
    }

    /**
     * IFloat impl returns range to give convenient handling of range.
     */
    public float getValue() {
        return range;
    }
}
