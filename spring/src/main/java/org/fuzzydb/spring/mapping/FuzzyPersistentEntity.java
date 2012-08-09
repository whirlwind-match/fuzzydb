package org.fuzzydb.spring.mapping;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.util.TypeInformation;


public class FuzzyPersistentEntity<T>  extends BasicPersistentEntity<T, FuzzyProperty> {

	private final LinkedList<FuzzyProperty> derivations = new LinkedList<FuzzyProperty>();
	
	public FuzzyPersistentEntity(TypeInformation<T> information) {
		super(information);
	}

	public void addDerivation(FuzzyProperty fuzzyProperty) {
		derivations.add(fuzzyProperty);
	}

	public List<FuzzyProperty> getDerivations() {
		return derivations;
	}
	
	@Override
	public void verify() {
		super.verify();

		// check all field exist
		for (FuzzyProperty targetProperty : derivations) {
			String sourceFieldName = targetProperty.getDerivedField().value();
			FuzzyProperty sourceProperty = getPersistentProperty(sourceFieldName);
			if (sourceProperty == null) {
				throw new MappingException("No property named " + sourceFieldName + " found on type: " + getType());
			}
		}
	}
}
