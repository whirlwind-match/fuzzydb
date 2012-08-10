package org.fuzzydb.attrs.converters;

import java.util.UUID;

import org.springframework.core.convert.converter.Converter;

public class StringToUuidConverter implements Converter<String, UUID> {

	@Override
	public UUID convert(String uuid) {
		return UUID.fromString(uuid);
	}
}
