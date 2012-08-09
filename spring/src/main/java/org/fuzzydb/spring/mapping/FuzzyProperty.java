package org.fuzzydb.spring.mapping;

import org.fuzzydb.spring.annotation.DerivedField;
import org.springframework.data.mapping.PersistentProperty;


public interface FuzzyProperty extends PersistentProperty<FuzzyProperty> {
	
	/**
	 * Is this data that is indexed for fuzzy matching.
	 *  
	 * @return true if this property is a fuzzy matchable property
	 */
	boolean isFuzzyAttribute();
	
	
	DerivedField getDerivedField();
}