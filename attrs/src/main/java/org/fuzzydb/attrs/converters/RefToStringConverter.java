package org.fuzzydb.attrs.converters;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.internal.RefImpl;
import org.springframework.core.convert.converter.Converter;


public class RefToStringConverter implements Converter<Ref<?>, String> {

	public String convert(Ref<?> ref) {
		return ((RefImpl<?>) ref).asString();
	}
}
