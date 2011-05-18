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

import com.wwm.attrs.simple.FloatHave;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.DateAttribute;


public class DateConverter implements AttributeConverter {

    private static final DateConverter instance = new DateConverter();
    
    public static DateConverter getInstance() {
    	return instance;
    }

	public Class<FloatHave> getIAttributeClass() {
		return FloatHave.class;
	}

	public Class<DateAttribute> getObjectClass() {
		return DateAttribute.class;
	}

	public DateAttribute convert(String name, IAttribute attribute) {
		Date date = new Date((long) ((FloatHave) attribute).getValue());
		return new DateAttribute(name, date);
	}

	public FloatHave convertToInternal(int attrid, Attribute<?> object) {
		DateAttribute dateValue = (DateAttribute) object;
		float floatValue = dateValue.getValue().getTime();
		return new FloatHave(attrid, floatValue);
	}

}
