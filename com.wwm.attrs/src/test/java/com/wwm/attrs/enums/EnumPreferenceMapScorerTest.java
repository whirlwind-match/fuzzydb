package com.wwm.attrs.enums;



import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.wwm.attrs.AttrsFactory;
import com.wwm.attrs.Score;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumExclusiveScorerPreference;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumPreferenceMap;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.NodeScore;
import com.wwm.attrs.internal.ScoreConfiguration;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;



/**
 * White box test of enum preference map to score two enums against each other.
 * @author dnu
 */
public class EnumPreferenceMapScorerTest {


	ScoreConfiguration scorers;
	AttrDefinitionMgr attrMgr =  new AttrDefinitionMgr();
    private final EnumDefinition haveEnumDef = attrMgr.getEnumDefinition("SmokeStates");
    private final EnumDefinition wantEnumDef = attrMgr.getEnumDefinition("SmokePrefs");
    private final int smokeId = attrMgr.getAttrId("Smoke", EnumExclusiveValue.class);
    private final int wantSmokeId = attrMgr.getAttrId("wantSmoke", EnumExclusiveValue.class);;
    
    private final String[] haveValues =  {"NoSmoke", "SmokeStopping", "SmokeLight", "SmokeHeavy"};
    private final String[] wantValues =  {"MustNot", "PreferNot", "OkGiveUp", "Prefer"};

	@Before
	public void setUp() throws Exception {
        EnumPreferenceMap preferenceMap = new EnumPreferenceMap();

    	for (int want = 0; want < wantValues.length; want++) {
    		String wantName = wantValues[want];
            EnumExclusiveValue wantVal = wantEnumDef.getEnumValue(wantName, wantSmokeId); //new EnumExclusiveValue(wantAttrId, wantEnumDef, wantName);
            for (int have = 0; have < haveValues.length; have++) {
            	String haveName = haveValues[have];
            	EnumExclusiveValue haveVal = haveEnumDef.getEnumValue(haveName, smokeId); //new EnumExclusiveValue(haveAttrId, haveEnumDef, haveName);
                // Score increases linearly up to the diagonal
                float score = 1f - Math.abs(want - have) / (float)wantValues.length; 
                preferenceMap.add(wantVal, haveVal, score);
            }
        	preferenceMap.add(wantVal, null, 0.01f); // add want -> null score (will reduce score by factor of 0.1f when linearised for 2 attrs)
        }
        scorers = new ScoreConfiguration();
        EnumExclusiveScorerPreference enumExclusiveScorer = new EnumExclusiveScorerPreference(wantSmokeId, smokeId, preferenceMap);
        enumExclusiveScorer.setScoreNull(true);
		scorers.add(enumExclusiveScorer);
        
	}


	/**
	 * Use this for generating maps, can then easily swap to compact attr map
	 * 
	 * @return
	 */
	private IAttributeMap<IAttribute> getAttributeMap() {
		return AttrsFactory.getCardinalAttributeMap().getAttributeMap();
	}


	@Test public void testSmokingMustNot_NonSmoker() {
		IAttributeMap<IAttribute> search = getAttributeMap();
		search.putAttr( wantEnumDef.getEnumValue( wantValues[0], wantSmokeId ) );

		IAttributeMap<IAttribute> profile = getAttributeMap();
		profile.putAttr( haveEnumDef.getEnumValue( haveValues[0], smokeId ) );

		{
			NodeScore score = new NodeScore();
			
			scorers.scoreAllItemToItems(score, Score.Direction.forwards, search, profile);
			assertEquals( 1f, score.total(), 0f);
			assertEquals( 1f, score.getCount(), 0f);
		}
		
		{
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItemBothWays(score, search, profile);
			assertEquals( 1f, score.total(), 0f);
			// Only expect forwards scorer to trigger, as don't have wantSmoke on profile
			assertEquals( 1f, score.getCount(), 0f);
		}

		{
			profile.putAttr( wantEnumDef.getEnumValue( wantValues[0], wantSmokeId ) ); // Add a want to the profile so this time we trigger a reverse score
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItemBothWays(score, search, profile);
			assertEquals( 0.1f, score.total(), 0f);
			assertEquals( 2f, score.getCount(), 0f);
		}
	
	}

