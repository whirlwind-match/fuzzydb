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

import com.wwm.indexer.exceptions.AttributeException;

public abstract class InboundDerivation<T> {

    private String derivedAttrName;

    abstract public T convertToInternal(int attrid, Object object) throws AttributeException;
    abstract public Class<T> getInboundClass();


    public InboundDerivation(String derivedAttrName) {
        this.derivedAttrName = derivedAttrName;
    }

    public void setDerivedAttrName(String derivedAttrName) {
        this.derivedAttrName = derivedAttrName;
    }

    public String getDerivedAttrName() {
        return derivedAttrName;
    }


}
