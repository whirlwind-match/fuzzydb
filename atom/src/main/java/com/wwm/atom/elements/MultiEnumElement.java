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

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;

import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.MultiEnumAttribute;

public class MultiEnumElement extends AttributeElement {

	public MultiEnumElement(Element internal) {
		super(internal);
	}

	public MultiEnumElement(Factory factory, QName qname) {
		super(factory, qname);
	}

	public String getEnumName() {
		return getAttributeValue("enumName");
	}

	public void setEnumName(String name) {
		setAttributeValue("enumName", name);
	}

	public void addValue(String value) {
		addSimpleExtension(AbderaElementFactory.wwmValue, value);
	}

	public ArrayList<String> getValues() {
		java.util.List<? extends ExtensibleElement> elements = getElements();
		ArrayList<String> list = new ArrayList<String>(elements.size());
		for (ExtensibleElement extensibleElement : elements) {
			list.add(extensibleElement.getText());
		}
		return list;
	}

	@Override
	public Attribute<?> getAttribute() {
		return new MultiEnumAttribute( getName(), getEnumName(), getValues().toArray( new String[0] ) );
	}
}
