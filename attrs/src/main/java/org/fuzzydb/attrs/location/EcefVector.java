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
package org.fuzzydb.attrs.location;



import org.fuzzydb.attrs.dimensions.DimensionsRangeConstraint;
import org.fuzzydb.attrs.dimensions.Value3D;
import org.fuzzydb.dto.dimensions.IPoint3D;



public class EcefVector extends Value3D {

    private static final long serialVersionUID = 3258134652223828787L;
    public static final float EARTHCIRCUMFERENCE = 24901.55f;
    public static final float EARTHDIAMETER = EARTHCIRCUMFERENCE / (float)Math.PI;


    /**
     * Default constructor so we can use class.newInstance()
     */
    public EcefVector(){
        super();
    }

    /**Copy constructor
     * @param rhs
     */
    public EcefVector(EcefVector rhs){
        super(rhs);
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public EcefVector(int attrId, float x, float y, float z) {
        super(attrId, x, y, z);
    }

    /**
     * Double precision ctor to encourage casting to float only for storage.
     */
    protected EcefVector(int attrId, double x, double y, double z) {
    	super(attrId, (float)x, (float)y, (float)z);
    }


    public EcefVector(int attrId, IPoint3D point) {
        super(attrId, point);
    }


	public final double getMag2() {
        return getX() * getX() + getY() * getY() + getZ() * getZ();
    }


    public final double getMag() {
        return Math.sqrt(getMag2());
    }


    public final double getLatDegs() {
    	double lat = getLatRads() / Math.PI * 180;
        return lat;
    }


    public final double getLatRads() {
    	double lat = Math.asin( getZ() );
        return lat;
    }


    public final double getLonDegs() {
    	double lon = getLonRads() / Math.PI * 180;
        return lon;
    }

    public final double getLonRads() {
		double lon = Math.atan2( getY(), getX() );
//		double cosLat = Math.cos(getLatRads());
//		double lon = Math.acos( getX() / cosLat );
//        if ( Double.isNaN(lon) ) {
//            lon = 0f; // FIXME: IS THIS A FUDGE (We were getting acos(1.00001) )
//        }
//        if (getY() < 0) {
//            lon *= -1f; // FIXME: Check this
//        }
        return lon;
    }


    /**
     * @param lat latitude in rads
     * @param lon longitude in rads
     */
    static public EcefVector fromRads(int attrId, double lat, double lon) {
        EcefVector v = new EcefVector( attrId,
                Math.cos(lat) * Math.cos(lon),
                Math.cos(lat) * Math.sin(lon),
                Math.sin(lat));

        assert(v.getMag2() > 0.9999);
        assert(v.getMag2() < 1.0001);

        return v;
    }


    /**
     * Create an EcefVector given a lat,lon value in degrees.
     * @param lat
     * @param lon
     * @return
     */
    static public EcefVector fromDegs(int attrId, double lat, double lon) {
        final double degToRad = Math.PI / 180;
        return fromRads(attrId, lat * degToRad, lon * degToRad);
    }


    /**
     * @param vector The vector to calculate range against. Both must be unit vectors.
     * @return Distance between vectors in miles
     */
    public final float distance(EcefVector vector) {

        assert(vector.getMag2() > 0.9999);
        assert(vector.getMag2() < 1.0001);
        assert(getMag2() > 0.9999);
        assert(getMag2() < 1.0001);
        float curvedDistance = (float)(Math.acos(getX()
                * vector.getX()
                + getY()
                * vector.getY()
                + getZ()
                * vector.getZ())
                / (2 * Math.PI) * EARTHCIRCUMFERENCE);


        double x = getX() - vector.getX();
        double y = getY() - vector.getY();
        double z = getZ() - vector.getZ();

        float straightDistance = (float) ecefToMiles( Math.sqrt(x*x+y*y+z*z) );

//        distancetime += System.currentTimeMillis() - start;
//        distanceCount++;
//        if (distanceCount > 1e6) {
//            System.out.println("EcevVector.distance() called 1m times, total cost: " + distancetime + "ms");
//            distancetime = 0f;
//            distanceCount = 0;
//        }

        // The above acos can work on values close to 1 with the data getting underflowed. So we correct for this
        // error by reverting to straight line distance, to avoid ever returning a distance smaller than the point-constraint scorer saw.
        return (curvedDistance > straightDistance) ? curvedDistance : straightDistance;
    }


    /**
     * @param vector
     * @return
     */
    public final float distance(IPoint3D vector) {
        return (float) (Math.acos(getX()
                * vector.getX()
                + getY()
                * vector.getY()
                + getZ()
                * vector.getZ())
                / (2 * Math.PI) * EARTHCIRCUMFERENCE);

    }


    @Override
    public String toString() {
        return "(" + getX() + "," + getY() + "," + getZ() + ")";
    }


    public String toString( int attrId ) {
        return toString();
    }

    /* (non-Javadoc)
     * @see likemynds.db.indextree.attributes.Attribute#createAnnotation()
     */
    @Override
    public DimensionsRangeConstraint createAnnotation() {
        // Defensive copies, as we don't want DRC having access to modifying 'this'
        return new DimensionsRangeConstraint(getAttrId(), new EcefVector(this), new EcefVector(this));
    }

    public static float milesToEcef(float miles) {
        return 2f * miles / EARTHDIAMETER;
    }

    public static float ecefToMiles(float ecef) {
        return EARTHDIAMETER * ecef / 2f;
    }

    public static double ecefToMiles(double ecef) {
        return EARTHDIAMETER * ecef / 2;
    }

    @Override
    public EcefVector clone() {
        return new EcefVector(this);
    }
}