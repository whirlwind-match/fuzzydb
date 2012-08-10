package org.fuzzydb.attrs.converters;

import java.util.Date;

import org.fuzzydb.attrs.simple.FloatValue;
import org.springframework.core.convert.converter.Converter;


public class FloatAttrToDateConverter implements Converter<FloatValue, Date> {

	public Date convert(FloatValue source) {
		return new Date((long)source.getValue());
	}
}
