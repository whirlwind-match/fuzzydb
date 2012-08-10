package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.simple.FloatValue;
import org.springframework.core.convert.converter.Converter;


public class FloatToAttrConverter implements Converter<Float, FloatValue> {

	public FloatValue convert(Float source) {
		return new FloatValue(0, source);
	}
}
