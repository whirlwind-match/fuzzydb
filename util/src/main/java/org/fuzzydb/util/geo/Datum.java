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

/**Static data for reference ellipsoids and grids
 * <br>Data obtained from:
 * <br>http://www.gps.gov.uk/guidea.asp
 */
public final class Datum {
	public static final Ellipsoid airy1830 = new Ellipsoid(6377563.396, 6356256.910);
	public static final Ellipsoid airy1830Modified = new Ellipsoid(6377340.189, 6356034.447);
	
	public static final TransverseMercator nationalGrid = new TransverseMercator(0.9996012717, 49.0, -2.0, -100000, 400000, airy1830);
	public static final TransverseMercator irishNationalGrid = new TransverseMercator(1.000035, 53.5, -8.0, 250000, 200000, airy1830Modified);
}