	@Test public void testSmokingMustNot_SmokerStopping() {
		IAttributeMap<IAttribute> search = getAttributeMap();
		search.putAttr( wantEnumDef.getEnumValue( wantValues[0], wantSmokeId ) );

		IAttributeMap<IAttribute> profile = getAttributeMap(); 
		profile.putAttr( haveEnumDef.getEnumValue( haveValues[1], smokeId ) );

		{
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItems(score, Score.Direction.forwards, search, profile);
			assertEquals( 0.75f, score.total(), 0f);
			assertEquals( 1f, score.getCount(), 0f);
		}
		
		{
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItemBothWays(score, search, profile);
			assertEquals( 0.75f, score.total(), 0f);
			// Only expect forwards scorer to trigger, as don't have wantSmoke on profile
			assertEquals( 1f, score.getCount(), 0f);
		}
		
		{
			profile.putAttr( wantEnumDef.getEnumValue( wantValues[0], wantSmokeId ) ); // Add a want to the profile so this time we trigger a reverse score
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItemBothWays(score, search, profile);
			assertEquals( (float)Math.sqrt(0.75f * 0.01f), score.total(), 0f);
			assertEquals( 2f, score.getCount(), 0f);
		}
	}
	
	@Test public void testSmokingOKGivingUp_SmokerStopping() {
        IAttributeMap<IAttribute> search = getAttributeMap();
        search.putAttr( wantEnumDef.getEnumValue( wantValues[2], wantSmokeId ) );

        IAttributeMap<IAttribute> profile = getAttributeMap(); 
		profile.putAttr( haveEnumDef.getEnumValue( haveValues[1], smokeId ) );

		{
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItems(score, Score.Direction.forwards, search, profile);
			assertEquals( 0.75f, score.total(), 0f);
			assertEquals( 1f, score.getCount(), 0f);
		}
		
		{
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItemBothWays(score, search, profile);
			assertEquals( 0.75f, score.total(), 0f);
			// Only expect forwards scorer to trigger, as don't have wantSmoke on profile
			assertEquals( 1f, score.getCount(), 0f);
		}
		
		{
			profile.putAttr( wantEnumDef.getEnumValue( wantValues[0], wantSmokeId ) ); // Add a want to the profile so this time we trigger a reverse score
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItemBothWays(score, search, profile);
			assertEquals( (float)Math.sqrt(0.75f * 0.01f), score.total(), 0f);
			assertEquals( 2f, score.getCount(), 0f);
		}
	}

	@Test public void testSmokingPreferNot_SmokerStopping() {
        IAttributeMap<IAttribute> search = getAttributeMap();
		search.putAttr( wantEnumDef.getEnumValue( wantValues[1], wantSmokeId ) );

		IAttributeMap<IAttribute> profile = getAttributeMap(); 
		profile.putAttr( haveEnumDef.getEnumValue( haveValues[1], smokeId ) );

		{
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItems(score, Score.Direction.forwards, search, profile);
			assertEquals( 1f, score.total(), 0f);
			assertEquals( 1f, score.getCount(), 0f);
		}
		
		{
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItemBothWays(score, search, profile);
			assertEquals( 1f, score.total(), 0f);
			// Only expect forwards scorer to trigger, as don't have wantSmoke on profile
			assertEquals( 1f, score.getCount(), 0f);
		}
		
		{
			profile.putAttr( wantEnumDef.getEnumValue( wantValues[0], wantSmokeId ) ); // Add a want to the profile so this time we trigger a reverse score
			NodeScore score = new NodeScore();
			scorers.scoreAllItemToItemBothWays(score, search, profile);
			assertEquals( 0.1f, score.total(), 0f);
			assertEquals( 2f, score.getCount(), 0f);
		}
	}

}
