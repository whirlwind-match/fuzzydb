/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.indexer.db.converters;

import java.util.Date;

import org.fuzzydb.attrs.simple.FloatValue;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.DateAttribute;



public class DateConverter implements AttributeConverter {

    private static final DateConverter instance = new DateConverter();
    
    public static DateConverter getInstance() {
    	return instance;
    }

	public Class<FloatValue> getIAttributeClass() {
		return FloatValue.class;
	}

	public Class<DateAttribute> getObjectClass() {
		return DateAttribute.class;
	}

	public DateAttribute convert(String name, IAttribute attribute) {
		Date date = new Date((long) ((FloatValue) attribute).getValue());
		return new DateAttribute(name, date);
	}

	public FloatValue convertToInternal(int attrid, Attribute<?> object) {
		DateAttribute dateValue = (DateAttribute) object;
		float floatValue = dateValue.getValue().getTime();
		return new FloatValue(attrid, floatValue);
	}

}
