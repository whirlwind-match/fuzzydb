package com.wwm.db.tests.functional;


import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.IScoreConfiguration;
import org.fuzzydb.attrs.ManualIndexStrategy;
import org.fuzzydb.attrs.WhirlwindConfiguration;
import org.fuzzydb.attrs.bool.BooleanScorer;
import org.fuzzydb.attrs.bool.BooleanValue;
import org.fuzzydb.attrs.byteencoding.CompactBooleanScorer;
import org.fuzzydb.attrs.byteencoding.CompactSimilarFloatValueScorer;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;
import org.fuzzydb.attrs.internal.ScoreConfigurationManager;
import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.attrs.simple.FloatSplitConfiguration;
import org.fuzzydb.attrs.simple.FloatValue;
import org.fuzzydb.attrs.simple.SimilarFloatValueScorer;
import org.fuzzydb.attrs.userobjects.TestWhirlwindClass;
import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.junit.Before;
import org.junit.BeforeClass;

import com.wwm.db.BaseDatabaseTest;
import com.wwm.db.EmbeddedClientFactory;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.model.attributes.OptionsSource;


public abstract class BaseWWTest extends BaseDatabaseTest {

	protected int genderId;
	protected int wantGenderId;

	protected int floatId;

	protected int locationId;

	protected OptionsSource smokeDef;
	protected OptionsSource wantSmokeDef;
	protected int smokeId;
	protected int wantSmokeId;
    
	protected String[] haveValues =  {"NoSmoke", "SmokeStopping", "SmokeLight", "SmokeHeavy"};
	protected String[] wantValues =  {"MustNot", "PreferNot", "OkGiveUp", "Prefer"};
	
	public BaseWWTest() {
		super();
	}

 	@BeforeClass
 	static public void setPersistent() {
 		EmbeddedClientFactory.getInstance().setPersistent(true);
 	}
 	
	@Before
	public void setUpWWTest() throws Exception {
	    AttributeDefinitionService attrMgr = getAttrMgr(); // can use reference here
		genderId = attrMgr.getAttrId("Gender", BooleanValue.class);
	    wantGenderId = attrMgr.getAttrId("wantGender", BooleanValue.class);
	    floatId = attrMgr.getAttrId("Float", FloatValue.class);
	    locationId = attrMgr.getAttrId("Location", EcefVector.class);
	    smokeId =  attrMgr.getAttrId("Smoke", EnumExclusiveValue.class);
	    wantSmokeId =  attrMgr.getAttrId("wantSmoke", EnumExclusiveValue.class);
		smokeDef = attrMgr.getEnumDefinition("SmokeStates");
		wantSmokeDef = attrMgr.getEnumDefinition("SmokePref");
		insertWWConfig();
	}


	private void insertWWConfig() throws ArchException {
		WhirlwindConfiguration conf = new WhirlwindConfiguration(TestWhirlwindClass.class);
		
		{
			ManualIndexStrategy strategy = new ManualIndexStrategy("default");
			// Split on attrId 1, as a float, with expected range of 100k and priority 1
			strategy.add(new FloatSplitConfiguration(floatId, 1e5f, 0.9f));
			conf.addStrategy(strategy);
		}
		
		{
			// Just for fun, we'll have two WWIndexes built
			ManualIndexStrategy strategy = new ManualIndexStrategy( "second" ); 
			// Split on attrId 1, as a float, with expected range of 10k and priority 1
			strategy.add( new FloatSplitConfiguration( floatId, 1e4f, 0.9f) ); 
			conf.addStrategy( strategy );
		}

		ScoreConfigurationManager mgr = conf.getScoreConfigManager();
		{
			// Add a scorer config
			IScoreConfiguration config = mgr.getConfig("default");
			config.add( new SimilarFloatValueScorer(floatId, floatId, 1e4f));
			config.add( new BooleanScorer(wantGenderId, genderId) );
		}
		{
			// Add a scorer config based on CompactAttrMaps where poss
			IScoreConfiguration config = mgr.getConfig("compact");
			config.add( new CompactSimilarFloatValueScorer(floatId, floatId, 1e4f));
			config.add( new CompactBooleanScorer(wantGenderId, genderId) );

		}
		
		Transaction t = store.getAuthStore().begin();
		@SuppressWarnings("unused")
		Ref<WhirlwindConfiguration> ref = t.create(conf);
		t.commit();
	}

}