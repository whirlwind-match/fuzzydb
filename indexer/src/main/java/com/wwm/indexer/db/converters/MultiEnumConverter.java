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

import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumMultipleValue;
import com.wwm.attrs.enums.EnumValue;
import com.wwm.model.attributes.EnumeratedAttribute;
import com.wwm.model.attributes.MultiEnumAttribute;


public class MultiEnumConverter implements EnumeratedConverter {

    private static final MultiEnumConverter instance = new MultiEnumConverter();
    
    public static MultiEnumConverter getInstance() {
    	return instance;
    }

    public Class<EnumMultipleValue> getIAttributeClass() {
        return EnumMultipleValue.class;
    }

    public Class<MultiEnumAttribute> getObjectClass() {
        return MultiEnumAttribute.class;
    }

    public MultiEnumAttribute convert(String name, EnumDefinition enumDef, EnumValue attribute) {
        short[] values = ((EnumMultipleValue) attribute).getValues();

        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = enumDef.getValues().get(values[i]);
        }
        return new MultiEnumAttribute(name, enumDef.getName(), result);
    }

    public EnumValue convertToInternal(int attrId, EnumDefinition enumDef, EnumeratedAttribute<?> attr) {
        MultiEnumAttribute enumAttr = (MultiEnumAttribute) attr;
        return enumDef.getMultiEnum(enumAttr.getValues(), attrId);
    }
}
