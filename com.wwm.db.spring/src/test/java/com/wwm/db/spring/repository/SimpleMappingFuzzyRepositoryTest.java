package com.wwm.db.spring.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.DirectFieldAccessor;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.bool.BooleanValue;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.simple.FloatHave;
import com.wwm.attrs.userobjects.BlobStoringWhirlwindItem;
import com.wwm.db.DataOperations;
import com.wwm.db.GenericRef;
import com.wwm.db.Ref;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.marker.IWhirlwindItem;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;

@RunWith(MockitoJUnitRunner.class)
public class SimpleMappingFuzzyRepositoryTest  {
	
	@SuppressWarnings("serial")
	public class SimpleAttrDefinitionManager extends AttrDefinitionMgr {
	}


	private SimpleMappingFuzzyRepository<FuzzyItem> repo;
	
	@Mock
	private DataOperations persister;

	private final AttributeDefinitionService attrDefinitionService = new SimpleAttrDefinitionManager();
	
	// prime the attribute mappings so we know what ids to look for
	private final int isMaleId = attrDefinitionService.getAttrId("isMale", Boolean.class);
	private final int ageId = attrDefinitionService.getAttrId("age", Float.class);
	
	
	@Captor 
	private ArgumentCaptor<IWhirlwindItem> wwItemCaptor;
	
	
	@Before
	public void injectMocksManually() throws Exception {
		repo = new SimpleMappingFuzzyRepository<FuzzyItem>(FuzzyItem.class);
		new DirectFieldAccessor(repo).setPropertyValue("persister", persister);
		new DirectFieldAccessor(repo).setPropertyValue("attrDefinitionService", attrDefinitionService);
	}
	
	@Test
	public void shouldConvertToWWItemOnSave() {
		// mocks
		
		
		// the action
		FuzzyItem external = new FuzzyItem();
		external.populateTestData();
		repo.save(external);

		// verify
		verify(persister, times(1)).save(wwItemCaptor.capture());
		IAttributeMap<IAttribute> attrs = wwItemCaptor.getValue().getAttributeMap();
		assertThat((BooleanValue)attrs.findAttr(isMaleId),equalTo(new BooleanValue(isMaleId,false)));
//		assertThat((FloatHave)attrs.findAttr(ageId),equalTo(new FloatHave(ageId,1.1f)));
		
	}
	
	
	@Test 
	public void shouldConvertToMapEntriesOnRetrieve() {
		
		// mock
		BlobStoringWhirlwindItem internal = getWWItem();
		when(persister.retrieve((GenericRef<BlobStoringWhirlwindItem>) anyObject())).thenReturn(internal);

		// the action
		FuzzyItem x = repo.findOne(new RefImpl<SimpleMappingFuzzyRepositoryTest.FuzzyItem>(0, 0, 0));
		
		// verify
		verify(persister, times(1)).retrieve(ArgumentCaptor.forClass(GenericRef.class).capture());
		Map<String, Object> map = x.attributes;
		assertEquals(Boolean.TRUE, map.get("isMale"));
		assertEquals(2.2f, map.get("age"));
	}
	
	
	
	private BlobStoringWhirlwindItem getWWItem() {
		BlobStoringWhirlwindItem item = new BlobStoringWhirlwindItem("somePrimaryKey");
		item.getAttributeMap().putAttr(new BooleanValue(isMaleId, true));
		item.getAttributeMap().putAttr(new FloatHave(ageId, 2.2f));
		return item;
	}



	public static class FuzzyItem {
		
		Map<String, Object> attributes = new HashMap<String,Object>();
		
		void populateTestData() {
			attributes.put("isMale", Boolean.FALSE);
//			attributes.put("age", 1.1f);
		}
		
	}
}
