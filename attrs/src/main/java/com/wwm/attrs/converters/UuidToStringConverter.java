package com.wwm.attrs.converters;

import java.util.UUID;

import org.springframework.core.convert.converter.Converter;

public class UuidToStringConverter implements Converter<UUID, String> {

	@Override
	public String convert(UUID uuid) {
		return uuid.toString();
	}
}
