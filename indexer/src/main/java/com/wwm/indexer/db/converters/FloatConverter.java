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
import org.fuzzydb.dto.attributes.FloatAttribute;
import org.fuzzydb.dto.attributes.UnspecifiedTypeAttribute;



public class FloatConverter implements AttributeConverter {

	
	private static final FloatConverter instance = new FloatConverter();

	public static FloatConverter getInstance() {
		return instance;
	}

	public Class<FloatValue> getIAttributeClass() {
        return FloatValue.class;
    }

    public Class<FloatAttribute> getObjectClass() {
        return FloatAttribute.class;
    }

    public FloatAttribute convert(String name, IAttribute attribute) {
        return new FloatAttribute( name, ((FloatValue)attribute).getValue() );
    }

    public FloatValue convertToInternal(int attrid, Attribute<?> object) {
    	if (object instanceof UnspecifiedTypeAttribute) {
            return new FloatValue(attrid, ((UnspecifiedTypeAttribute)object).asFloat() );
    	}
    	
        FloatAttribute attr = (FloatAttribute) object;
        return new FloatValue(attrid, attr.getValue() );
    }
}
