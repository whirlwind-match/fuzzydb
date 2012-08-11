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


import org.fuzzydb.attrs.bool.BooleanValue;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.BooleanAttribute;
import org.fuzzydb.dto.attributes.UnspecifiedTypeAttribute;



public class BooleanConverter implements AttributeConverter {
		
    private static final BooleanConverter instance = new BooleanConverter();
    
    public static BooleanConverter getInstance() {
    	return instance;
    }

	public Class<BooleanValue> getIAttributeClass() {
        return BooleanValue.class;
    }

    public Class<BooleanAttribute> getObjectClass() {
        return BooleanAttribute.class;
    }

    public BooleanAttribute convert(String name, IAttribute attribute) {
        return new BooleanAttribute(name, ((BooleanValue)attribute).isTrue() );
    }

    public BooleanValue convertToInternal(int attrid, Attribute<?> object) {
    	if (object instanceof UnspecifiedTypeAttribute){
    		return new BooleanValue(attrid, ((UnspecifiedTypeAttribute) object).asBoolean());
    	}
        BooleanAttribute attr = (BooleanAttribute)object;
        return new BooleanValue(attrid, attr.getValue() );
    }
}
