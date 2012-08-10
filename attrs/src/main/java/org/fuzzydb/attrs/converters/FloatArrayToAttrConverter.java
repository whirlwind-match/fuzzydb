package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.simple.FloatRangePreference;
import org.springframework.core.convert.converter.Converter;


public class FloatArrayToAttrConverter implements Converter<float[], FloatRangePreference> {

	public FloatRangePreference convert(float[] source) {
		return new FloatRangePreference(0, source[0], source[1], source[2]);
	}
}
