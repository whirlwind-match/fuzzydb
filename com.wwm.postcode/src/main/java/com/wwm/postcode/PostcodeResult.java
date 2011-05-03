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
package com.wwm.postcode;

import java.io.Serializable;

public class PostcodeResult implements Serializable {

	private static final long serialVersionUID = -705047195020495531L;

	private String town;
	private String county;
	private float latitude;
	private float longitude;
	
	public PostcodeResult(String town, String county, float latitude, float longitude) {
		super();
		this.county = county;
		this.latitude = latitude;
		this.longitude = longitude;
		this.town = town;
	}
	public String getCounty() {
		return county;
	}
	public float getLatitude() {
		return latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public String getTown() {
		return town;
	}
	
	@Override
	public String toString() {
		return getTown() + ", " + getCounty() + ", (" + getLatitude() + ", " + getLongitude() + ")";
	}
}
