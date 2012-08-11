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
package org.fuzzydb.dto.attributes;

/**
 * This indicates an attribute where we received on type information (such as in the search parameters
 * of a URL query).  It is therefore stored as a string until we can infer the type
 */
public class UnspecifiedTypeAttribute extends Attribute<String> implements NonIndexedAttribute {

	private static final long serialVersionUID = 1L;

	private String value;

	public UnspecifiedTypeAttribute(String name, String value) {
		super(name);
		this.value = value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}

	public float asFloat() {
		return Float.valueOf(value);
	}

	public boolean asBoolean() {
		return "true".equals(value);
	}
	
	@Override
	public String getValueAsObject() {
		return value;
	}
}
