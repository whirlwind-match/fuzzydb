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
package com.wwm.attrs.location;



import org.junit.Assert;
import org.junit.Test;

import com.wwm.attrs.location.EcefVector;

public class EcefVectorTest {

    private int locId = 1; 
    
    @Test public void testLatLonRads() {
    	EcefVector NorthPole1 = EcefVector.fromRads(locId, (float) Math.PI/2, 0);
    	EcefVector SouthPole1 = EcefVector.fromRads(locId, -(float) Math.PI/2, 0);
    	EcefVector NorthPole2 = EcefVector.fromRads(locId, (float) Math.PI/2, 0.3f);
    	EcefVector SouthPole2 = EcefVector.fromRads(locId, -(float) Math.PI/2, 7.2f);
    	
    	EcefVector GmtEquator = EcefVector.fromRads(locId, 0f, 0f);
    	EcefVector West90 = EcefVector.fromRads(locId, 0f, -(float) Math.PI/2);
    	EcefVector East90 = EcefVector.fromRads(locId, 0f, (float) Math.PI/2);

    	Assert.assertTrue(AboutUnity(NorthPole1.getMag()));
    	Assert.assertTrue(AboutUnity(SouthPole1.getMag()));
    	Assert.assertTrue(AboutUnity(NorthPole2.getMag()));
    	Assert.assertTrue(AboutUnity(SouthPole2.getMag()));
    	Assert.assertTrue(AboutUnity(GmtEquator.getMag()));
    	Assert.assertTrue(AboutUnity(West90.getMag()));
    	Assert.assertTrue(AboutUnity(East90.getMag()));
    	
    	Assert.assertTrue(AboutDistance(NorthPole1.distance(GmtEquator), 0.25f));
    	Assert.assertTrue(AboutDistance(NorthPole1.distance(West90), 0.25f));
    	Assert.assertTrue(AboutDistance(NorthPole1.distance(East90), 0.25f));
    	
    	Assert.assertTrue(AboutDistance(NorthPole1.distance(NorthPole2), 0f));
    	Assert.assertTrue(AboutDistance(SouthPole1.distance(SouthPole2), 0f));
    	
    	Assert.assertTrue(AboutDistance(West90.distance(West90), 0f));

    	Assert.assertTrue(AboutDistance(West90.distance(East90), 0.5f));
    	Assert.assertTrue(AboutDistance(NorthPole1.distance(SouthPole2), 0.5f));
    }

	/**
	 * @param mag
	 * @return
	 */
	private boolean AboutUnity(double mag) {
		return mag > 0.999 && mag < 1.001;
	}
	
	private boolean AboutDistance(float distance, float section)
	{
		float margin = 0.000001f * EcefVector.EARTHCIRCUMFERENCE;
		float dhi = distance + margin;
		float dlo = distance - margin;
		float desired = EcefVector.EARTHCIRCUMFERENCE*section;
		
		return dhi >= desired && dlo <= desired;
	}
	
}
