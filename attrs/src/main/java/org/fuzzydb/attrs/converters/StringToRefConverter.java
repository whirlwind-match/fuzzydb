package org.fuzzydb.attrs.converters;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.internal.RefImpl;
import org.springframework.core.convert.converter.Converter;


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
