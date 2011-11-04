package com.wwm.db.spring.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.string.StringValue;
import com.wwm.db.whirlwind.internal.IAttribute;

public class FuzzyPropertyImpl extends AnnotationBasedPersistentProperty<FuzzyProperty> implements FuzzyProperty {

	private int attrId = 0;
	
	private final boolean isFuzzyAttribute;
	
	public FuzzyPropertyImpl(Field field,
			PropertyDescriptor propertyDescriptor,
			PersistentEntity<?, FuzzyProperty> owner,
			SimpleTypeHolder simpleTypeHolder, AttributeDefinitionService attrDefinitionService) {

		super(field, propertyDescriptor, owner, simpleTypeHolder);
		if (isTransient() || isIdProperty() || isMap() || isEntity() || isAssociation()) {
			isFuzzyAttribute = false;
			return;
		}
		
		try {
			attrId = attrDefinitionService.getAttrId(getName());
		} catch (IllegalStateException e) {
			// workaround for read-only state on attrDefinitionService at this point
			attrId = 0;
			isFuzzyAttribute = false;
			return;
		}
		Class<? extends IAttribute> dbClass = attrDefinitionService.getDbClass(attrId);
		isFuzzyAttribute = !(dbClass.equals(StringValue.class));
	}

	@Override
	protected Association<FuzzyProperty> createAssociation() {

		return null; //new Association<SimpleProperty>(this, null);
	}
	
	@Override
	public boolean isFuzzyAttribute() {
		return isFuzzyAttribute;
	}
	
	@Override
	public String toString() {
		return getName() + " : " + getType();
	}
}