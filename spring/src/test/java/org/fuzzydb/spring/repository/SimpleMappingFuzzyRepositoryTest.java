package org.fuzzydb.spring.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.fuzzydb.spring.repository.SimpleMappingFuzzyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.data.annotation.Id;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.bool.BooleanValue;
import com.wwm.attrs.converters.WhirlwindConversionService;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.simple.FloatValue;
import com.wwm.attrs.simple.FloatRangePreference;
import com.wwm.attrs.userobjects.MappedFuzzyItem;
import com.wwm.db.DataOperations;
import com.wwm.db.Ref;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;

@RunWith(MockitoJUnitRunner.class)
public class SimpleMappingFuzzyRepositoryTest  {
	
	private SimpleMappingFuzzyRepository<FuzzyItem> repo;
	
	@Mock
	private DataOperations persister;

	private final AttributeDefinitionService attrDefinitionService = new AttrDefinitionMgr();
	
	// prime the attribute mappings so we know what ids to look for
	private final int isMaleId = attrDefinitionService.getAttrId("isMale", Boolean.class);
	private final int ageId = attrDefinitionService.getAttrId("age", Float.class);
	private final int ageRangeId = attrDefinitionService.getAttrId("ageRange", float[].class);
	private final int journeyDateId = attrDefinitionService.getAttrId("journeyDate", Date.class);
	
	
	@Captor 
	private ArgumentCaptor<MappedFuzzyItem> wwItemCaptor;
	
	
	@Before
	public void injectMocksManually() throws Exception {
		WhirlwindConversionService converter = new WhirlwindConversionService();
		new DirectFieldAccessor(converter).setPropertyValue("attrDefinitionService", attrDefinitionService);
		converter.afterPropertiesSet();
		
		repo = new SimpleMappingFuzzyRepository<FuzzyItem>(FuzzyItem.class);
		new DirectFieldAccessor(repo).setPropertyValue("persister", persister);
		new DirectFieldAccessor(repo).setPropertyValue("attrDefinitionService", attrDefinitionService);
		new DirectFieldAccessor(repo).setPropertyValue("converter", converter);
		repo.afterPropertiesSet();
	}
	
	@Test
	public void shouldConvertToWWItemOnSave() {
		// mocks
		when(persister.save((FuzzyItem)anyObject())).thenReturn(new RefImpl<FuzzyItem>(1,2,3));
		when(persister.getRef((FuzzyItem)anyObject())).thenReturn(new RefImpl<FuzzyItem>(1,2,3));
		
		
		// the action
		FuzzyItem external = new FuzzyItem();
//		external.ref = "1_2_3";
		
		external.populateTestData();
		FuzzyItem result = repo.save(external);

		// verify attributes converted
		verify(persister, times(1)).save(wwItemCaptor.capture());
		MappedFuzzyItem storedInDatabase = wwItemCaptor.getValue();
		IAttributeMap<IAttribute> attrs = storedInDatabase.getAttributeMap();
		assertThat((BooleanValue)attrs.findAttr(isMaleId),equalTo(new BooleanValue(isMaleId,false)));
		FloatValue attr = (FloatValue)attrs.findAttr(ageId);

		assertThat(attr, equalTo(new FloatValue(ageId,1.1f)));

		FloatRangePreference floatPref = (FloatRangePreference) attrs.findAttr(ageRangeId);
		assertThat(floatPref, equalTo(new FloatRangePreference(ageRangeId, 25f, 30f, 38f)));
		
		FloatValue journeyDateAttr = (FloatValue) attrs.findAttr(journeyDateId);
		assertThat(journeyDateAttr, equalTo(new FloatValue(journeyDateId, 9.4348798E11f)));
		
		
		// Verify id got set in result
		assertThat(result.ref, equalTo("1_2_3"));
	}
	
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Test 
	public void shouldConvertToMapEntriesOnRetrieve() {
		
		// mock
		MappedFuzzyItem internal = getWWItem();
		when(persister.retrieve((Ref<MappedFuzzyItem>) anyObject())).thenReturn(internal);
		when(persister.getRef((FuzzyItem)anyObject())).thenReturn(new RefImpl<FuzzyItem>(1,2,3));
		

		// the action
		FuzzyItem result = repo.findOne("1_1_1");
		
		// verify
		verify(persister, times(1)).retrieve(ArgumentCaptor.forClass(Ref.class).capture());
		Map<String, Object> map = result.attributes;
		assertEquals(Boolean.TRUE, map.get("isMale"));
		assertEquals(2.2f, map.get("age"));
		assertArrayEquals(new float[]{1.2f, 2.3f, 3.4f}, (float[])map.get("ageRange"), 0f);
		assertEquals(new Date(99,10,25).getTime(), 943488000000l);
	}
	
	
	private MappedFuzzyItem getWWItem() {
		MappedFuzzyItem item = new MappedFuzzyItem();
		item.getAttributeMap().putAttr(new BooleanValue(isMaleId, true));
		item.getAttributeMap().putAttr(new FloatValue(ageId, 2.2f));
		item.getAttributeMap().putAttr(new FloatRangePreference(ageRangeId, 1.2f, 2.3f, 3.4f));
		item.getAttributeMap().putAttr(new FloatValue(journeyDateId, 9.4348798E11f));
		return item;
	}


	public static class FuzzyItem implements Serializable {
		
		private static final long serialVersionUID = 1L;

		@Id
		String ref;
		
		Map<String, Object> attributes = new HashMap<String,Object>();
		
		@SuppressWarnings("deprecation")
		void populateTestData() {
			attributes.put("isMale", Boolean.FALSE);
			attributes.put("age", 1.1f);
			attributes.put("ageRange", new float[]{25f, 30f, 38f});
			attributes.put("journeyDate", new Date(99,10,25));
		}
	}
}
