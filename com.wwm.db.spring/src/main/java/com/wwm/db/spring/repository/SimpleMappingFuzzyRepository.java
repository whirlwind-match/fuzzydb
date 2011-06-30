package com.wwm.db.spring.repository;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.converters.WhirlwindConversionService;
import com.wwm.attrs.search.SearchSpecImpl;
import com.wwm.attrs.userobjects.BlobStoringWhirlwindItem;
import com.wwm.db.GenericRef;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultIterator;
import com.wwm.db.query.ResultSet;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.IAttribute;

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
 */
public class SimpleMappingFuzzyRepository<T> extends AbstractConvertingRepository<BlobStoringWhirlwindItem, T, GenericRef<T>> {

	// WIP: This should be configured and injected by XML namespace code
	private WhirlwindConversionService converter;
	{ 
		converter = new WhirlwindConversionService();
		try {
			converter.afterPropertiesSet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Autowired
	private AttributeDefinitionService attrDefinitionService;

	public SimpleMappingFuzzyRepository(Class<T> type) {
		super(type);
	}

	@Override
	protected T fromInternal(BlobStoringWhirlwindItem internal) {
		T result = createInstance();
		Map<String,Object> externalMap = getAttrsField(result);
		
		for( IAttribute attr : internal.getAttributeMap()) {
			addConvertedAttribute(externalMap, attr);
		}
		
		return result;
	}

	private void addConvertedAttribute(Map<String, Object> externalMap, IAttribute attr) {
		
		String key = attrDefinitionService.getAttrName(attr.getAttrId());
		Object value = converter.convert(attr, attrDefinitionService.getExternalClass(attr.getAttrId()));
		externalMap.put(key, value);
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
		IAttribute attr = converter.convert(value, attrDefinitionService.getDbClass(id));
		result.getAttributeMap().put(id, attr);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getAttrsField(T external) {
		Object attrs = new DirectFieldAccessor(external).getPropertyValue("attributes"); // TODO: make annotated
		return (Map<String, Object>) attrs;
	}

	private T createInstance() {
		try {
			return type.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final GenericRef<BlobStoringWhirlwindItem> toInternalId(GenericRef<T> id) {
		// Externally we ref as GenericRef<T>  and we are using the real ref here
		return (GenericRef<BlobStoringWhirlwindItem>) id;
	}
	
	@Override
	protected Iterator<T> findMatchesInternal(BlobStoringWhirlwindItem internal, String matchStyle, int maxResults) {
		SearchSpec spec = new SearchSpecImpl(BlobStoringWhirlwindItem.class, matchStyle);
		spec.setAttributes(internal);
		ResultSet<Result<BlobStoringWhirlwindItem>> resultsInternal = getPersister().query(BlobStoringWhirlwindItem.class, spec);
		final ResultIterator<Result<BlobStoringWhirlwindItem>> resultIterator = resultsInternal.iterator();
		Iterator<T> iterator = new Iterator<T>() {
			public boolean hasNext() {
				return resultIterator.hasNext();
			}
			public T next() {
				return fromInternal(resultIterator.next().getItem());
			}
			public void remove() {
				resultIterator.remove(); // Generally we'd not expect this to be supported
				
			}
		};
		return iterator;
	}
}
