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
package com.wwm.model.dimensions;


public class Point3D implements Cloneable, IPoint3D {
    private static final long serialVersionUID = 3258126947086448692L;
    protected float x;
    protected float y;
    protected float z;

    /**
     * Copy Constructor
     * @param original
     */
    public Point3D( IPoint3D original ){
        this( original.getX(), original.getY(), original.getZ() );
    }


    /**
     * @param x
     * @param y
     * @param z
     */
    public Point3D(float x, float y, float z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }


    @Override
    public String toString(){
        return x + "," + y + "," + z;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Point3D clone() {
        try {
            return (Point3D) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    /** @return Returns the x value */
    public float getX() { return x; }
    /** @return Returns the y value */
    public float getY() { return y; }
    /** @return Returns the z value */
    public float getZ() { return z; }


    /** @param x The x to set. */
    public void setX(float x) {
        this.x = x;
    }
    /** @param y The y to set. */
    public void setY(float y) {
        this.y = y;
    }
    /** @param z The z to set. */
    public void setZ(float z) {
        this.z = z;
    }


    /* (non-Javadoc)
     * @see likemynds.db.indextree.attributes.dimensions.IDimensions#getDimension(int)
     */
    public float getDimension(int dimension) {
        switch (dimension) {
        case 0:
            return x;
        case 1:
            return y;
        case 2:
            return z;

        default:
            throw new RuntimeException( "Illegal dimension");
        }
    }


    /* (non-Javadoc)
     * @see likemynds.db.indextree.attributes.dimensions.IDimensions#getNumDimensions()
     */
    public int getNumDimensions() {
        return 3;
    }
    /* (non-Javadoc)
     * @see likemynds.db.indextree.attributes.dimensions.IDimensions#setDimension(int, float)
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
     * @see likemynds.db.indextree.attributes.dimensions.IDimensions#setDimensionIfLower(int, float)
     */
    public void setDimensionIfLower(int dimension, float val) {
        if (val < getDimension(dimension)) {
            setDimension( dimension, val );
        }
    }

    /* (non-Javadoc)
     * @see likemynds.db.indextree.attributes.dimensions.IDimensions#setDimensionIfHigher(int, float)
     */
    public void setDimensionIfHigher(int dimension, float val) {
        if (val > getDimension(dimension)) {
            setDimension( dimension, val );
        }
    }

    public String toString( int attrId ) {
        return toString();
    }


    public boolean expandDown(IDimensions val) {
        assert(val.getNumDimensions() == 3);
        boolean rval = false;
        float xx = val.getDimension(0);
        float yy = val.getDimension(1);
        float zz = val.getDimension(2);
        if (x > xx) {
            rval = true;
            x = xx;
        }
        if (y > yy) {
            rval = true;
            y = yy;
        }
        if (z > zz) {
            rval = true;
            z = zz;
        }
        return rval;
    }


    public boolean expandUp(IDimensions val) {
        assert(val.getNumDimensions() == 3);
        boolean rval = false;
        float xx = val.getDimension(0);
        float yy = val.getDimension(1);
        float zz = val.getDimension(2);
        if (x < xx) {
            rval = true;
            x = xx;
        }
        if (y < yy) {
            rval = true;
            y = yy;
        }
        if (z < zz) {
            rval = true;
            z = zz;
        }
        return rval;
    }


    public boolean equals(IPoint3D rhs) {
    	if (! (rhs instanceof Point3D)) {
    		return false;
    	}
        Point3D val = (Point3D) rhs;
        return this.x == val.x && this.y == val.y && this.z == val.z;
    }

    public boolean equals(IDimensions rhs) {
    	if (! (rhs instanceof Point3D)) {
    		return false;
    	}
        Point3D val = (Point3D) rhs;
        return this.x == val.x && this.y == val.y && this.z == val.z;
    }


    public boolean canExpandDown(IDimensions val) {
        assert(val.getNumDimensions() == 3);
        boolean rval = false;
        float xx = val.getDimension(0);
        float yy = val.getDimension(1);
        float zz = val.getDimension(2);
        if (x > xx) {
            rval = true;
        }
        if (y > yy) {
            rval = true;
        }
        if (z > zz) {
            rval = true;
        }
        return rval;
    }


    public boolean canExpandUp(IDimensions val) {
        assert(val.getNumDimensions() == 3);
        boolean rval = false;
        float xx = val.getDimension(0);
        float yy = val.getDimension(1);
        float zz = val.getDimension(2);
        if (x < xx) {
            rval = true;
        }
        if (y < yy) {
            rval = true;
        }
        if (z < zz) {
            rval = true;
        }
        return rval;
    }
}
