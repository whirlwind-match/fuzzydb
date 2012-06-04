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

/**
 * A string that needs to be stored, but should not be indexed
 */
public class NonIndexStringAttribute extends UnspecifiedTypeAttribute implements NonIndexedAttribute {

	private static final long serialVersionUID = 1L;

	public NonIndexStringAttribute(String name, String value) {
		super(name, value);
	}
}
