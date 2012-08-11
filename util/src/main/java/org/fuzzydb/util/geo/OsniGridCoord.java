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
 * A grid co-ordinate on the Irish National Grid, as used by OSNI
 */
public final class OsniGridCoord extends GridCoord {

	public OsniGridCoord(double easting, double northing) {
		super(easting, northing);
	}

	@Override
	protected TransverseMercator getProjection() {
		return Datum.irishNationalGrid;
	}
}
