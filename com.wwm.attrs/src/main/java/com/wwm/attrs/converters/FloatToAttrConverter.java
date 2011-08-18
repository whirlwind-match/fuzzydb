package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.simple.FloatValue;

public class FloatToAttrConverter implements Converter<Float, FloatValue> {

	public FloatValue convert(Float source) {
		return new FloatValue(0, source);
	}
}
