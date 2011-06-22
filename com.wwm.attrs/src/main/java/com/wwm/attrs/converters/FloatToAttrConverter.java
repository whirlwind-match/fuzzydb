package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.simple.FloatHave;

public class FloatToAttrConverter implements Converter<Float, FloatHave> {

	public FloatHave convert(Float source) {
		return new FloatHave(0, source);
	}
}
