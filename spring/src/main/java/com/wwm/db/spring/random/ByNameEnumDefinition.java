package com.wwm.db.spring.random;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.model.attributes.OptionsSource;

public class ByNameEnumDefinition implements OptionsSource {

	private final AttributeDefinitionService attributeService;
	private final String attrName;

	public ByNameEnumDefinition(AttributeDefinitionService attributeService,
			String attrName) {
		this.attributeService = attributeService;
		this.attrName = attrName;
	}

	@Override
	public String findAsString(short index) {
		return attributeService.getEnumDefinition(attrName).findAsString(index);
	}

	@Override
	public String getName() {
		return attributeService.getEnumDefinition(attrName).getName();
	}
	
	@Override
	public int size() {
		return attributeService.getEnumDefinition(attrName).size();
	}
}
