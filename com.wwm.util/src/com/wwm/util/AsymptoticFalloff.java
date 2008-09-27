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


/**
 * Class to provide a function that falls off from 1.0 to near zero, with
 * a 50% figure at a specified value.
 * There is one (and only one) value where it is absolutely 1.0, and
 * there are no values where it is absolutely zero, it just tends to zero as
 * the value tends to infinity.
 *
 * @author Neale Upstone
 */
public class AsymptoticFalloff {

    /** When class is loaded, we just run a quick test */
    static {
        /* Once only test harness */
        assert AsymptoticFalloff.getValue( 0.0f ) == 1.0f;
        assert AsymptoticFalloff.getValue( 1.0f ) == 0.5f;

        assert AsymptoticFalloff.getSingleFalloff( 10f, 20f, 1f, 10f ) == 1.0f;
        assert AsymptoticFalloff.getSingleFalloff( 10f, 20f, 1f, 20f ) == 0.5f;
    }
    
    /**
     * Gets the value of the falloff function for value, x.
     * If x is zero, the value is 1.0.
     * If x is 1.0, the value is 0.5.
     * As x -> infinity, value -> 0.0.
     * @param double x - (double as it'll get promoted when doing pow() func anyway)
     * @return float - falloff function result.
     */
    public static float getValue( double x ) {
        // implemented function is exp( -x )
        float result = (float) Math.pow( 2.0, -x );
        return result;
    }
    
    
    /** 
     * Get a falloff given a x values for peak, and falloff point (50% point)
     * and squareness.
     * @param start - Value that returns zero.
     * @param threshold - Value that returns 0.5
     * @param squareness - Squareness (between zero and infinity) higher is squarer.
     * @param x - a value greater than is the same side of start as threshold (i.e. if threshold < start, then x must be < start)
     * @return value - where 0.0 <= value <= 1.0
     * Note, if start == threshold, then return value will be zero or 1.0.
     */
    public static float getSingleFalloff( float start, float threshold, 
            float squareness, float x) {
        // if zero range, then offset is either zero or infinity giving a result of 1 or zero.
        if ( start == threshold) return ( x == start) ? 1.0f : 0.0f;
        
        float range, offset;
        if ( threshold > start) {
            range = threshold - start;
            offset = x - start;
        }
        else {
            range = start - threshold;
            offset = start - x;
        }
        // Test x is same side of start as threshold
        if ( offset < 0.0f ) throw new IllegalArgumentException( "Invalid x value");
        
        
        float normalised = offset / range;
        float result = getValue( Math.pow(normalised, squareness) );
        return result;
    }
    
    /** 
     * Get a falloff given a x values for lower falloff point, peak, upper falloff point (50% point)
     * and squareness.
     * @param lower - Lower value that returns 0.5 (cannot be equal to start)
     * @param peak - Value that returns zero.
     * @param upper - Upper value that returns 0.5 (cannot be equal to start)
     * @param squareness - Squareness (between zero and infinity) higher is squarer.
     * @param x - a value greater than is the same side of start as threshold (i.e. if threshold < start, then x must be < start)
     * @return value - where 0.0 < value <= 1.0
     */
    public static float getRangeFalloff( float lower, float peak, float upper, 
            float squareness, float x) {

        assert peak >= lower;
        assert upper >= peak;
        
        if ( x > peak ) return getSingleFalloff( peak, upper, squareness, x );
        return getSingleFalloff( peak, lower, squareness, x );
    }
}
