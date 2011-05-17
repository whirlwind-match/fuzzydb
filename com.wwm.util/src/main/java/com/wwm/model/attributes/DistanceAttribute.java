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

@Deprecated // Use a float attribute with the location and range scorer
public class DistanceAttribute extends Attribute {
	private float miles;
	private String postcode;

	// FIXME: Consider not having the postcode in here, and instead having
	// Postcode and distance as sep attrs that are combined in a scorer
	public DistanceAttribute(String name, float miles, String postcode) {
		super(name);
		this.setMiles(miles);
		this.postcode = postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setMiles(float miles) {
		this.miles = miles;
	}

	public float getMiles() {
		return miles;
	}
	
	@Override
	public Object getValueAsObject() {
		throw new UnsupportedOperationException("Deprecated class");
	}
}
