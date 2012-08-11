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
 * Definiton for reference ellipsoid
 */
public final class Ellipsoid {
	public final double a;	// The semi-major axis
	public final double b;	// The semi-minor axis
	public final double e2;	// The ellipsoid eccentricity2
	
	public Ellipsoid(double a, double b) {
		this.a = a;
		this.b = b;
		this.e2 = (a*a-b*b) / (a*a);
	}
}
