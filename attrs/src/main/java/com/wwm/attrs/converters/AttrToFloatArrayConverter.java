package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.simple.FloatRangePreference;

public class AttrToFloatArrayConverter implements Converter<FloatRangePreference, float[]> {

	public float[] convert(FloatRangePreference source) {
		return new float[]{source.getMin(), source.getPreferred(), source.getMax()};
	}
}
