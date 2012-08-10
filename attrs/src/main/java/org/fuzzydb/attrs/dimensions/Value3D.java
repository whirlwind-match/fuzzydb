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


import org.fuzzydb.attrs.internal.Attribute;
import org.fuzzydb.core.whirlwind.internal.IAttribute;

import com.wwm.model.dimensions.IDimensions;
import com.wwm.model.dimensions.IPoint3D;

/**
 * X,Y,Z 
 */
public abstract class Value3D extends Attribute<Value3D> implements IPoint3D {

	private static final long serialVersionUID = 8140172132739575464L;
	
    public static final int DIMENSIONS = 3;

    protected float x = 0;
    protected float y = 0;
    protected float z = 0;

    /**
     * Default constructor, so we can instantiate from class.
     */
    public Value3D(){
        super(0); // default unused value for attrId
    }

    /** Copy constructor
     * @param rhs
     */
    public Value3D(Value3D rhs){
        super(rhs);
        x = rhs.x;
        y = rhs.y;
        z = rhs.z;
    }
    
    /**
     * @param val
     */
    public Value3D(int attrId, IPoint3D value) {
        super(attrId);
        this.x = value.getX();
        this.y = value.getY();
        this.z = value.getZ();
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public Value3D(int attrId, float x, float y, float z) {
        super(attrId);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public abstract Value3D clone() throws CloneNotSupportedException;
    

    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.dimensions.IDimensions#getDimension(int)
     */
    public float getDimension(int dimension) {
        switch (dimension) {
        case 0:
            return getX();
        case 1:
            return getY();
        case 2:
            return getZ();

        default:
            throw new RuntimeException("Illegal dimension");
        }
    }
    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.dimensions.IDimensions#getNumDimensions()
     */
    public int getNumDimensions() {
        return DIMENSIONS;
    }
    
    
    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.dimensions.IDimensions#setDimension(int, float)
     */
    public void setDimension(int dimension, float val) {
        switch (dimension) {
        case 0:
            x = val;
            break;
        case 1:
            y = val;
            break;
        case 2:
            z = val;
            break;

        default:
            throw new RuntimeException( "Illegal dimension");
        }
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

    
    @Override
	public String toString() {
        return x + "," + y + "," + z;
    }
    
    /** @return Returns the x value */
    public final float getX() {
        return x;
    }
    
    /** @return Returns the y value */
    public final float getY() {
        return y;
    }
    
    /** @return Returns the z value */
    public final float getZ() {
        return z;
    }

    /** @param x The x to set. */
    public final void setX(float x) {
        this.x = x;
    }
    
    /** @param y The y to set. */
    public final void setY(float y) {
        this.y = y;
    }
    
    /** @param z The z to set. */
    public final void setZ(float z) {
        this.z = z;
    }

	public int compareAttribute(IAttribute rhs) {
		Value3D r = (Value3D) rhs;
		if (x < r.x) return -1;
		if (x > r.x) return 1;
		if (y < r.y) return -1;
		if (y > r.y) return 1;
		if (z < r.z) return -1;
		if (z > r.z) return 1;
		return 0;
	}

	public boolean expandDown(IDimensions val) {
		boolean rval = false;
		assert(val.getNumDimensions() == 3);
		float xx = val.getDimension(0);
		float yy = val.getDimension(1);
		float zz = val.getDimension(2);
		if (this.x > xx) {
			this.x = xx;
			rval = true;
		}
		if (this.y > yy) {
			this.y = yy;
			rval = true;
		}
		if (this.z > zz) {
			this.z = zz;
			rval = true;
		}
		return rval;
	}

	public boolean expandUp(IDimensions val) {
		boolean rval = false;
		assert(val.getNumDimensions() == 3);
		float xx = val.getDimension(0);
		float yy = val.getDimension(1);
		float zz = val.getDimension(2);
		if (this.x < xx) {
			this.x = xx;
			rval = true;
		}
		if (this.y < yy) {
			this.y = yy;
			rval = true;
		}
		if (this.z < zz) {
			this.z = zz;
			rval = true;
		}
		return rval;
	}
	
	public boolean equals(IDimensions rhs) {
		if (rhs instanceof Dimensions) {
			Dimensions val = (Dimensions) rhs;
			assert(val.getNumDimensions() == 3);
			return x==val.getDimension(0) && y==val.getDimension(1) && z==val.getDimension(2);
		}
		Value3D val = (Value3D) rhs;
		return this.equals(val);
	}
	
    public boolean equals(IPoint3D rhs) {
		Value3D val = (Value3D) rhs;
		return this.equals(val);
    }

    public boolean equals(Value3D rhs) {
		return this.x == rhs.x && this.y == rhs.y && this.z == rhs.z;
    }

	public boolean canExpandDown(IDimensions val) {
		boolean rval = false;
		assert(val.getNumDimensions() == 3);
		float xx = val.getDimension(0);
		float yy = val.getDimension(1);
		float zz = val.getDimension(2);
		if (this.x > xx) {
			rval = true;
		}
		if (this.y > yy) {
			rval = true;
		}
		if (this.z > zz) {
			rval = true;
		}
		return rval;
	}

	public boolean canExpandUp(IDimensions val) {
		boolean rval = false;
		assert(val.getNumDimensions() == 3);
		float xx = val.getDimension(0);
		float yy = val.getDimension(1);
		float zz = val.getDimension(2);
		if (this.x < xx) {
			rval = true;
		}
		if (this.y < yy) {
			rval = true;
		}
		if (this.z < zz) {
			rval = true;
		}
		return rval;
	}
    
}
