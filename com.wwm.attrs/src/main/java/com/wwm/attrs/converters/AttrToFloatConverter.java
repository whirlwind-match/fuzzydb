package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.simple.FloatHave;

public class AttrToFloatConverter implements Converter<FloatHave, Float> {

	public Float convert(FloatHave source) {
		return source.getValue();
	}
}
