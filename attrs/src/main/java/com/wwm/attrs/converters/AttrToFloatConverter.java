package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.simple.FloatValue;

public class AttrToFloatConverter implements Converter<FloatValue, Float> {

	public Float convert(FloatValue source) {
		return source.getValue();
	}
}
