package org.fuzzydb.attrs.converters;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

public class StringToObjectIdConverter implements Converter<String, ObjectId> {

	@Override
	public ObjectId convert(String objectId) {
		return StringUtils.hasText(objectId) ? new ObjectId(objectId) : null;
	}
}
