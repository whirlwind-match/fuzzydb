package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.enums.EnumMultipleValue;

public class EnumMultiValueToStringArrayConverter implements Converter<EnumMultipleValue, String[]> {

	private AttributeDefinitionService attrDefinitionService;

	public EnumMultiValueToStringArrayConverter(AttributeDefinitionService attrDefinitionService) {
		this.attrDefinitionService = attrDefinitionService;
	}

	public String[] convert(EnumMultipleValue source) {
		return attrDefinitionService.getEnumDef(source.getEnumDefId()).getStrings(source.getValues());
	}
}
