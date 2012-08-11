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



import org.fuzzydb.attrs.AttributeMapFactory;
import org.fuzzydb.attrs.ItemScore;
import org.fuzzydb.attrs.Score;
import org.fuzzydb.attrs.dimensions.DimensionsRangeConstraint;
import org.fuzzydb.attrs.internal.IConstraintMap;
import org.fuzzydb.attrs.internal.ScoreConfiguration;
import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.attrs.location.LocationAndRangeScorer;
import org.fuzzydb.attrs.simple.FloatValue;
import org.fuzzydb.core.whirlwind.SearchSpec.SearchMode;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;
import org.fuzzydb.dto.dimensions.Point3D;
import org.fuzzydb.util.LinearScoreMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

/**
 * @author ac
 *
 */
public class TestScoringLocation {
    
    private int locPosId = 1;
    private int locWantId = 1;
    private int locWantRangeId = 3;
    
	private EcefVector point1 = EcefVector.fromDegs(locPosId, 55, -5);
	private EcefVector point2 = EcefVector.fromDegs(locPosId, 54, -5);
	private EcefVector point3 = EcefVector.fromDegs(locPosId, 53, -5);

	private FloatValue range100 = new FloatValue(locWantRangeId, 100f);
	private FloatValue range1 = new FloatValue(locWantRangeId, 1f);
	
	private ScoreConfiguration scoreConfig = new org.fuzzydb.attrs.internal.ScoreConfiguration();

	
    @Before
	public void setUpConfig() throws Exception {
        scoreConfig.add( new LocationAndRangeScorer( locPosId, locWantRangeId, locPosId, new LinearScoreMapper() ) ); 
    }

