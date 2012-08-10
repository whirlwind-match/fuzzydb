package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.bool.BooleanValue;
import org.springframework.core.convert.converter.Converter;


public class BooleanToAttrConverter implements Converter<Boolean, BooleanValue> {

	public BooleanValue convert(Boolean source) {
		return new BooleanValue(0, source);
	}
}
