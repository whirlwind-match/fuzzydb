package com.wwm.db.spring.mapping;

import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;


public class FuzzyPersistentEntity<T>  extends BasicPersistentEntity<T, FuzzyProperty> {

	public FuzzyPersistentEntity(TypeInformation<T> information) {
		super(information);
	}

}
