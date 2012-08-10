package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.attrs.enums.EnumMultipleValue;
import org.springframework.core.convert.converter.Converter;

import com.wwm.model.attributes.MultiEnumAttribute;

public class MultiEnumAttributeToEnumMultipleValueConverter implements Converter<MultiEnumAttribute, EnumMultipleValue> {

	private AttributeDefinitionService attrDefinitionService;

	public MultiEnumAttributeToEnumMultipleValueConverter(AttributeDefinitionService attrDefinitionService) {
		this.attrDefinitionService = attrDefinitionService;
	}

	public EnumMultipleValue convert(MultiEnumAttribute source) {
		
		int attrId = attrDefinitionService.getAttrId(source.getName());
		EnumDefinition def = attrDefinitionService.getEnumDefForAttrId(attrId);
		return def.getMultiEnum(source.getValues(), attrId);
	}
}
