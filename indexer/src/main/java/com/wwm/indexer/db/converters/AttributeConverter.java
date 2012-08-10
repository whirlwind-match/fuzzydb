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

import org.fuzzydb.core.whirlwind.internal.IAttribute;

import com.wwm.indexer.exceptions.AttributeException;
import com.wwm.model.attributes.Attribute;

public interface AttributeConverter {

    Class<?> getObjectClass();
    Class<?> getIAttributeClass();

    Attribute<?> convert(String name, IAttribute attribute);
    
    /**
     * 
     * @param attrid
     * @param object Can be an Attribute or, say a String
     * @return
     * @throws AttributeException
     */
    IAttribute convertToInternal(int attrid, Attribute<?> object) throws AttributeException;
}
