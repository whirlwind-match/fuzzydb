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
package org.fuzzydb.attrs;


import org.fuzzydb.attrs.dimensions.DimensionsRangeConstraint;
import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.util.MTRandom;
import org.junit.Test;


import static org.junit.Assert.assertTrue;

public class LocationBoxTest {

	/**
	 * Error margin to allow for cumulative errors in calculations
	 */
	private static final float FLOAT_ERROR_MARGIN = 0.0001f;

	@Test
	public void testOnePoint() {
		EcefVector home = EcefVector.fromDegs(1, 45, 45);
		DimensionsRangeConstraint homeBc = home.createAnnotation();
		
		{
			EcefVector p1 = EcefVector.fromDegs(2, 55, 45);
			float p1ToBox = homeBc.getDistance(p1);
			float p1ToHome = home.distance(p1);
			assertTrue(p1ToBox <= p1ToHome);
		}
		
		{
			EcefVector p2 = EcefVector.fromDegs(2, 55, 55);
			float p2ToBox = homeBc.getDistance(p2);
			float p2ToHome = home.distance(p2);
			assertTrue(p2ToBox <= p2ToHome);
		}

		{
			EcefVector p3 = EcefVector.fromDegs(2, 45, 55);
			float p3ToBox = homeBc.getDistance(p3);
			float p3ToHome = home.distance(p3);
			assertTrue(p3ToBox <= p3ToHome);
		}

		{
			EcefVector p4 = EcefVector.fromDegs(2, 45, 45);
			float p4ToBox = homeBc.getDistance(p4);
			float p4ToHome = home.distance(p4);
			assertTrue(p4ToBox <= p4ToHome);
		}

		{
			EcefVector p1 = EcefVector.fromDegs(2, 35, 45);
			float p1ToBox = homeBc.getDistance(p1);
			float p1ToHome = home.distance(p1);
			assertTrue(p1ToBox <= p1ToHome);
		}
			
		{
			EcefVector p2 = EcefVector.fromDegs(2, 35, 35);
			float p2ToBox = homeBc.getDistance(p2);
			float p2ToHome = home.distance(p2);
			assertTrue(p2ToBox <= p2ToHome);
		}

		{
			EcefVector p3 = EcefVector.fromDegs(2, 45, 35);
			float p3ToBox = homeBc.getDistance(p3);
			float p3ToHome = home.distance(p3);
			assertTrue(p3ToBox <= p3ToHome);
		}

		{
			EcefVector p4 = EcefVector.fromDegs(2, 35, 35);
			float p4ToBox = homeBc.getDistance(p4);
			float p4ToHome = home.distance(p4);
			assertTrue(p4ToBox <= p4ToHome);
		}
	}

