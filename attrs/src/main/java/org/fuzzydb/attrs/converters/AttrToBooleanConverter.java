package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.bool.BooleanValue;
import org.springframework.core.convert.converter.Converter;


public class AttrToBooleanConverter implements Converter<BooleanValue, Boolean> {

	public Boolean convert(BooleanValue source) {
		return source.isTrue();
	}

}
