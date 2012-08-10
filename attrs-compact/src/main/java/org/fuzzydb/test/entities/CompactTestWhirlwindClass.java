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
package org.fuzzydb.test.entities;

import java.io.Serializable;

import org.fuzzydb.attrs.byteencoding.CompactAttrMap;
import org.fuzzydb.attrs.userobjects.AugmentedAttributeMap;
import org.fuzzydb.core.marker.IAttributeContainer;
import org.fuzzydb.core.whirlwind.internal.AttributeCache;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;



public class CompactTestWhirlwindClass extends AugmentedAttributeMap implements Serializable {


	private static final long serialVersionUID = 1L;

	CompactAttrMap<IAttribute> attrs = new CompactAttrMap<IAttribute>();
	
	/**
	 * Create instance with single attribute with integer value of i
	 * @param f
	 */
	public CompactTestWhirlwindClass(int attrId, float f) {
		setFloat(attrId, f);
	}



	
	public IAttributeMap<IAttribute> getAttributeMap() {
		return attrs;  // Server side.  Sound aim to eliminate this.
	}
	

	public void setAttributeMap(IAttributeContainer attrs) {
		throw new UnsupportedOperationException();
	}
	
	
	
	
	
	public Object getNominee() {
		throw new UnsupportedOperationException();
	}


	public void setNominee(Object o) {
		throw new UnsupportedOperationException();
	}

	public void mergeDuplicates(AttributeCache cache) {
		// Do nothing - we don't need merge on this type 
	}
}
