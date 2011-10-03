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
package com.wwm.db.whirlwind;

import com.wwm.db.marker.IAttributeContainer;
import com.wwm.model.attributes.Attribute;


/**
 * Custom AttributeMap interface for easily supporting different classes of attribute.
 *
 */
public interface StringAttributeMap<V> extends IAttributeContainer {

	public void put(Attribute obj);
}
