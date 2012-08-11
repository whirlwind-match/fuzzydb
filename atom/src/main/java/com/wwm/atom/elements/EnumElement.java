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
import org.fuzzydb.dto.attributes.EnumAttribute;


public class EnumElement extends AttributeElement {

	public EnumElement(Element internal) {
		super(internal);
	}

	public EnumElement(Factory factory, QName qname) {
		super(factory, qname);
	}

	public String getValue() {
		return getAttributeValue("value");
	}

	public void setValue(String value) {
		if (value == null) {
			throw new Error("null not allowed as a value for an enum.  Don't specify it if you want 'any'" );
		}
		setAttributeValue("value", value);
	}

	public String getEnumName() {
		return getAttributeValue("enumName");
	}

	public void setEnumName(String name) {
		setAttributeValue("enumName", name);
	}

	@Override
	public Attribute<?> getAttribute() {
		return new EnumAttribute( getName(), getEnumName(), getValue() );
	}
}
