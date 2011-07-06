package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.enums.EnumExclusiveValue;

public class EnumExclusiveValueToStringConverter implements Converter<EnumExclusiveValue, String> {

	private AttributeDefinitionService attrDefinitionService;

	public EnumExclusiveValueToStringConverter(AttributeDefinitionService attrDefinitionService) {
		this.attrDefinitionService = attrDefinitionService;
	}

	public String convert(EnumExclusiveValue source) {
		return attrDefinitionService.getEnumDef(source.getEnumDefId()).findAsString(source.getEnumIndex());
	}
}
