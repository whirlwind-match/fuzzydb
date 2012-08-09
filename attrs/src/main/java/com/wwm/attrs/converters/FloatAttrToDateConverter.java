package com.wwm.attrs.converters;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.simple.FloatValue;

public class FloatAttrToDateConverter implements Converter<FloatValue, Date> {

	public Date convert(FloatValue source) {
		return new Date((long)source.getValue());
	}
}
