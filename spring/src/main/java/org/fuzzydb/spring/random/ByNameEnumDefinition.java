package org.fuzzydb.spring.random;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.dto.attributes.OptionsSource;


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
		return getEnumDef().findAsString(index);
	}

	private EnumDefinition getEnumDef() {
		return attributeService.getEnumDefForAttrId(attributeService.getAttrId(attrName));
	}

	@Override
	public String getName() {
		return getEnumDef().getName();
	}
	
	@Override
	public int size() {
		return getEnumDef().size();
	}
}
