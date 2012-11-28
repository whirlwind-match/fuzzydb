package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class EnumExclusiveValueToEnumConverterFactory implements ConverterFactory<EnumExclusiveValue, Enum> {

	private final AttributeDefinitionService attrDefinitionService;

	public EnumExclusiveValueToEnumConverterFactory(AttributeDefinitionService attrDefinitionService) {
		this.attrDefinitionService = attrDefinitionService;
	}

	public <T extends Enum> Converter<EnumExclusiveValue, T> getConverter(Class<T> targetType) {
		return new EnumExclusiveValueToEnumConverter<T>(targetType);
	}

	
	private class EnumExclusiveValueToEnumConverter<T extends Enum> implements Converter<EnumExclusiveValue, T> {

		private final Class<T> enumType;

		public EnumExclusiveValueToEnumConverter(Class<T> enumType) {
			this.enumType = enumType;
		}
	
		public T convert(EnumExclusiveValue source) {
			 String asString = attrDefinitionService.getEnumDef(source.getEnumDefId()).findAsString(source.getEnumIndex());
			return (T) Enum.valueOf(this.enumType, asString);
		}
	}
}