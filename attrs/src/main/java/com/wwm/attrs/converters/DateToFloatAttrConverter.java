package com.wwm.attrs.converters;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.simple.FloatValue;

public class DateToFloatAttrConverter implements Converter<Date, FloatValue> {

	public FloatValue convert(Date source) {
		return new FloatValue(0, source.getTime());
	}
}
