package com.wwm.db.spring.repository;


import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.bool.BooleanValue;
import com.wwm.attrs.userobjects.BlobStoringWhirlwindItem;
import com.wwm.db.GenericRef;

/**
 * A simple (PoC) Repository implementation that performs a minimal conversion to get attributes
 * in and out of the database
 *
 * Fuller support will come in time. This is a starting point to get a walking-skeleton 
 * up and err... walking.
 * 
 * @author Neale Upstone
 *
 * @param <T> the type being stored (Must contain a field: Map<String,Object> attributes for the fuzzy data)
 * @param <ID> the type for the external id
 */
public class SimpleMappingFuzzyRepository<T> extends AbstractConvertingRepository<BlobStoringWhirlwindItem, T, GenericRef<T>> {

	@Autowired
	private AttributeDefinitionService attrDefinitionService;

	public SimpleMappingFuzzyRepository(Class<T> type) {
		super(type);
	}

	@Override
	protected T fromInternal(BlobStoringWhirlwindItem internal) {
		throw new UnsupportedOperationException(); // Need to implement converters next
	}

	@Override
	protected BlobStoringWhirlwindItem toInternal(T external) {
		Map<String,Object> externalMap = getAttrsField(external);
		BlobStoringWhirlwindItem result = new BlobStoringWhirlwindItem(null);
		for (Entry<String, Object> item : externalMap.entrySet()) {
			addConvertedAttribute(result, item.getKey(), item.getValue());
		}
		return result;
	}

	private void addConvertedAttribute(BlobStoringWhirlwindItem result,
			String key, Object value) {
		int id = attrDefinitionService.getAttrId(key, value.getClass());
		
		
		result.getAttributeMap().put(id, new BooleanValue(0, false)); // fake boolean for fun
		
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getAttrsField(T external) {
		Object attrs = new DirectFieldAccessor(external).getPropertyValue("attributes"); // TODO: make annotated
		return (Map<String, Object>) attrs;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final GenericRef<BlobStoringWhirlwindItem> toInternalId(GenericRef<T> id) {
		// Externally we ref as GenericRef<T>  and we are using the real ref here
		return (GenericRef<BlobStoringWhirlwindItem>) id;
	}
}
