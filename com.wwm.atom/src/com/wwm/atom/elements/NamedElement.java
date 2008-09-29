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
import org.apache.abdera.model.ExtensibleElementWrapper;

public abstract class NamedElement extends ExtensibleElementWrapper {

	public NamedElement(Element internal) {
		super(internal);
	}

	public NamedElement(Factory factory, QName qname) {
		super(factory, qname);
	} 
	
	public String getName() {
		return getAttributeValue("name");
	}
	
	public void setName(String name) {
		setAttributeValue("name", name);
	}
}
