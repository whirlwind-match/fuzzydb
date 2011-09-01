package com.wwm.attrs.converters;

import org.springframework.core.convert.converter.Converter;

import com.wwm.db.Ref;
import com.wwm.db.internal.RefImpl;

/**
 * For import only.  It might not be advisable to use on public websites without
 * strong security.
 * 
 * @author Neale Upstone
 *
 */
public class StringToRefConverter implements Converter<String, Ref<?>> {

	public Ref<?> convert(String refAsString) {
		return RefImpl.valueOf(refAsString);
	}
}
