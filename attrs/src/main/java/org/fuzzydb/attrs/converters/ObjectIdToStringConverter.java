package org.fuzzydb.attrs.converters;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

public class ObjectIdToStringConverter implements Converter<ObjectId, String> {

	@Override
	public String convert(ObjectId objectId) {
		return objectId == null ? null : objectId.toString();
	}
}
