package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.db.Ref;
import com.wwm.db.internal.RefImpl;

public class RefToStringConverter implements Converter<Ref<?>, String> {

	public String convert(Ref<?> ref) {
		return ((RefImpl<?>) ref).asString();
	}
}
