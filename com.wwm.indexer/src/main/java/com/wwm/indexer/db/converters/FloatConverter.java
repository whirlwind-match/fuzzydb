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
import com.wwm.model.attributes.FloatAttribute;
import com.wwm.model.attributes.UnspecifiedTypeAttribute;


public class FloatConverter implements AttributeConverter {

	
	private static final FloatConverter instance = new FloatConverter();

	public static FloatConverter getInstance() {
		return instance;
	}

	public Class<FloatHave> getIAttributeClass() {
        return FloatHave.class;
    }

    public Class<FloatAttribute> getObjectClass() {
        return FloatAttribute.class;
    }

    public FloatAttribute convert(String name, IAttribute attribute) {
        return new FloatAttribute( name, ((FloatHave)attribute).getValue() );
    }

    public FloatHave convertToInternal(int attrid, Attribute<?> object) {
    	if (object instanceof UnspecifiedTypeAttribute) {
            return new FloatHave(attrid, ((UnspecifiedTypeAttribute)object).asFloat() );
    	}
    	
        FloatAttribute attr = (FloatAttribute) object;
        return new FloatHave(attrid, attr.getValue() );
    }
}
