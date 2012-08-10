package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.enums.EnumMultipleValue;
import org.springframework.core.convert.converter.Converter;


public class EnumMultiValueToStringArrayConverter implements Converter<EnumMultipleValue, String[]> {

	private AttributeDefinitionService attrDefinitionService;

	public EnumMultiValueToStringArrayConverter(AttributeDefinitionService attrDefinitionService) {
		this.attrDefinitionService = attrDefinitionService;
	}

	public String[] convert(EnumMultipleValue source) {
		return attrDefinitionService.getEnumDef(source.getEnumDefId()).getStrings(source.getValues());
	}
}
