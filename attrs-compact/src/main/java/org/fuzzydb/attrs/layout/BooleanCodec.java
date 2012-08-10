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
package org.fuzzydb.attrs.layout;


import org.fuzzydb.attrs.bool.BooleanConstraint;
import org.fuzzydb.attrs.bool.BooleanValue;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;

// FIXME: This is not at all complete
public class BooleanCodec extends LayoutAttrCodec<IAttribute> {

	
	private static final int ENCODED_LENGTH = 1;

	private static BooleanCodec instance = null;
		
	public static synchronized BooleanCodec getInstance() {
		if (instance == null) {
			instance = new BooleanCodec();
		}
		return instance;
	}

	

	@Override
	public void encode(LayoutAttrMap<IAttribute> map, int attrId, Object value) {
		// FIXME: For now, we don't bother with any bit shifting, thus meaning that we encode it as an int (how wasteful!)
		int index = map.getIndexForIntsWrite(attrId, ENCODED_LENGTH);

		Boolean bv = (Boolean)value;
		map.getInts()[index] = bv.booleanValue() ? 1 : 0;
	}
	
	
	@Override
	public IAttribute getDecoded(LayoutAttrMap<IAttribute> map, int attrId) {

		int index = map.getIndexQuick(attrId);
		boolean isTrue = map.getInts()[index] == 1;
		return BooleanValue.valueOf(attrId, isTrue);
	}
	
	@Override
	protected boolean consistentForInternal(LayoutAttrMap<IAttribute> map, int attrId, IAttributeConstraint constraint) {
		int index = map.getIndexQuick(attrId);
		boolean isTrue = map.getInts()[index] == 1;

		BooleanConstraint bc = (BooleanConstraint)constraint;
		return bc.consistent( isTrue );
	}
}
