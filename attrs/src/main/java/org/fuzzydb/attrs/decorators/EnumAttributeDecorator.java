/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.attrs.decorators;


import org.fuzzydb.attrs.Decorator;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;
import org.fuzzydb.attrs.internal.BaseAttribute;

public class EnumAttributeDecorator extends Decorator {

	private static final long serialVersionUID = 1L;
	private String[] strings;

	
	public EnumAttributeDecorator(String name, String[] strings) {
		super(name);
		this.strings = strings;
	}

	
	@Override
	public String getValueString(BaseAttribute attr) {
		EnumExclusiveValue val = (EnumExclusiveValue) attr;
		return getStringFor(val.getEnumIndex());
	}
	
	protected String getStringFor(int enumIndex) {
		if ( enumIndex == EnumExclusiveValue.WANT_NULL_VALUE){
			return "WANT_NULL";
		}
		return strings[enumIndex];
	}

}
