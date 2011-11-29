package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.model.attributes.EnumAttribute;

public class EnumAttributeToEnumExclusiveValueConverter implements Converter<EnumAttribute, EnumExclusiveValue> {

	private AttributeDefinitionService attrDefinitionService;

	public EnumAttributeToEnumExclusiveValueConverter(AttributeDefinitionService attrDefinitionService) {
		this.attrDefinitionService = attrDefinitionService;
	}

	public EnumExclusiveValue convert(EnumAttribute source) {
		
		int attrId = attrDefinitionService.getAttrId(source.getName());
		EnumDefinition def = attrDefinitionService.getEnumDefForAttrId(attrId);
		return def.getEnumValue(source.getValue(), attrId);
	}
}