	@Test
	public void testSamePoint() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(point1);
		search.putAttr(range100);
		profile.putAttr(point1);
		Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
		Assert.assertEquals(1.0f / 1.1f, score.total(), 0.001f); // large range reduces score a bit: 1 / (1 + range/1000)
	}

	@Test
	public void testInRange() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(point1);
		search.putAttr(range100);
		profile.putAttr(point2);
		Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
		Assert.assertTrue(score.total() > 0f);
		Assert.assertTrue(score.total() < 1.0f);
	}

	@Test
	public void testOutOfRange() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(point1);
		search.putAttr(range100);
		profile.putAttr(point3);
		Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
		Assert.assertTrue(score.total() == 0f);
	}

	@Test
	public void test2SamePoint() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(point1);
		search.putAttr(range100);
		profile.putAttr(point1);
		profile.putAttr(range100);
		Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
		Assert.assertEquals(1f / 1.1f, score.total(), 0.001f);
	}

	@Test
	public void test2InRange() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(point1);
		search.putAttr(range100);
		profile.putAttr(point2);
		profile.putAttr(range100);
		Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
		Assert.assertTrue(score.total() > 0f);
		Assert.assertTrue(score.total() < 1.0f);
	}

	@Test public void test2OutOfRange() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(point1);
		search.putAttr(range100);
		profile.putAttr(point3);
		profile.putAttr(range100);
		Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
		Assert.assertTrue(score.total() == 0f);
	}

	@Test public void test2OneInOneOut() {
		FloatValue range2small = new FloatValue(locWantRangeId, 10f);
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(point1);
		search.putAttr(range100);
		profile.putAttr(point2);
		profile.putAttr(range2small);
		Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
		Assert.assertTrue(score.total() == 0f);
	}
	
	@Test public void testInNode() {
	    DimensionsRangeConstraint lbv = new DimensionsRangeConstraint(locPosId, 
	            new Point3D(-1f,-1f,-1f), new Point3D(1f,1f,0f) );
		IConstraintMap node = AttributeMapFactory.newConstraintMap();
		node.putAttr(lbv);

		{
			IAttributeMap<IAttribute> search1 = AttributeMapFactory.newInstance(IAttribute.class);
			EcefVector areainside1 = new EcefVector(locWantId, -0.8f,-0.8f,-0.8f);
			search1.putAttr(areainside1);
			search1.putAttr(range100);
			ItemScore score1 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score1, node, SearchMode.TwoWay, search1);
			Assert.assertTrue(score1.total() == 1f);
		}
		{
			IAttributeMap<IAttribute> search2 = AttributeMapFactory.newInstance(IAttribute.class);
			EcefVector areainside2 = new EcefVector(locWantId, 0.8f,0.8f,-0.8f);
			search2.putAttr(areainside2);
			search2.putAttr(range100);
			ItemScore score2 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score2, node, SearchMode.TwoWay, search2);
			Assert.assertTrue(score2.total() == 1f);
		}
		{
			IAttributeMap<IAttribute> search2 = AttributeMapFactory.newInstance(IAttribute.class);
			EcefVector areainside2 = new EcefVector(locWantId, 0.8f,-0.8f,-0.1f);
			search2.putAttr(areainside2);
			search2.putAttr(range1); // NOTE: smaller range
			ItemScore score2 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score2, node, SearchMode.TwoWay, search2);
			Assert.assertTrue(score2.total() == 1f);
		}
		
	}
	@Test public void testOutNode() {
	    DimensionsRangeConstraint lbv = new DimensionsRangeConstraint(locPosId, 
	            new Point3D(-1f,-1f,-1f), new Point3D(1f,1f,0f));
		IConstraintMap node = AttributeMapFactory.newConstraintMap();
		node.putAttr(lbv);

		{
			IAttributeMap<IAttribute> search1 = AttributeMapFactory.newInstance(IAttribute.class);
			EcefVector areainside1 = new EcefVector(locWantId, -0.8f,-0.8f,0.8f);
			search1.putAttr(areainside1);
			search1.putAttr(range100);
			ItemScore score1 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score1, node, SearchMode.TwoWay, search1);
			Assert.assertEquals(0f, score1.total(), 0f);
		}
		{
			IAttributeMap<IAttribute> search2 = AttributeMapFactory.newInstance(IAttribute.class);
			EcefVector areainside2 = new EcefVector(locWantId, 0.8f,0.8f,0.8f);
			search2.putAttr(areainside2);
			search2.putAttr(range100);
			ItemScore score2 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score2, node, SearchMode.TwoWay, search2);
			Assert.assertEquals(0f, score2.total(), 0f);
		}
		{
			IAttributeMap<IAttribute> search2 = AttributeMapFactory.newInstance(IAttribute.class);
			EcefVector areainside2 = new EcefVector(locWantId, 0.8f,-0.8f,0.1f);
			search2.putAttr(areainside2);
			search2.putAttr(range100);
			ItemScore score2 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score2, node, SearchMode.TwoWay, search2);
			Assert.assertEquals(0f, score2.total(), 0f);
		}
		
	}
	
