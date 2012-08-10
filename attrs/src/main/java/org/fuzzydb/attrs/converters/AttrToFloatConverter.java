package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.simple.FloatValue;
import org.springframework.core.convert.converter.Converter;


public class AttrToFloatConverter implements Converter<FloatValue, Float> {

	public Float convert(FloatValue source) {
		return source.getValue();
	}
}
