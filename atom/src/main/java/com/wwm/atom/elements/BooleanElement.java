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
import org.fuzzydb.dto.attributes.BooleanAttribute;


public class BooleanElement extends AttributeElement {

	public BooleanElement(Element internal) {
		super(internal);
	}

	public BooleanElement(Factory factory, QName qname) {
		super(factory, qname);
	}

	public final String getValue() {
		return getAttributeValue("value");
	}

	public final void setValue(String value) {
		setAttributeValue("value", value);
	}

	@Override
	public Attribute<?> getAttribute() {
		Boolean bool;
		String value = getValue();
		if ("yes".equals(value.toLowerCase())) {
			bool = Boolean.TRUE;
//		} else if ("no".equals(value.toLowerCase())){ // don't need this as Boolean.valueOf() returns false if not "true".equals(value.toLowercase())
//			bool = Boolean.FALSE;
		} else {
			bool = Boolean.valueOf(value);
		}
		return new BooleanAttribute( getName(), bool );
	}
}
