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

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.indexer.exceptions.AttributeException;

public interface SearchConverter<T extends IAttribute> {

    public Class<T> getInboundClass();

    public T convertStringToInternal(int attrId, String value) throws AttributeException;

    public String getDerivedAttrName();

}