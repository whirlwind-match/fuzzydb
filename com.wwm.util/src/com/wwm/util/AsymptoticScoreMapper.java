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
package com.wwm.util;

import java.io.Serializable;


/**
 * Class to provide a function that falls off from 1.0 to near zero, with
 * a 50% figure at a specified value.
 * There is one (and only one) value where it is absolutely 1.0, and
 * there are no values where it is absolutely zero, it just tends to zero as
 * the value tends to infinity.
 *
 * @author Neale Upstone
 */
public class AsymptoticScoreMapper implements ScoreMapper, Serializable {
    
    private static final long serialVersionUID = 3104336355683759651L;

    // Quicky test
//    static {
//        ScoreMapper m = new AsymptoticScoreMapper( 5f, 0.5f );
//        assert( m.getScore(1.0f) == 1.0f );
//        assert( m.getScore(0.0f) == 0.5f );
//        assert( m.getScore(-0.1f) < 0.5f );
//    }
    
    private float squareness;
    private float inverseValueAtBoundary;
    
    public AsymptoticScoreMapper( float squareness, float valueAtBoundary) {
        this.squareness = squareness;
        this.inverseValueAtBoundary = 1.0f / valueAtBoundary;
    }
    
    /**
     * Returns 1.0 for 1.0
     * returns 0.5 for zero
     * returns asymptotic value tending to zero as scoreFactor tends -> -infinity.
     */
    public float getScore(float scoreFactor) {
    	
    	assert inverseValueAtBoundary >= 1f : "value at boundary must be <= 1, so inverse must be >= 1";
    	
        // scoreFactor: 1.0 -> -infinity (any negative value)
        // turn into range of zero -> infinity
        float x = 1.0f - scoreFactor;
        
        // Power up our scoreFactor to give our squareness. 
        // pow() ensures that 0.0 and 1.0 points stay same.
        float result = getValue( Math.pow(x, squareness) );
        return result;
    }
    
    /**
     * Implements 1 / ( 2 ^ -x )
     * If x is zero, the value is 1.0.
     * If x is 1.0, the value is 0.5.
     * As x -> infinity, value -> 0.0.
     * @param double x - (double as it'll get promoted when doing pow() func anyway)
     * @return float - falloff function result.
     */
    private float getValue( double x ) {
        // implemented function is exp( -x )
        float result = (float) Math.pow( inverseValueAtBoundary, -x );
        return result;
    }

    public float getInverseValueAtBoundary() {
        return inverseValueAtBoundary;
    }

    public float getSquareness() {
        return squareness;
    }
}
