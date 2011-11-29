package org.fuzzydb.spring.repository.support;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class FuzzyEntityInformation<T, ID extends Serializable> 
	extends AbstractEntityInformation<T, ID> {

	protected Field idField;

	public FuzzyEntityInformation(Class<T> domainClass) {
		super(domainClass);

		ReflectionUtils.doWithFields(domainClass, new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				if (field.isAnnotationPresent(Id.class)) {
					ReflectionUtils.makeAccessible(field);
					idField = field;
				}
			}
		});
		if (idField == null) {
			throw new MappingException(domainClass.getCanonicalName() + " must have an @Id annotated field");
		}
	}

	@SuppressWarnings("unchecked")
	public ID getId(T entity) {
		try {
			return (ID) idField.get(entity);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public Class<ID> getIdType() {
		return (Class<ID>) idField.getType();
	}

}
