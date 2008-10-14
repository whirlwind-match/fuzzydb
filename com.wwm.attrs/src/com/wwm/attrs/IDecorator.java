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
package com.wwm.attrs;

import com.wwm.attrs.internal.BaseAttribute;

public interface IDecorator {

	public abstract String getAttrName();

	/**
	 * Default implementation for turning an attribute into a meaningful string.
	 * This impl copes with several attribute types.
	 * @param attr
	 * @return String
	 */
	public abstract String getValueString(BaseAttribute attr);

	/**
	 * Default render implementation to deal with basic types.  
	 * Note, this is called by BaseAttribute.toString() so don't call toString()
	 * @param attr
	 * @return
	 */
	public abstract String render(BaseAttribute attr);

}