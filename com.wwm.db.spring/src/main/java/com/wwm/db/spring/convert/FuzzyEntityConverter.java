package com.wwm.db.spring.convert;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.convert.EntityConverter;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.converters.WhirlwindConversionService;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumMultipleValue;
import com.wwm.attrs.userobjects.BlobStoringWhirlwindItem;
import com.wwm.db.DataOperations;
import com.wwm.db.Ref;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.spring.mapping.FuzzyMappingContext;
import com.wwm.db.spring.mapping.FuzzyPersistentEntity;
import com.wwm.db.spring.mapping.FuzzyProperty;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.attributes.EnumAttribute;
import com.wwm.model.attributes.MultiEnumAttribute;

/**
 * Maps an entity to a {@link BlobStoringWhirlwindItem} by mapping fuzzy attributes (floats, enums etc) to the fuzzy attributes
 * and undefined attributes to {@link BlobStoringWhirlwindItem#setNonIndexString(String, String)}

 * @author Neale Upstone
 *
 * @param <E>
 */
public class FuzzyEntityConverter<E>
		implements
		EntityConverter<PersistentEntity<E, FuzzyProperty>, FuzzyProperty, E, BlobStoringWhirlwindItem> {

	private final FuzzyMappingContext<E> mappingContext;
	private final WhirlwindConversionService converter;
	private final AttributeDefinitionService attrDefinitionService;
	private final DataOperations persister;
	
	public FuzzyEntityConverter(WhirlwindConversionService converter, AttributeDefinitionService attrDefinitionService, DataOperations persister) {
		this.mappingContext = new FuzzyMappingContext<E>();
		this.converter = converter;
		this.attrDefinitionService = attrDefinitionService;
		this.persister = persister;
	}
	
	@Override
	public <R extends E> R read(Class<R> type, final BlobStoringWhirlwindItem internal) {
		// mapping context can deal with subtypes of E, of which R is one
		@SuppressWarnings("unchecked")
		FuzzyPersistentEntity<R> persistentEntity = (FuzzyPersistentEntity<R>) mappingContext.getPersistentEntity(type);
		
		final BeanWrapper<FuzzyPersistentEntity<R>, R> wrapper = BeanWrapper.create(persistentEntity, null, converter);
		R result = wrapper.getBean();
		
		// It should be quicker to go through what properties we have than to go looking for
		// all properties found in the attribute map than repeated lookups based on the persistent properties
		// using persistentEntity.doWithProperties()
		for( IAttribute attr : internal.getAttributeMap()) {
			addConvertedAttribute(persistentEntity, wrapper, attr);
		}
		
		// Non-indexed String attributes
		for ( Entry<String, String> entry: internal.getNonIndexAttrs().entrySet()) {
			setProperty(wrapper, persistentEntity.getPersistentProperty(entry.getKey()), entry.getValue());
		}
		
		String value = toExternalId(persister.getRef(internal));
		setProperty(wrapper, persistentEntity.getIdProperty(), value);

		return result;
	}

	/**
	 * Map the given {@link IAttribute} to the {@link PersistentProperty} by name. 
	 */
	private <R> void addConvertedAttribute(FuzzyPersistentEntity<R> entity, BeanWrapper<FuzzyPersistentEntity<R>, R> wrapper, IAttribute attr) {
		String key = attrDefinitionService.getAttrName(attr.getAttrId());
		Object value = converter.convert(attr, attrDefinitionService.getExternalClass(attr.getAttrId()));
		
		FuzzyProperty persistentProperty = entity.getPersistentProperty(key);
		// if there isn't a field, then it must have come from attributes map
		if (persistentProperty == null) {
			addAttribute(wrapper.getBean(), key, value);
		}
		else {
			setProperty(wrapper, persistentProperty, value);
		}
	}

	private <R> void addAttribute(R target, String key, Object value) {
		getAttrsField(target).put(key, value);
	}

	private static final String ATTRIBUTES_FIELD_NAME = "attributes";

	@SuppressWarnings("unchecked")
	private Map<String, Object> getAttrsField(Object external) {
		DirectFieldAccessor directFieldAccessor = new DirectFieldAccessor(external);
		if (directFieldAccessor.isReadableProperty(ATTRIBUTES_FIELD_NAME)) {
			Object attrs = directFieldAccessor.getPropertyValue(ATTRIBUTES_FIELD_NAME); // TODO: make annotated
			return (Map<String, Object>) attrs;
		}
		return Collections.EMPTY_MAP;
	}

	@Override
	public void write(E source, final BlobStoringWhirlwindItem sink) {
		// mapping context can deal with subtypes of E, of which R is one
		FuzzyPersistentEntity<E> persistentEntity = mappingContext.getPersistentEntity(source.getClass());
		
		final BeanWrapper<FuzzyPersistentEntity<E>, E> wrapper = BeanWrapper.create(source, converter);

		// Iterate over peristent props and map as needed
		persistentEntity.doWithProperties(new PropertyHandler<FuzzyProperty>() {
			
			@Override
			public void doWithPersistentProperty(FuzzyProperty persistentProperty) {

				if (persistentProperty.isTransient()) {
					return;
				}

				Object value = getProperty(wrapper, persistentProperty);
				if (value == null) {
					return;
				}
				
				if (persistentProperty.isIdProperty()) {
					// currently dealt with by merge() - TODO Need to look at whether we need to deal with ID at this level 
//					toInternalId(value);
				}
				
				else if (persistentProperty.isMap() && persistentProperty.getComponentType().equals(String.class)) {
					addAttributesFromMap(sink, (Map<String,Object>) value);
				}
				
				// To persist strings,
				else if (persistentProperty.getType().equals(String.class)) {
					addNonFuzzyAttr(sink, persistentProperty.getName(), (String) value);
				}
				else {
					addConvertedAttribute(sink, persistentProperty.getName(), value);
				}
			}

		});
	}

	private void addNonFuzzyAttr(BlobStoringWhirlwindItem sink, String name, String value) {
		sink.setNonIndexString(name, value);
	}

	private void addAttributesFromMap(BlobStoringWhirlwindItem sink, Map<String,Object> map) {
		for (Entry<String, Object> item : map.entrySet()) {
			addConvertedAttribute(sink, item.getKey(), item.getValue());
		}
	}

	// Doesn't currently handle things that cannot be represented as fuzzy attributes
	private void addConvertedAttribute(BlobStoringWhirlwindItem result,
			String key, Object value) {

		Assert.hasLength(key);

		// Nulls and Empty strings are ignored
		if (value == null || value instanceof String && value.toString().length() == 0) {
			return;
		}

			
		// We expect the id to already be known
		int id;
		try {
			id = attrDefinitionService.getAttrId(key, value.getClass());
		} catch (IllegalStateException e) {
			// if already known but clashing, convert value to required type
			id = attrDefinitionService.getAttrId(key);
			Class<?> expected = attrDefinitionService.getExternalClass(id);
			value = converter.convert(value, expected); 
		}
		
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

	@Override
	public FuzzyMappingContext<E> getMappingContext() {
		return mappingContext;
	}

	@Override
	public ConversionService getConversionService() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	private <R> R getProperty(
			BeanWrapper<FuzzyPersistentEntity<E>, E> wrapper,
			FuzzyProperty persistentProperty) {
		try {
			return (R) wrapper.getProperty(persistentProperty);
		} catch (IllegalAccessException e) {
			throw new MappingException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new MappingException(e.getMessage(), e);
		}
	}

	protected <R> void setProperty(
			final BeanWrapper<FuzzyPersistentEntity<R>, R> wrapper,
			FuzzyProperty persistentProperty, Object value) {
		try {
			wrapper.setProperty(persistentProperty,  value);
		} catch (IllegalAccessException e) {
			throw new MappingException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new MappingException(e.getMessage(), e);
		}
	}

	protected final Ref<BlobStoringWhirlwindItem> toInternalId(String id) {
		// Externally we ref as Ref<T>  and we are using the real ref here
		return RefImpl.valueOf(id);
	}
	
	protected String toExternalId(Ref<BlobStoringWhirlwindItem> ref) {
		return ((RefImpl<BlobStoringWhirlwindItem>) ref).asString();
	}


}
