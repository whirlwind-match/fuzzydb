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


import com.wwm.attrs.string.StringValue;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.NonIndexStringAttribute;
import com.wwm.model.attributes.UnspecifiedTypeAttribute;


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

    public StringValue convertToInternal(int attrid, Attribute object) {
        return new StringValue(attrid, ((UnspecifiedTypeAttribute)object).getValue() );
    }
}
