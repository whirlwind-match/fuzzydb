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
package com.wwm.attrs.layout;

import com.wwm.attrs.simple.FloatConstraint;
import com.wwm.attrs.simple.FloatValue;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;


class FloatCodec extends LayoutAttrCodec<IAttribute> {

	private static final int ENCODED_LENGTH = 1;
	
	private static FloatCodec instance = null;
	
	public static synchronized FloatCodec getInstance() {
		if (instance == null) {
			instance = new FloatCodec();
		}
		return instance;
	}


	@Override
	public void encode(LayoutAttrMap<IAttribute> map, int attrId, Object value) {

		int index = map.getIndexForFloatsWrite(attrId, ENCODED_LENGTH);
		map.getFloats()[index] = (Float)value;
	}
	
	
	@Override
	public IAttribute getDecoded(LayoutAttrMap<IAttribute> map, int attrId) {
		int index = map.getIndexQuick(attrId);
		float value = map.getFloats()[index];
		return new FloatValue(attrId, value);
	}
	
	@Override
	protected boolean consistentForInternal(LayoutAttrMap<IAttribute> map, int attrId, IAttributeConstraint constraint) {
		// get the bit field
		int index = map.getIndexQuick(attrId);
		float value = map.getFloats()[index];
		
		// test against constraint
		FloatConstraint fc = (FloatConstraint) constraint;
		return fc.contains(value);
	}
	
}
