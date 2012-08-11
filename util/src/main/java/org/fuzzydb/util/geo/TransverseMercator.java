/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.util.geo;

/**
 * Definition for a grid
 */
public final class TransverseMercator {
	public final double f0;			// Scale factor on central meridian
	public final double phi0;		// Latitude of true origin in rads
	public final double lamda0;		// Longitude of true origin and central meridian in rads
	public final double n0;			// Northing of true origin
	public final double e0;			// Easting of true origin
	public final Ellipsoid e;		// Ellipsoid
	
	public TransverseMercator(double f0, double phi0degs, double lamda0degs, double n0, double e0, Ellipsoid e) {
		this.f0 = f0;
		this.phi0 = Math.toRadians(phi0degs);
		this.lamda0 = Math.toRadians(lamda0degs);
		this.n0 = n0;
		this.e0 = e0;
		this.e = e;
	}
}
