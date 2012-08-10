package org.fuzzydb.attrs.converters;

import java.util.Date;

import org.fuzzydb.attrs.simple.FloatValue;
import org.springframework.core.convert.converter.Converter;


public class DateToFloatAttrConverter implements Converter<Date, FloatValue> {

	public FloatValue convert(Date source) {
		return new FloatValue(0, source.getTime());
	}
}
