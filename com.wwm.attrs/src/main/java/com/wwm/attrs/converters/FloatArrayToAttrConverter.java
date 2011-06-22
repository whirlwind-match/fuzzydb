package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.simple.FloatRangePreference;

public class FloatArrayToAttrConverter implements Converter<float[], FloatRangePreference> {

	public FloatRangePreference convert(float[] source) {
		return new FloatRangePreference(0, source[0], source[1], source[2]);
	}
}
