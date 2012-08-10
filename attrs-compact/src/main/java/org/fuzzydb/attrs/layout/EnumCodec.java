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

import org.fuzzydb.attrs.enums.EnumExclusiveConstraint;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;


public class EnumCodec extends LayoutAttrCodec<IAttribute> {

	
	private static final int ENCODED_LENGTH = 1;

	private static EnumCodec instance = null;
	
	public static synchronized EnumCodec getInstance() {
		if (instance == null) {
			instance = new EnumCodec();
		}
		return instance;
	}

	

	@Override
	public void encode(LayoutAttrMap<IAttribute> map, int attrId, Object value) {
		int index = map.getIndexForIntsWrite(attrId, ENCODED_LENGTH);

		EnumExclusiveValue enumValue = (EnumExclusiveValue)value;
		int enumIndex = enumValue.getEnumIndex();
		int defId = enumValue.getEnumDefId();
		// encode into one int by shifting defId into top 16 bits
		map.getInts()[index] = enumIndex | (defId << 16);
	}
	
	
	@Override
	public IAttribute getDecoded(LayoutAttrMap<IAttribute> map, int attrId) {

		int index = map.getIndexQuick(attrId);
		int encoded = map.getInts()[index];
		short enumIndex = (short) (encoded & 0xffff);
		short defId = (short) (encoded >> 16);

		return new EnumExclusiveValue( attrId, defId, enumIndex );
	}
	
	@Override
	protected boolean consistentForInternal(LayoutAttrMap<IAttribute> map, int attrId, IAttributeConstraint constraint) {
		int index = map.getIndexQuick(attrId);
		int encoded = map.getInts()[index];
		int enumIndex = encoded & 0xffff;
		
		EnumExclusiveConstraint eec = (EnumExclusiveConstraint)constraint;
		return eec.consistent( enumIndex );

	}
}
