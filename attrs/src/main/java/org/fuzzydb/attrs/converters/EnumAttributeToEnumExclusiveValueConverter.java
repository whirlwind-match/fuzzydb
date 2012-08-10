package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;
import org.springframework.core.convert.converter.Converter;

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
