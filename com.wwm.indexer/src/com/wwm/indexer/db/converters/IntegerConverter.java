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


import com.wwm.attrs.simple.FloatHave;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.IntegerAttribute;


public class IntegerConverter implements AttributeConverter {

    private static final IntegerConverter instance = new IntegerConverter();
    
    public static IntegerConverter getInstance() {
    	return instance;
    }

	public Class<FloatHave> getIAttributeClass() {
		return FloatHave.class;
	}

	public Class<IntegerAttribute> getObjectClass() {
		return IntegerAttribute.class;
	}

	public IntegerAttribute convert(String name, IAttribute attribute) {
		Float f = ((FloatHave) attribute).getValue();
		return new IntegerAttribute(name, f.intValue());
	}

	public FloatHave convertToInternal(int attrid, Attribute object) {
		
		IntegerAttribute intAttr = (IntegerAttribute) object;
		return new FloatHave(attrid, intAttr.getValue());
	}

}