	@Test
	public void testOnePointTwoHomes() {
		EcefVector home = EcefVector.fromDegs(1, 45, 45);
		EcefVector home2 = EcefVector.fromDegs(1, 40, 40);
		DimensionsRangeConstraint homeBc = home.createAnnotation();
		homeBc.expand(home2);
		
		{
			EcefVector p1 = EcefVector.fromDegs(2, 55, 45);
			float p1ToBox = homeBc.getDistance(p1);
			float p1ToHome = home.distance(p1);
			float p1ToHome2 = home2.distance(p1);
			assertTrue(p1ToBox <= p1ToHome);
			assertTrue(p1ToBox <= p1ToHome2);
		}
		
		{
			EcefVector p2 = EcefVector.fromDegs(2, 55, 55);
			float p2ToBox = homeBc.getDistance(p2);
			float p2ToHome = home.distance(p2);
			float p2ToHome2 = home2.distance(p2);
			assertTrue(p2ToBox <= p2ToHome);
			assertTrue(p2ToBox <= p2ToHome2);
		}

		{
			EcefVector p3 = EcefVector.fromDegs(2, 45, 55);
			float p3ToBox = homeBc.getDistance(p3);
			float p3ToHome = home.distance(p3);
			float p3ToHome2 = home2.distance(p3);
			assertTrue(p3ToBox <= p3ToHome);
			assertTrue(p3ToBox <= p3ToHome2);
		}

		{
			EcefVector p4 = EcefVector.fromDegs(2, 45, 45);
			float p4ToBox = homeBc.getDistance(p4);
			float p4ToHome = home.distance(p4);
			float p4ToHome2 = home2.distance(p4);
			assertTrue(p4ToBox <= p4ToHome);
			assertTrue(p4ToBox <= p4ToHome2);
		}

		{
			EcefVector p1 = EcefVector.fromDegs(2, 35, 45);
			float p1ToBox = homeBc.getDistance(p1);
			float p1ToHome = home.distance(p1);
			float p1ToHome2 = home2.distance(p1);
			assertTrue(p1ToBox <= p1ToHome);
			assertTrue(p1ToBox <= p1ToHome2);
		}
			
		{
			EcefVector p2 = EcefVector.fromDegs(2, 35, 35);
			float p2ToBox = homeBc.getDistance(p2);
			float p2ToHome = home.distance(p2);
			float p2ToHome2 = home2.distance(p2);
			assertTrue(p2ToBox <= p2ToHome);
			assertTrue(p2ToBox <= p2ToHome2);
		}

		{
			EcefVector p3 = EcefVector.fromDegs(2, 45, 35);
			float p3ToBox = homeBc.getDistance(p3);
			float p3ToHome = home.distance(p3);
			float p3ToHome2 = home2.distance(p3);
			assertTrue(p3ToBox <= p3ToHome);
			assertTrue(p3ToBox <= p3ToHome2);
		}

		{
			EcefVector p4 = EcefVector.fromDegs(2, 35, 35);
			float p4ToBox = homeBc.getDistance(p4);
			float p4ToHome = home.distance(p4);
			float p4ToHome2 = home2.distance(p4);
			assertTrue(p4ToBox <= p4ToHome);
			assertTrue(p4ToBox <= p4ToHome2);
		}
	}
	
	@Test(timeout=1000)
	public void testManyPoints() {
		MTRandom rand = new MTRandom(42);
		EcefVector home = EcefVector.fromDegs(1, 45, 45);
		DimensionsRangeConstraint homeBc = home.createAnnotation();
		
		for (int count=0; count < 50000; count++)
		{
			EcefVector p1 = EcefVector.fromDegs(2, 
					55 - rand.nextFloat() * 20, 
					55 - rand.nextFloat() * 20
					);
			float p1ToBox = homeBc.getDistance(p1);
			float p1ToHome = home.distance(p1);
			assertTrue(p1ToBox <= p1ToHome + FLOAT_ERROR_MARGIN);
		}
	}
	
	@Test(timeout=2000) 
	public void testManyPointsSixHomes() {
		MTRandom rand = new MTRandom(42);
		for (int loop=0; loop < 50; loop++) {
			int homes = rand.nextInt(6)+1;	// 1-6 inclusive
			DimensionsRangeConstraint homeBc = null;
			EcefVector home[] = new EcefVector[homes];
			for (int count=0; count < homes; count++) {
				home[count] = EcefVector.fromDegs( 1,
						180 * rand.nextFloat() - 90, 
						360 * rand.nextFloat() - 180
						);
				if (count==0) {
					homeBc = home[0].createAnnotation();
				} else {
					homeBc.expand(home[count]);
				}
			}
			
			for (int count=0; count < 1000; count++) {
				EcefVector p1 = EcefVector.fromDegs( 2, 
						180 * rand.nextFloat() - 90, 
						360 * rand.nextFloat() - 180
						);
				float pToBox = homeBc.getDistance(p1);
				for (int i=0; i < homes; i++) {
					float pToHome = home[i].distance(p1);
					assertTrue(pToBox <= pToHome + FLOAT_ERROR_MARGIN);
				}
			}
		}
	}
	
}
