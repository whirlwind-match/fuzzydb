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
import org.fuzzydb.dto.attributes.LocationAttribute;


// FIXME: Consider making this implement ILocationAttribute, and have a factory
// for creating AtomImpl
public class LocationElement extends AttributeElement {

	public LocationElement(Element internal) {
		super(internal);
	}

	public LocationElement(Factory factory, QName qname) {
		super(factory, qname);
	}

	public String getPostcode() {
		return getAttributeValue("postcode");
	}

	public void setPostcode(String name) {
		setAttributeValue("postcode", name);
	}

	@Override
	public Attribute<?> getAttribute() {
		return new LocationAttribute( getName(), getPostcode() );
	}
}
