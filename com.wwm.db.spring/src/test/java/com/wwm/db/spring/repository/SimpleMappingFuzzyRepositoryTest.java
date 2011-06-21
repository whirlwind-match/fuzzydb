package com.wwm.db.spring.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
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
import com.wwm.db.DataOperations;
import com.wwm.db.marker.IWhirlwindItem;

@RunWith(MockitoJUnitRunner.class)
public class SimpleMappingFuzzyRepositoryTest  {
	
	private SimpleMappingFuzzyRepository<FuzzyItem> repo;
	
	@Mock
	private DataOperations persister;

	@Mock
	private AttributeDefinitionService attrDefinitionService;
	
	@Captor 
	private ArgumentCaptor<IWhirlwindItem> wwItemCaptor;
	
	
	@Before
	public void injectMocksManually() throws Exception {
		repo = new SimpleMappingFuzzyRepository<FuzzyItem>(FuzzyItem.class);
		new DirectFieldAccessor(repo).setPropertyValue("persister", persister);
		new DirectFieldAccessor(repo).setPropertyValue("attrDefinitionService", attrDefinitionService);
	}
	
	@Test
	public void shouldCreateWWItemOnSave() {
		// mocks
		when(attrDefinitionService.getAttrId("isMale", Boolean.class)).thenReturn(1);
		
		
		// the action
		FuzzyItem external = new FuzzyItem();
		repo.save(external);

		// verify
		verify(persister, times(1)).save(wwItemCaptor.capture());
		IWhirlwindItem item = wwItemCaptor.getValue();
		assertThat((BooleanValue)item.getAttributeMap().findAttr(1),equalTo(new BooleanValue(1,false)));
		
	}
	
	
	public static class FuzzyItem {
		
		Map<String, Object> attributes = new HashMap<String,Object>();
		{
			attributes.put("isMale", Boolean.FALSE);
//			attributes.put("Age", 1.1f);
		}
		
	}
}
