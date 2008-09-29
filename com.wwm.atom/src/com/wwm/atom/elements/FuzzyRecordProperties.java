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
package com.wwm.atom.elements;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;


public class FuzzyRecordProperties extends NamedElement {

    public FuzzyRecordProperties(Element internal) {
        super(internal);
    }

    public FuzzyRecordProperties(Factory factory, QName qname) {
        super(factory, qname);
    }

    public String getContentType() {
        return getAttributeValue("contentType");
    }

    public void setValue(String contentType) {
        setAttributeValue("contentType", contentType);
    }

    public String getPrivateId() {
        return getAttributeValue("privateId");
    }

    public void setPrivateId(String privateId) {
        assert privateId != null;
        setAttributeValue("privateId", privateId);
    }

}
