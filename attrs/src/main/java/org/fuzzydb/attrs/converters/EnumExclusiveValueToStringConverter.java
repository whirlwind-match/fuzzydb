package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;
import org.springframework.core.convert.converter.Converter;


public class EnumExclusiveValueToStringConverter implements Converter<EnumExclusiveValue, String> {

	private AttributeDefinitionService attrDefinitionService;

	public EnumExclusiveValueToStringConverter(AttributeDefinitionService attrDefinitionService) {
		this.attrDefinitionService = attrDefinitionService;
	}

	public String convert(EnumExclusiveValue source) {
		return attrDefinitionService.getEnumDef(source.getEnumDefId()).findAsString(source.getEnumIndex());
	}
}
