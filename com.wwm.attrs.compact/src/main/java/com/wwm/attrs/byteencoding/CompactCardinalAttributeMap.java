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

package com.wwm.attrs.byteencoding;
import com.wwm.db.whirlwind.AttributeRemapper;
import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.StringAttributeMap;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;


public class CompactCardinalAttributeMap extends CompactAttrMap<IAttribute> implements CardinalAttributeMap<IAttribute> {

	private static final long serialVersionUID = 1L;


	public IAttributeMap<IAttribute> getAttributeMap() {
		return this; // a fancy cast
	}

	// TODO: I think that remap is going to disappear... as we'll just support a single internal impl.
	public StringAttributeMap<IAttribute> remap(AttributeRemapper remapper) {
		throw new UnsupportedOperationException();
	}


	public IAttribute get(int key) {
		return findAttr(key);
	}
	
}
