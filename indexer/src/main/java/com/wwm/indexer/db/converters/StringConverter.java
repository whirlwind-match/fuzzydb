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


import org.fuzzydb.attrs.string.StringValue;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.NonIndexStringAttribute;
import org.fuzzydb.dto.attributes.UnspecifiedTypeAttribute;



public class StringConverter implements AttributeConverter {

    private static final StringConverter instance = new StringConverter();
    
    public static StringConverter getInstance() {
    	return instance;
    }

    public Class<StringValue> getIAttributeClass() {
        return StringValue.class;
    }

    public Class<NonIndexStringAttribute> getObjectClass() {
        return NonIndexStringAttribute.class;
    }

    public NonIndexStringAttribute convert(String name, IAttribute attribute) {
        return new NonIndexStringAttribute(name, ((StringValue) attribute).getValue() );
    }

    public StringValue convertToInternal(int attrid, Attribute<?> object) {
        return new StringValue(attrid, ((UnspecifiedTypeAttribute)object).getValue() );
    }
}
