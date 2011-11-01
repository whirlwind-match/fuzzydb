package com.wwm.db.spring.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.SimpleTypeHolder;

public class FuzzyPropertyImpl extends AnnotationBasedPersistentProperty<FuzzyProperty> implements FuzzyProperty {

	public FuzzyPropertyImpl(Field field,
			PropertyDescriptor propertyDescriptor,
			PersistentEntity<?, FuzzyProperty> owner,
			SimpleTypeHolder simpleTypeHolder) {

		super(field, propertyDescriptor, owner, simpleTypeHolder);
	}

	@Override
	protected Association<FuzzyProperty> createAssociation() {

		return null; //new Association<SimpleProperty>(this, null);
	}
}