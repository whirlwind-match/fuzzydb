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
import org.fuzzydb.attrs.Score;
import org.fuzzydb.attrs.bool.BooleanScorer;
import org.fuzzydb.attrs.bool.BooleanValue;
import org.fuzzydb.attrs.internal.Attribute;
import org.fuzzydb.attrs.internal.ScoreConfiguration;
import org.fuzzydb.core.whirlwind.SearchSpec.SearchMode;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestScoringSex {

//    private static final AttributeFactory attributeFactory = AttributeFactory.getInstance();
	protected int sexHaveId = 1; 
	protected int sexWantId = 2; 
    protected int dogSexHaveId = 3; 
    protected int dogSexWantId = 4; 

	private ScoreConfiguration scoreConfig = new ScoreConfiguration();

    private Attribute wantman = new BooleanValue( sexWantId, true );
	private Attribute haveman = new BooleanValue( sexHaveId, true );
	private Attribute wantwoman = new BooleanValue( sexWantId, false );
	private Attribute havewoman = new BooleanValue( sexHaveId, false );

	private Attribute haveMaleDog = new BooleanValue( dogSexHaveId, true );
	private Attribute haveFemaleDog = new BooleanValue( dogSexHaveId, false );
	private Attribute wantMaleDog = new BooleanValue( dogSexWantId, true );
	
	
    @Before
	public void setUpConfig() throws Exception {
        
        // If we find a sexWant, score it against a sexHave

        scoreConfig.add( new BooleanScorer(sexWantId, sexHaveId) );

        scoreConfig.add( new BooleanScorer( dogSexWantId, dogSexHaveId ) );

//        attributeFactory.setDecorator(sexHaveId, new BooleanDecorator( "sexHave", "m", "f" ) );
//        attributeFactory.setDecorator(sexWantId, new BooleanDecorator( "sexWant", "m", "f" ) );
//        attributeFactory.setDecorator(dogSexHaveId, new BooleanDecorator( "dogSexHave", "m", "f" ) );
//        attributeFactory.setDecorator(dogSexWantId, new BooleanDecorator( "dogSexWant", "m", "f" ) );
    }

    
    @Test public void testSexMM() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantman);	// looking for man
			profile.putAttr(haveman);	// profile is man
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 1.0f);
	}
	
    
    @Test public void testSexFF() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantwoman);	// looking for woman
			profile.putAttr(havewoman);	// profile is woman
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 1.0f);
	}
	
    
    @Test public void testSexFM() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantwoman);	// looking for woman
			profile.putAttr(haveman);	// profile is man
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 0.0f);
	}
	
    
    @Test public void testSexMF() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantman);	// looking for man
			profile.putAttr(havewoman); // profile is woman
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 0.0f);
	}
	
    
	@Test public void testSexMM2FF() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantman);
			search.putAttr(havewoman);	// straight woman
			
			profile.putAttr(haveman);
			profile.putAttr(wantwoman); // straight man
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 1.0f);
	}

	
	@Test public void testSexMM2MF() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantman);
			search.putAttr(haveman);	// gay man
			
			profile.putAttr(haveman);	// straight man
			profile.putAttr(wantwoman);
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 0.0f);
	}

	
	@Test public void testSexMF2MM() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantman);
			search.putAttr(haveman);	// gay man
			
			profile.putAttr(havewoman);	// straight woman
			profile.putAttr(wantman);
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 0.0f);
	}
	
	
	@Test public void testSexFM2FF() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantwoman);
			search.putAttr(haveman);	// straight man
			
			profile.putAttr(havewoman);	// gay woman
			profile.putAttr(wantwoman);
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 0.0f);
	}
	
	
	@Test public void testSexMF2MF() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantman);
			search.putAttr(haveman);	// gay man
			
			profile.putAttr(havewoman);	// gay woman
			profile.putAttr(wantwoman);
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 0.0f);
	}
	
	
	@Test public void testSexFM2FM() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantwoman);
			search.putAttr(havewoman);	// gay woman
			
			profile.putAttr(haveman);	// gay man
			profile.putAttr(wantman);
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 0.0f);
	}

	
	@Test public void testSexMM2MM() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantman);
			search.putAttr(haveman);	// gay man
			
			profile.putAttr(haveman);	// gay man
			profile.putAttr(wantman);
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 1.0f);
	}

	
	@Test public void testSexFF2FF() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantwoman);
			search.putAttr(havewoman);	// gay woman
			
			profile.putAttr(havewoman);	// gay woman
			profile.putAttr(wantwoman);
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 1.0f);
	}

	
	@Test public void testSexMMWantDogM2MMHaveDogM() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantman);
			search.putAttr(haveman);	// gay man wanting male dog
			search.putAttr(wantMaleDog);
			
			profile.putAttr(haveman);	// gay man with male dog
			profile.putAttr(wantman);
			profile.putAttr(haveMaleDog);
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 1.0f);
	}

	
	@Test public void testSexMMWantDogM2MMHaveDogF() {
		
		IAttributeMap<IAttribute> search = AttributeMapFactory.newInstance(IAttribute.class);
		IAttributeMap<IAttribute> profile = AttributeMapFactory.newInstance(IAttribute.class);
			
			search.putAttr(wantman);
			search.putAttr(haveman);	// gay man wanting male dog
			search.putAttr(wantMaleDog);
			
			profile.putAttr(haveman);	// gay man with female dog
			profile.putAttr(wantman);
			profile.putAttr(haveFemaleDog);
			
			Score score = scoreConfig.scoreAllItemToItem(search, profile, SearchMode.TwoWay);
			Assert.assertTrue(score.total() == 0.0f);
	}
	
}
