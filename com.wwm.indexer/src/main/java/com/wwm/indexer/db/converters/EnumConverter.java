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
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumValue;
import com.wwm.model.attributes.EnumAttribute;
import com.wwm.model.attributes.EnumeratedAttribute;


public class EnumConverter implements EnumeratedConverter {


    private static final EnumConverter instance = new EnumConverter();
    
    public static EnumConverter getInstance() {
    	return instance;
    }

    public Class<EnumExclusiveValue> getIAttributeClass() {
        return EnumExclusiveValue.class;
    }

    public Class<EnumAttribute> getObjectClass() {
        return EnumAttribute.class;
    }

    public EnumAttribute convert(String name, EnumDefinition enumDef, EnumValue attribute) {
        EnumExclusiveValue value = (EnumExclusiveValue) attribute;
        return new EnumAttribute(name, enumDef.getName(),
                enumDef.getValues().get(value.getEnumIndex()) );
    }

    public EnumExclusiveValue convertToInternal(int attrId, EnumDefinition enumDef, EnumeratedAttribute<?> attr) {
        EnumAttribute enumAttr = (EnumAttribute) attr;
        return enumDef.getEnumValue(enumAttr.getValue(), attrId);
    }
}
