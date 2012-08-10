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

import org.fuzzydb.attrs.simple.FloatRangePreference;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.DateRangeAttribute;


public class DateRangeConverter implements AttributeConverter {

    private static final DateRangeConverter instance = new DateRangeConverter();
    
    public static DateRangeConverter getInstance() {
    	return instance;
    }

	public Class<FloatRangePreference> getIAttributeClass() {
		return FloatRangePreference.class;
	}

	public Class<DateRangeAttribute> getObjectClass() {
		return DateRangeAttribute.class;
	}

	public DateRangeAttribute convert(String name, IAttribute attribute) {
		FloatRangePreference pref = (FloatRangePreference) attribute;
		return new DateRangeAttribute(name, new Date((long) pref.getMin()), 
				new Date((long) pref.getPreferred()),
				new Date((long) pref.getMax()));
	}

	public FloatRangePreference convertToInternal(int attrid, Attribute<?> object) {
		DateRangeAttribute dr = (DateRangeAttribute) object;
		return new FloatRangePreference(attrid, 
				dr.getMin().getTime(),
				dr.getPref().getTime(), 
				dr.getMax().getTime());
	}
}
