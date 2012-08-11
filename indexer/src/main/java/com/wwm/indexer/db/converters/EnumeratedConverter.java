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

import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.attrs.enums.EnumValue;
import org.fuzzydb.dto.attributes.EnumeratedAttribute;


public interface EnumeratedConverter {

    public Class<? extends EnumValue> getIAttributeClass();

    public Class<? extends EnumeratedAttribute<?>> getObjectClass();

    public EnumeratedAttribute<?> convert(String name, EnumDefinition enumDef, EnumValue attribute);

    public EnumValue convertToInternal(int attrId, EnumDefinition enumDef,
            EnumeratedAttribute<?> attr);

}