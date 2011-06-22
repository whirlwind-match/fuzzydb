package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.bool.BooleanValue;

public class BooleanToAttrConverter implements Converter<Boolean, BooleanValue> {

	public BooleanValue convert(Boolean source) {
		return new BooleanValue(0, source);
	}
}
