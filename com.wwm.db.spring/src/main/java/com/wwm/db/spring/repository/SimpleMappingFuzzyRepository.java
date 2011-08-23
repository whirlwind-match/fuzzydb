package com.wwm.db.spring.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.converters.WhirlwindConversionService;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumMultipleValue;
import com.wwm.attrs.search.SearchSpecImpl;
import com.wwm.attrs.userobjects.BlobStoringWhirlwindItem;
import com.wwm.db.DataOperations;
import com.wwm.db.Ref;
import com.wwm.db.internal.ResultImpl;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultIterator;
import com.wwm.db.query.ResultSet;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.attributes.EnumAttribute;
import com.wwm.model.attributes.MultiEnumAttribute;

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
public class SimpleMappingFuzzyRepository<T> extends AbstractConvertingRepository<BlobStoringWhirlwindItem, T, Ref<T>> implements FuzzyRepository<T> {

	private static final String ATTRIBUTES_FIELD_NAME = "attributes";

	@Autowired
	private WhirlwindConversionService converter; 
	
	@Autowired
	private AttributeDefinitionService attrDefinitionService;

	private final boolean useDefaultNamespace;
	
	public SimpleMappingFuzzyRepository(Class<T> type) {
		super(type);
		useDefaultNamespace = false;
	}

	public SimpleMappingFuzzyRepository(Class<T> type, boolean useDefaultNamespace) {
		super(type);
		this.useDefaultNamespace = useDefaultNamespace;
	}

	public SimpleMappingFuzzyRepository(Class<T> type, boolean useDefaultNamespace, DataOperations persister) {
		super(type, persister);
		this.useDefaultNamespace = useDefaultNamespace;
	}

	@Override
	protected T fromInternal(BlobStoringWhirlwindItem internal, Ref<BlobStoringWhirlwindItem> ref) {
		T result = createInstance(internal);
		Map<String,Object> externalMap = getAttrsField(result);
		
		for( IAttribute attr : internal.getAttributeMap()) {
			addConvertedAttribute(externalMap, attr);
		}
		
		setId(result, ref);
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
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(external);
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		byte[] bytes = baos.toByteArray();
		result.setBlob(bytes);
		return result;
	}

	private void addConvertedAttribute(BlobStoringWhirlwindItem result,
			String key, Object value) {

		int id = attrDefinitionService.getAttrId(key, value.getClass());
		Class<? extends IAttribute> dbClass = attrDefinitionService.getDbClass(id);
		// If can't convert as is, wrap the value based on the db class
		if (!converter.canConvert(value.getClass(), dbClass)) {
			// TODO: Generalize this as part of using TypeDescriptor base converters
			value = wrapValue(key, value, dbClass);
		}
		IAttribute attr = converter.convert(value, dbClass);
		result.getAttributeMap().put(id, attr);
	}

	
	private Object wrapValue(String key, Object value, Class<? extends IAttribute> dbClass) {
		if (dbClass.equals(EnumExclusiveValue.class)) {
			return new EnumAttribute(key, "not used", (String)value);
		}
		if (dbClass.equals(EnumMultipleValue.class)) {
			return new MultiEnumAttribute(key, "not used", (String[])value);
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getAttrsField(T external) {
		DirectFieldAccessor directFieldAccessor = new DirectFieldAccessor(external);
		if (directFieldAccessor.isReadableProperty(ATTRIBUTES_FIELD_NAME)) {
			Object attrs = directFieldAccessor.getPropertyValue(ATTRIBUTES_FIELD_NAME); // TODO: make annotated
			return (Map<String, Object>) attrs;
		}
		return Collections.EMPTY_MAP;
	}

	@SuppressWarnings("unchecked")
	private T createInstance(BlobStoringWhirlwindItem internal) {
		try {
		if (internal.getBlob() != null) {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(internal.getBlob()));
			return (T) ois.readObject();
		}
			return type.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final Ref<BlobStoringWhirlwindItem> toInternalId(Ref<T> id) {
		// Externally we ref as Ref<T>  and we are using the real ref here
		return (Ref<BlobStoringWhirlwindItem>) id;
	}
	
	@Override
	protected Class<BlobStoringWhirlwindItem> getInternalType() {
		return BlobStoringWhirlwindItem.class;
	}
	
	@Override
	protected Iterator<Result<T>> findMatchesInternal(BlobStoringWhirlwindItem internal, String matchStyle, int maxResults) {
		SearchSpec spec = new SearchSpecImpl(BlobStoringWhirlwindItem.class, matchStyle);
		spec.setTargetNumResults(maxResults);
		spec.setAttributes(internal);
		ResultSet<Result<BlobStoringWhirlwindItem>> resultsInternal = getPersister().query(BlobStoringWhirlwindItem.class, spec);
		final ResultIterator<Result<BlobStoringWhirlwindItem>> resultIterator = resultsInternal.iterator();

		Iterator<Result<T>> iterator = new ConvertingIterator<Result<BlobStoringWhirlwindItem>,Result<T>>(resultIterator) {
			protected Result<T> convert(Result<BlobStoringWhirlwindItem> internal) {
				
				BlobStoringWhirlwindItem item = internal.getItem();
				T external = fromInternal(item, null);// FIXME !! getPersister().getRef(item));
				Result<T> result = new ResultImpl<T>(external, internal.getScore());
				return result;
			}
		};
		return iterator;
	}
	
	@Override
	protected BlobStoringWhirlwindItem merge(BlobStoringWhirlwindItem toWrite,
			Ref<BlobStoringWhirlwindItem> existingRef) {
		
		BlobStoringWhirlwindItem existing = getPersister().retrieve(existingRef);
		existing.setBlob(toWrite.getBlob());
		existing.setAttributeMap(toWrite.getAttributeMap());
		return existing;
	}

	@Override
	protected void selectNamespace() {
		getPersister().setNamespace(
				useDefaultNamespace ? "" : type.getCanonicalName()
				);
	}	
}
