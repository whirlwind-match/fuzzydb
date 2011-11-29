package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.bool.BooleanValue;

public class AttrToBooleanConverter implements Converter<BooleanValue, Boolean> {

	public Boolean convert(BooleanValue source) {
		return source.isTrue();
	}

}
