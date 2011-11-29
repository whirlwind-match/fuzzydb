package com.wwm.db.spring.mapping;

import org.springframework.data.mapping.PersistentProperty;

import com.wwm.db.spring.annotation.DerivedField;

public interface FuzzyProperty extends PersistentProperty<FuzzyProperty> {
	
	/**
	 * Is this data that is indexed for fuzzy matching.
	 *  
	 * @return true if this property is a fuzzy matchable property
	 */
	boolean isFuzzyAttribute();
	
	
	DerivedField getDerivedField();
}