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
package org.fuzzydb.attrs.userobjects;

import java.io.Serializable;

import org.fuzzydb.client.marker.IWhirlwindItem;
import org.fuzzydb.core.marker.IAttributeContainer;
import org.fuzzydb.core.whirlwind.internal.AttributeCache;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;



public class TestAbstractWWClass implements IWhirlwindItem, Serializable {


	private static final long serialVersionUID = 1L;

	IAttributeContainer attrs = null;
	
	/**
	 */
	public TestAbstractWWClass( IAttributeContainer attrs ) {
		this.attrs = attrs;
	}


	public IAttributeMap<IAttribute> getAttributeMap() {
		return attrs.getAttributeMap();  // Server side.  Should aim to eliminate this.
	}
	
	public void setAttributeMap(IAttributeContainer attrs) {
		this.attrs = attrs;
	}	
	
	
	public Object getNominee() {
		throw new UnsupportedOperationException();
	}


	public void setNominee(Object o) {
		throw new UnsupportedOperationException();
	}

	public void mergeDuplicates(AttributeCache cache) {
		// FIXME: We should merge, as it is part of testing.
	}
}
