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
import org.fuzzydb.dto.attributes.DateAttribute;


public class DateElement extends AttributeElement {

	public DateElement(Element internal) {
		super(internal);
	}

	public DateElement(Factory factory, QName qname) {
		super(factory, qname);
	}

	public void setYear(int year) {
		setAttributeValue("year", String.valueOf(year));
	}

	public void setMonth(int month) {
		setAttributeValue("month", String.valueOf(month));
	}

	public void setDay(int day) {
		setAttributeValue("day", String.valueOf(day));
	}

	public int getYear() {
		return Integer.valueOf( getAttributeValue("year") );
	}

	public int getMonth() {
		return Integer.valueOf( getAttributeValue("month") );
	}

	public int getDay() {
		String day = getAttributeValue("day");

		if (day == null || day.equals("")){
			return -1;
		}
		return Integer.valueOf(day);
	}

	@Override
	public Attribute<?> getAttribute() {
		return new DateAttribute(getName(), getYear(), getMonth(), getDay() );
	}
}
