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

public class FuzzyScoreElement extends NamedElement {

	public FuzzyScoreElement(Element internal) {
		super(internal);
	}

	public FuzzyScoreElement(Factory factory, QName qname) {
		super(factory, qname);
	} 
	
	public float getScore() {
		return Float.valueOf( getAttributeValue("score") ).floatValue(); 
	}
	
	public void setScore(float score) {
		setAttributeValue("score", String.valueOf(score));
	}
	
}
