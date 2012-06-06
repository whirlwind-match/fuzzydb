package com.wwm.db.spring.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.string.StringValue;
import com.wwm.db.spring.annotation.DerivedField;
import com.wwm.db.whirlwind.internal.IAttribute;

public class FuzzyPropertyImpl extends AnnotationBasedPersistentProperty<FuzzyProperty> implements FuzzyProperty {

	private int attrId = 0;
	
	private final boolean isFuzzyAttribute;

	private final DerivedField derivedField;
	
	
	
	public FuzzyPropertyImpl(Field field,
			PropertyDescriptor propertyDescriptor,
			FuzzyPersistentEntity<?> owner,
			SimpleTypeHolder simpleTypeHolder, AttributeDefinitionService attrDefinitionService) {

		super(field, propertyDescriptor, owner, simpleTypeHolder);
		this.derivedField = field.getAnnotation(DerivedField.class);
		if (derivedField != null) {
			owner.addDerivation(this);
		}
		
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
	public DerivedField getDerivedField() {
		return derivedField;
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