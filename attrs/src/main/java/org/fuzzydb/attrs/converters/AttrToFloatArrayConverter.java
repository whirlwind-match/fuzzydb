package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.simple.FloatRangePreference;
import org.springframework.core.convert.converter.Converter;


public class AttrToFloatArrayConverter implements Converter<FloatRangePreference, float[]> {

	public float[] convert(FloatRangePreference source) {
		return new float[]{source.getMin(), source.getPreferred(), source.getMax()};
	}
}
