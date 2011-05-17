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

import java.util.Date;

public class DateRangeAttribute extends Attribute<Date[]> {

	private Date min;
	private Date max;
	private Date pref;

	public DateRangeAttribute(String name, Date min, Date max, Date pref) {
		super(name);
		assert (min.compareTo(pref) <= 0);
		assert (pref.compareTo(max) <= 0);
		this.setMin(min);
		this.setMax(max);
		this.setPref(pref);
	}

	public void setMin(Date min) {
		this.min = min;
	}

	public Date getMin() {
		return min;
	}

	public void setMax(Date max) {
		this.max = max;
	}

	public Date getMax() {
		return max;
	}

	public void setPref(Date pref) {
		this.pref = pref;
	}

	public Date getPref() {
		return pref;
	}

	@Override
	public Date[] getValueAsObject() {
		return new Date[]{min,pref,max};
	}
}
