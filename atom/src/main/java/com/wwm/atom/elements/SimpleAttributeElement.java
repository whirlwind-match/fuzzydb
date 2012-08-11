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
import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.NonIndexStringAttribute;


public class SimpleAttributeElement extends AttributeElement {

	public SimpleAttributeElement(Element internal) {
		super(internal);
	}

	public SimpleAttributeElement(Factory factory, QName qname) {
		super(factory, qname);
	}

	public String getValue() {
		return getAttributeValue("value");
	}

	public void setValue(String name) {
		setAttributeValue("value", name);
	}

	@Override
	public Attribute<?> getAttribute() {
		return new NonIndexStringAttribute( getName(), getValue() );
	}
}