//	@Test public void testNodeSelection() {
//		BranchNode root = new BranchNode();
//		
//		root.createLeaf(new DimensionsRangeConstraint(locId, new Point3D(-1f, -1f, -1f), new Point3D(1f,1f,0f)), locId);
//		root.createLeaf(new DimensionsRangeConstraint(locId, new Point3D(-1f, -1f,  0f), new Point3D(1f,1f,1f)), locId);
//		
//		EcefVector leftpoint = new EcefVector(locId, -0.5f, -0.5f, -0.5f);
//		EcefVector rightpoint = new EcefVector(locId, 0.5f,  0.5f,  0.5f);
//
//		SearchAttributeContainer leftatts = new SearchAttributeContainer();
//		leftatts.putAttr(leftpoint);
//		SearchAttributeContainer rightatts = new SearchAttributeContainer();
//		rightatts.putAttr(rightpoint);
//		
//		Branch branches[] = root.getNodes();
//		
//		Assert.assertTrue(branches[0].getConstraint().consistent((Attribute)leftatts.findAttr(locId)));
//		Assert.assertTrue(branches[1].getConstraint().consistent((Attribute)rightatts.findAttr(locId)));
//		Assert.assertFalse(branches[0].getConstraint().consistent((Attribute)rightatts.findAttr(locId)));
//		Assert.assertFalse(branches[1].getConstraint().consistent((Attribute)leftatts.findAttr(locId)));
//	}
	
	@Test public void testDistance() {
		{
			EcefVector point1 = EcefVector.fromDegs(locPosId, 0f, 0f);
			EcefVector point2 = EcefVector.fromDegs(locPosId, 0f, 1f);
			
			float dist = point1.distance(point2);
			Assert.assertTrue(dist > 68f && dist < 71f);
		}
		{
			EcefVector point1 = EcefVector.fromDegs(locPosId, 90f, 0f);
			EcefVector point2 = EcefVector.fromDegs(locPosId, 90f, 1f);
			
			float dist = point1.distance(point2);
			Assert.assertTrue(dist > -0.001f && dist < 0.001f);
		}
		{
			EcefVector point1 = EcefVector.fromDegs(locPosId, 90f, 0f);
			EcefVector point2 = EcefVector.fromDegs(locPosId, 89f, 180f);
			
			float dist = point1.distance(point2);
			Assert.assertTrue(dist > 68f && dist < 71f);
		}
	}
	/*
	@Test public void testAcos() {
		float lookup[] = new float[(int)(2000f * Math.PI)];
		
		for (int i = 0; i < (int)(2000f * Math.PI); i++) {
			lookup[i] = (float)Math.acos(((double)i) / 1000f);
		}
		Random random = new Random( 22101970 );
		float result = 0f;
	    Stopwatch addTimer = new Stopwatch();
		
	    addTimer.start();
		for (int i=0; i<10000000; i++) {
			//result += random.nextFloat()*Math.PI*1000;
			//result += lookup[(int)(random.nextFloat() * Math.PI * 1000f)];	// 5798 // 0.5ms per 1000
			result += Math.acos(random.nextFloat() * Math.PI);	// 13991 // 1.5ms per 1000
		}
		
		addTimer.stop();
		System.out.println( "Time (ms): " + (addTimer.getValue() - 1091f));
		
	}
	*/
	
	@Test public void testLocationConversion1() {
		
		EcefVector v1 = EcefVector.fromDegs(locPosId, 0, 0);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		Assert.assertEquals(0.0, lat, 0.0001); 
		Assert.assertEquals(0.0, lon, 0.0001); 
	}

	@Test public void testLocationConversion2() {
		
		EcefVector v1 = EcefVector.fromDegs(locPosId, 90 , 0);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		assertEquals(90.0, lat, 0.0001); 
		assertEquals(0.0, lon, 0.0001); 
	}

	@Test public void testLocationConversion3() {
		
		EcefVector v1 = EcefVector.fromDegs(locPosId, 0, 90);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		assertEquals(0.0, lat, 0.0001); 
		assertEquals(90.0, lon, 0.0001); 
	}

	@Test public void testLocationConversion4() {
		
		EcefVector v1 = EcefVector.fromDegs(locPosId, 27, 42);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		assertEquals(27.0, lat, 0.0001); 
		assertEquals(42.0, lon, 0.0001); 
	}
	
	@Test public void testLocationConversionNearNorthPole() {
		
		EcefVector v1 = EcefVector.fromDegs(locPosId, 89.0f , 0);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		assertEquals(89.0, lat, 0.0001); 
		assertEquals(0.0, lon, 0.0001); 
	}

	@Test public void testLocationConversionNearSouthPole() {
		
		EcefVector v1 = EcefVector.fromDegs(locPosId, -89.0f , 0);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		assertEquals(-89.0, lat, 0.0001); 
		assertEquals(0.0, lon, 0.0001); 
	}
	
}
