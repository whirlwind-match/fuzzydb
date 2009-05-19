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
package com.wwm.attrs;



import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.wwm.attrs.AttributeMapFactory;
import com.wwm.attrs.ItemScore;
import com.wwm.attrs.dimensions.DimensionsRangeConstraint;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.attrs.internal.ScoreConfiguration;
import com.wwm.attrs.location.EcefVector;
import com.wwm.attrs.location.RangePreference;
import com.wwm.attrs.location.RangePreferenceScorer;
import com.wwm.db.whirlwind.SearchSpec.SearchMode;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.model.dimensions.Point3D;
import com.wwm.util.LinearScoreMapper;

/**
 * @author ac
 *
 */
public class TestScoringLocation {
    
    private int locId = 1;
    private int locWantId = 2;
    
	private EcefVector point1 = EcefVector.fromDegs(locId, 55, -5);
	private RangePreference area1 = new RangePreference(locWantId, point1, 100, true);
	private EcefVector point2 = EcefVector.fromDegs(locId, 54, -5);
	private RangePreference area2 = new RangePreference(locWantId, point2, 100, true);
	private EcefVector point3 = EcefVector.fromDegs(locId, 53, -5);
	private RangePreference area3 = new RangePreference(locWantId,point3, 100, true);
	
	private ScoreConfiguration scoreConfig = new com.wwm.attrs.internal.ScoreConfiguration();

	
    @Before
	public void setUpConfig() throws Exception {
        scoreConfig.add( new RangePreferenceScorer( locWantId, locId, new LinearScoreMapper() ) ); 
    }

