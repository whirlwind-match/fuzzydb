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

public class IntegerRangeAttribute extends Attribute {

	private float min;
	private float max;
	private float pref;

	public IntegerRangeAttribute(String name, float min, float max, float pref) {
		super(name);
		assert(min <= pref);
		assert(pref <= max);
		this.setMin(min);
		this.setMax(max);
		this.setPref(pref);
	}

	public void setMin(float min) {
		this.min = min;
	}

	public float getMin() {
		return min;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public float getMax() {
		return max;
	}

	public void setPref(float pref) {
		this.pref = pref;
	}

	public float getPref() {
		return pref;
	}



}
