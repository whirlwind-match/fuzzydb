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


import org.fuzzydb.attrs.simple.FloatValue;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.IntegerAttribute;



public class IntegerConverter implements AttributeConverter {

    private static final IntegerConverter instance = new IntegerConverter();
    
    public static IntegerConverter getInstance() {
    	return instance;
    }

	public Class<FloatValue> getIAttributeClass() {
		return FloatValue.class;
	}

	public Class<IntegerAttribute> getObjectClass() {
		return IntegerAttribute.class;
	}

	public IntegerAttribute convert(String name, IAttribute attribute) {
		Float f = ((FloatValue) attribute).getValue();
		return new IntegerAttribute(name, f.intValue());
	}

	public FloatValue convertToInternal(int attrid, Attribute<?> object) {
		
		IntegerAttribute intAttr = (IntegerAttribute) object;
		return new FloatValue(attrid, intAttr.getValue());
	}

}
