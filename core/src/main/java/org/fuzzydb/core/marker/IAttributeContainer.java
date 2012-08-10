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
package org.fuzzydb.core.marker;


import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;

/**
 * 
 */
public interface IAttributeContainer {

	/**
	 * All attribute maps sent to the server must be able to deliver a representation that the server expects
	 */
	IAttributeMap<IAttribute> getAttributeMap();

}
