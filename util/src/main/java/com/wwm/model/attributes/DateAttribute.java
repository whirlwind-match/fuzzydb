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
package com.wwm.model.attributes;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateAttribute extends Attribute<Date> {

	private static final long serialVersionUID = 1L;

	private int year, month, day;

	public DateAttribute(String name, int year, int month, int day) {
		super(name);
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public DateAttribute(String name, Date date) {
		super(name);
		setValue(date);
	}

	public void setValue(Date date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
	}

	public Date getValue() {
		Calendar cal = GregorianCalendar.getInstance();
		int day = this.day == -1 ? 15 : this.day;
		cal.set(year, month, day);
		return cal.getTime();
	}

	@Override
	public String toString() {
		return String.valueOf(getValue());
	}

	@Override
	public Date getValueAsObject() {
		return getValue();
	}
}
