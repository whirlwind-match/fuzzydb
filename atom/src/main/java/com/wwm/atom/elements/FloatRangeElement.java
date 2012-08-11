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
import org.fuzzydb.dto.attributes.FloatRangeAttribute;


public class FloatRangeElement extends AttributeElement {

	public FloatRangeElement(Element internal) {
		super(internal);
	}

	public FloatRangeElement(Factory factory, QName qname) {
		super(factory, qname);
	}

	public void setMin(float min) {
		setAttributeValue("min", String.valueOf(min));
	}

	public void setPref(float pref) {
		setAttributeValue("pref", String.valueOf(pref));
	}

	public void setMax(float max) {
		setAttributeValue("max", String.valueOf(max));
	}

	public float getMin() {
		return Float.valueOf( getAttributeValue("min") );
	}

	public float getPref() {
		return Float.valueOf( getAttributeValue("pref") );
	}

	public float getMax() {
		return Float.valueOf( getAttributeValue("max") );
	}

	@Override
	public Attribute<?> getAttribute() {
		return new FloatRangeAttribute(getName(), getMin(), getMax(), getPref() );
	}

}
