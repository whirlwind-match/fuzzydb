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

import java.util.Arrays;

import org.fuzzydb.attrs.simple.FloatValue;


import com.wwm.model.dimensions.IDimensions;

/**
 * @author Neale
 */
public class Dimensions implements IDimensions, Cloneable {

    private static final long serialVersionUID = 3761406395995928627L;
    public static final int SET_MIN = 0;
    public static final int SET_MAX = 1;

    protected float[] values = null;

    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private Dimensions() {
        super();
    }

    /**Basic constructor which leaves all dimensions uninitialised (zero)
     * @param numDimensions
     */
    public Dimensions(int numDimensions) {
        super();
        values = new float[numDimensions];
    }

    /**
     * Create based on anything compatible
     * @param val
     */
    public Dimensions(IDimensions val) {
        values = new float[val.getNumDimensions()];
        for (int i = 0; i < values.length; i++) {
            values[i] = val.getDimension( i );
        }
    }

    public static Dimensions createFrom2D(float a, float b) {
        Dimensions rval = new Dimensions(2);
        rval.values[0] = a;
        rval.values[1] = b;
        return rval;
    }

    public static Dimensions createFrom3D(float a, float b, float c) {
        Dimensions rval = new Dimensions(3);
        rval.values[0] = a;
        rval.values[1] = b;
        rval.values[2] = c;
        return rval;
    }

    public static Dimensions createFrom4D(float a, float b, float c, float d) {
        Dimensions rval = new Dimensions(4);
        rval.values[0] = a;
        rval.values[1] = b;
        rval.values[2] = c;
        rval.values[3] = d;
        return rval;
    }

    public static Dimensions createFrom5D(float a, float b, float c, float d, float e) {
        Dimensions rval = new Dimensions(5);
        rval.values[0] = a;
        rval.values[1] = b;
        rval.values[2] = c;
        rval.values[3] = d;
        rval.values[4] = e;
        return rval;
    }

    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.dimensions.IDimensions#getDimension(int)
     */
    public float getDimension(int dimension) {
        return values[dimension];
    }

    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.dimensions.IDimensions#getNumDimensions()
     */
    public int getNumDimensions() {
        return values.length;
    }

    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.dimensions.IDimensions#setDimension(int, float)
     */
    public void setDimension(int dimension, float value) {
        values[dimension] = value;
    }

    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.dimensions.IDimensions#setDimensionIfLower(int, float)
     */
    public void setDimensionIfLower(int dimension, float val) {
        if (val < getDimension(dimension)) {
            setDimension( dimension, val );
        }
    }

    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.dimensions.IDimensions#setDimensionIfHigher(int, float)
     */
    public void setDimensionIfHigher(int dimension, float val) {
        if (val > getDimension(dimension)) {
            setDimension( dimension, val );
        }
    }

    public String toString(int attrId) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            if (i < values.length - 1) {
                str.append(new FloatValue(attrId, values[i]).toString()).append(",");
            }
            else {
                str.append( new FloatValue(attrId, values[i]).toString());
            }
        }
        return str.toString();
    }

    public boolean expandDown(IDimensions val) {
        assert(val.getNumDimensions() == getNumDimensions());
        boolean rval = false;
        for (int i=0; i<getNumDimensions(); i++) {
            float a = val.getDimension(i);
            if (values[i] > a) {
                values[i] = a;
                rval = true;
            }
        }
        return rval;
    }

    public boolean expandUp(IDimensions val) {
        assert(val.getNumDimensions() == getNumDimensions());
        boolean rval = false;
        for (int i=0; i<getNumDimensions(); i++) {
            float a = val.getDimension(i);
            if (values[i] < a) {
                values[i] = a;
                rval = true;
            }
        }
        return rval;
    }

    public boolean equals(IDimensions rhs) {
    	if (rhs instanceof Dimensions == false){
    		return false;
    	}
        Dimensions val = (Dimensions) rhs;
        if (this.values.length != val.values.length) {
            return false;
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] != val.values[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
    	return Arrays.hashCode(values);
    }

    @Override
    public final Dimensions clone() {
        return new Dimensions(this);
    }

    public boolean canExpandDown(IDimensions value) {
        assert(value.getNumDimensions() == getNumDimensions());
        for (int i=0; i<getNumDimensions(); i++) {
            float a = value.getDimension(i);
            if (values[i] > a) {
                return true;
            }
        }
        return false;
    }

    public boolean canExpandUp(IDimensions value) {
        assert(value.getNumDimensions() == getNumDimensions());
        for (int i=0; i<getNumDimensions(); i++) {
            float a = value.getDimension(i);
            if (values[i] < a) {
                return true;
            }
        }
        return false;
    }

    //	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    //		// Select the stuff we want
    //		GetField f = in.readFields();
    //		values = (float[]) f.get("values", null);
    //	}
}
