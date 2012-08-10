package org.fuzzydb.spring.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

public class FuzzyMappingContext<E> extends AbstractMappingContext<FuzzyPersistentEntity<E>, FuzzyProperty> {

	@Autowired
	private AttributeDefinitionService attrDefinitionService;

	@Override
	protected <X> FuzzyPersistentEntity<E> createPersistentEntity(
			TypeInformation<X> typeInformation) {

		@SuppressWarnings("unchecked")
		FuzzyPersistentEntity<E> entity = (FuzzyPersistentEntity<E>) new FuzzyPersistentEntity<X>(typeInformation);
		return entity; 
	}

	@Override
	protected FuzzyProperty createPersistentProperty(Field field,
			PropertyDescriptor descriptor,
			FuzzyPersistentEntity<E> owner,
			SimpleTypeHolder simpleTypeHolder) {
		return new FuzzyPropertyImpl(field, descriptor, owner, simpleTypeHolder, attrDefinitionService);
	}

	public AttributeDefinitionService getAttrDefinitionService() {
		return attrDefinitionService;
	}

	public void setAttrDefinitionService(AttributeDefinitionService attrDefinitionService) {
		this.attrDefinitionService = attrDefinitionService;
	}
}