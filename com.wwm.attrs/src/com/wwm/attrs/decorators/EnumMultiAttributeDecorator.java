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
package com.wwm.attrs.decorators;


import com.wwm.attrs.enums.EnumMultipleValue;
import com.wwm.attrs.internal.BaseAttribute;

public class EnumMultiAttributeDecorator extends EnumAttributeDecorator {

	private static final long serialVersionUID = 1L;

	
	public EnumMultiAttributeDecorator(String name, String[] strings) {
		super( name, strings );
	}
	
	
	@Override
	public String getValueString(BaseAttribute attr) {
		EnumMultipleValue val = (EnumMultipleValue) attr;
		
		StringBuilder str = new StringBuilder();

		for( short index : val.getValues() ){
			str.append( getStringFor(index) ).append( ", " );
		}
		
		return str.substring(0, str.length() - 2);
	}

}