	@Test
	public void testSamePoint() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(area1);
		profile.putAttr(point1);
		ItemScore score = new ItemScore();
		scoreConfig.scoreAllItemToItem(score, search, profile, SearchMode.TwoWay);
		Assert.assertEquals(1.0f / 1.1f, score.total()); // large range reduces score a bit: 1 / (1 + range/1000)
	}

	@Test
	public void testInRange() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(area1);
		profile.putAttr(point2);
		ItemScore score = new ItemScore();
		scoreConfig.scoreAllItemToItem(score, search, profile, SearchMode.TwoWay);
		Assert.assertTrue(score.total() > 0f);
		Assert.assertTrue(score.total() < 1.0f);
	}

	@Test
	public void testOutOfRange() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(area1);
		profile.putAttr(point3);
		ItemScore score = new ItemScore();
		scoreConfig.scoreAllItemToItem(score, search, profile, SearchMode.TwoWay);
		Assert.assertTrue(score.total() == 0f);
	}

	@Test
	public void test2SamePoint() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(area1);
		search.putAttr(point1);
		profile.putAttr(point1);
		profile.putAttr(area1);
		ItemScore score = new ItemScore();
		scoreConfig.scoreAllItemToItem(score, search, profile, SearchMode.TwoWay);
		Assert.assertEquals(1f / 1.1f, score.total() );
	}

	@Test
	public void test2InRange() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(point1);
		search.putAttr(area1);
		profile.putAttr(point2);
		profile.putAttr(area2);
		ItemScore score = new ItemScore();
		scoreConfig.scoreAllItemToItem(score, search, profile, SearchMode.TwoWay);
		Assert.assertTrue(score.total() > 0f);
		Assert.assertTrue(score.total() < 1.0f);
	}

	@Test public void test2OutOfRange() {
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(point1);
		search.putAttr(area1);
		profile.putAttr(point3);
		profile.putAttr(area3);
		ItemScore score = new ItemScore();
		scoreConfig.scoreAllItemToItem(score, search, profile, SearchMode.TwoWay);
		Assert.assertTrue(score.total() == 0f);
	}

	@Test public void test2OneInOneOut() {
		RangePreference area2small = new RangePreference(locWantId, point2, 10, true);
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
		search.putAttr(point1);
		search.putAttr(area1);
		profile.putAttr(point2);
		profile.putAttr(area2small);
		ItemScore score = new ItemScore();
		scoreConfig.scoreAllItemToItem(score, search, profile, SearchMode.TwoWay);
		Assert.assertTrue(score.total() == 0f);
	}
	
	@Test public void testInNode() {
	    DimensionsRangeConstraint lbv = new DimensionsRangeConstraint(locId, 
	            new Point3D(-1f,-1f,-1f), new Point3D(1f,1f,0f) );
		IConstraintMap node = AttributeMapFactory.newConstraintMap();
		node.putAttr(lbv);

		{
			IAttributeMap<IAttribute> search1 = AttributeMapFactory.newInstance(IAttribute.class);
			RangePreference areainside1 = new RangePreference(locWantId, 
					new EcefVector(locWantId, -0.8f,-0.8f,-0.8f), 100, true);
			search1.putAttr(areainside1);
			ItemScore score1 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score1, node, SearchMode.TwoWay, search1);
			Assert.assertTrue(score1.total() == 1f);
		}
		{
			IAttributeMap<IAttribute> search2 = AttributeMapFactory.newInstance(IAttribute.class);
			RangePreference areainside2 = new RangePreference(locWantId, 
					new EcefVector(locWantId, 0.8f,0.8f,-0.8f), 100, true);
			search2.putAttr(areainside2);
			ItemScore score2 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score2, node, SearchMode.TwoWay, search2);
			Assert.assertTrue(score2.total() == 1f);
		}
		{
			IAttributeMap<IAttribute> search2 = AttributeMapFactory.newInstance(IAttribute.class);
			RangePreference areainside2 = new RangePreference(locWantId,
					new EcefVector(locWantId, 0.8f,-0.8f,-0.1f), 1, true);
			search2.putAttr(areainside2);
			ItemScore score2 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score2, node, SearchMode.TwoWay, search2);
			Assert.assertTrue(score2.total() == 1f);
		}
		
	}
	@Test public void testOutNode() {
	    DimensionsRangeConstraint lbv = new DimensionsRangeConstraint(locId, 
	            new Point3D(-1f,-1f,-1f), new Point3D(1f,1f,0f));
		IConstraintMap node = AttributeMapFactory.newConstraintMap();
		node.putAttr(lbv);

		{
			IAttributeMap<IAttribute> search1 = AttributeMapFactory.newInstance(IAttribute.class);
			RangePreference areainside1 = new RangePreference(locWantId,
					new EcefVector(locWantId, -0.8f,-0.8f,0.8f), 100, true);
			search1.putAttr(areainside1);
			ItemScore score1 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score1, node, SearchMode.TwoWay, search1);
			Assert.assertTrue(score1.total() == 0f);
		}
		{
			IAttributeMap<IAttribute> search2 = AttributeMapFactory.newInstance(IAttribute.class);
			RangePreference areainside2 = new RangePreference(locWantId,
					new EcefVector(locWantId, 0.8f,0.8f,0.8f), 100, true);
			search2.putAttr(areainside2);
			ItemScore score2 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score2, node, SearchMode.TwoWay, search2);
			Assert.assertTrue(score2.total() == 0f);
		}
		{
			IAttributeMap<IAttribute> search2 = AttributeMapFactory.newInstance(IAttribute.class);
			RangePreference areainside2 = new RangePreference(locWantId,
					new EcefVector(locWantId, 0.8f,-0.8f,0.1f), 100, true);
			search2.putAttr(areainside2);
			ItemScore score2 = new ItemScore();
			scoreConfig.scoreSearchToNodeBothWays(score2, node, SearchMode.TwoWay, search2);
			Assert.assertTrue(score2.total() == 0f);
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
			EcefVector point1 = EcefVector.fromDegs(locId, 0f, 0f);
			EcefVector point2 = EcefVector.fromDegs(locId, 0f, 1f);
			
			float dist = point1.distance(point2);
			Assert.assertTrue(dist > 68f && dist < 71f);
		}
		{
			EcefVector point1 = EcefVector.fromDegs(locId, 90f, 0f);
			EcefVector point2 = EcefVector.fromDegs(locId, 90f, 1f);
			
			float dist = point1.distance(point2);
			Assert.assertTrue(dist > -0.001f && dist < 0.001f);
		}
		{
			EcefVector point1 = EcefVector.fromDegs(locId, 90f, 0f);
			EcefVector point2 = EcefVector.fromDegs(locId, 89f, 180f);
			
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
		
		EcefVector v1 = EcefVector.fromDegs(locId, 0, 0);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		Assert.assertEquals(0.0, lat, 0.0001); 
		Assert.assertEquals(0.0, lon, 0.0001); 
	}

	@Test public void testLocationConversion2() {
		
		EcefVector v1 = EcefVector.fromDegs(locId, 90 , 0);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		Assert.assertEquals(90.0, lat, 0.0001); 
		Assert.assertEquals(0.0, lon, 0.0001); 
	}

	@Test public void testLocationConversion3() {
		
		EcefVector v1 = EcefVector.fromDegs(locId, 0, 90);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		Assert.assertEquals(0.0, lat, 0.0001); 
		Assert.assertEquals(90.0, lon, 0.0001); 
	}

	@Test public void testLocationConversion4() {
		
		EcefVector v1 = EcefVector.fromDegs(locId, 27, 42);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		Assert.assertEquals(27.0, lat, 0.0001); 
		Assert.assertEquals(42.0, lon, 0.0001); 
	}
	
	@Test public void testLocationConversionNearNorthPole() {
		
		EcefVector v1 = EcefVector.fromDegs(locId, 89.0f , 0);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		Assert.assertEquals(89.0, lat, 0.0001); 
		Assert.assertEquals(0.0, lon, 0.0001); 
	}

	@Test public void testLocationConversionNearSouthPole() {
		
		EcefVector v1 = EcefVector.fromDegs(locId, -89.0f , 0);
		
		double lat = v1.getLatDegs();
		double lon = v1.getLonDegs();
		
		Assert.assertEquals(-89.0, lat, 0.0001); 
		Assert.assertEquals(0.0, lon, 0.0001); 
	}
	
}
