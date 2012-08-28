package org.fuzzydb.attrs.internal;


import org.fuzzydb.attrs.bool.BooleanValue;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;
import org.fuzzydb.attrs.internal.AttrDefinitionMgr;
import org.fuzzydb.attrs.internal.SyncedAttrDefinitionMgr;
import org.fuzzydb.dto.attributes.OptionsSource;
import org.fuzzydb.server.BaseDatabaseTest;
import org.junit.Before;
import org.junit.Test;



import static org.junit.Assert.assertEquals;


/**
 * Test that when we make changes, we get to see them on a new transaction
 */
public class SyncedAttrDefinitionMgrTest extends BaseDatabaseTest {

	protected int genderId;
	protected int wantGenderId; 
	protected int smokeId;
	protected int wantSmokeId;
	protected OptionsSource smokeDef;
	protected OptionsSource wantSmokeDef;
	
	
	@Before
	public void setUpAttrIds() throws Exception {
		genderId = getAttrMgr().getAttrId("Gender", BooleanValue.class);
		wantGenderId = getAttrMgr().getAttrId("wantGender", BooleanValue.class);
		smokeId = getAttrMgr().getAttrId("Smoke", EnumExclusiveValue.class);
		wantSmokeId = getAttrMgr().getAttrId("wantSmoke", EnumExclusiveValue.class);
		
		smokeDef = getAttrMgr().getEnumDefinition("SmokeStates");
		wantSmokeDef = getAttrMgr().getEnumDefinition("SmokePref");
	}

	
	
	@Test 
	public void testCanReadSetup() throws Exception {
		AttrDefinitionMgr mgr = SyncedAttrDefinitionMgr.getFromStore(store);
		assertEquals( genderId, mgr.getAttrId("Gender") );
		assertEquals( smokeId, mgr.getAttrId("Smoke") );
	}
}
