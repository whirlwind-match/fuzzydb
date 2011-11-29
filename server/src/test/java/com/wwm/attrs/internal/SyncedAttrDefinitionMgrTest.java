package com.wwm.attrs.internal;


import org.junit.Before;
import org.junit.Test;


import com.wwm.attrs.bool.BooleanValue;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.SyncedAttrDefinitionMgr;
import com.wwm.db.BaseDatabaseTest;
import static org.junit.Assert.assertEquals;


/**
 * Test that when we make changes, we get to see them on a new transaction
 */
public class SyncedAttrDefinitionMgrTest extends BaseDatabaseTest {

	protected int genderId;
	protected int wantGenderId; 
	protected int smokeId;
	protected int wantSmokeId;
	protected EnumDefinition smokeDef;
	protected EnumDefinition wantSmokeDef;
	
	
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
