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
package com.wwm.scratch;

import java.io.Serializable;

import com.wwm.attrs.byteencoding.CompactAttrMap;
import com.wwm.attrs.simple.FloatHave;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.marker.IWhirlwindItem;
import com.wwm.db.whirlwind.internal.AttributeCache;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;


public class CompactTestWhirlwindClass implements IWhirlwindItem, Serializable {


	private static final long serialVersionUID = 1L;

	CompactAttrMap<IAttribute> attrs = new CompactAttrMap<IAttribute>();
	
	/**
	 * Create instance with single attribute with integer value of i
	 * @param f
	 */
	public CompactTestWhirlwindClass(int attrId, float f) {
		setFloat(attrId, f);
	}



	
	public void setFloat(int attrId, float f) {
		attrs.addAttribute(attrId, f);
	}
	
	public Object getFloat(int attrId) {
		FloatHave attr = (FloatHave) attrs.findAttr(attrId);
		return attr.getValue();
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
