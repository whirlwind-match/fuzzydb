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

import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumMultipleConstraint;
import com.wwm.attrs.enums.EnumMultipleValue;
import com.wwm.attrs.enums.MultiEnumReverseNodeSelector;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;


public class EnumMultiValueCodec extends LayoutAttrCodec<IAttribute> {

	private static final int ENUM_VALUES_LENGTH = (EnumDefinition.MAX_ENTRIES + 7) / 32;


	private static EnumMultiValueCodec instance = null;
	
	public static synchronized EnumMultiValueCodec getInstance() {
		if (instance == null) {
			instance = new EnumMultiValueCodec();
		}
		return instance;
	}

	

	@Override
	public void encode(LayoutAttrMap<IAttribute> map, int attrId, Object value) {
		int index = map.getIndexForIntsWrite(attrId, ENUM_VALUES_LENGTH);

		EnumMultipleValue enumValue = (EnumMultipleValue)value;

		if (ENUM_VALUES_LENGTH == 1){
			int bits = (int) enumValue.getBitSet().getWord();
			map.getInts()[index] = bits;
		} else if (ENUM_VALUES_LENGTH == 2){
			long bitfield = enumValue.getBitSet().getWord();
			map.getInts()[index] = (int) (bitfield & 0xffffffffL); // FIXME: I don't trust the sign bits here needs testing
			map.getInts()[index+1] = (int) (bitfield >>> 32);
			
		} else {
			throw new Error( "Unsupported Enum size");
		}
	}
	
	
	@Override
	public IAttribute getDecoded(LayoutAttrMap<IAttribute> map, int attrId) {

		int index = map.getIndexQuick(attrId);

		long vals = getValues(map.getInts(), index);
		
		return new EnumMultipleValue( attrId, (short)-1, vals );
	}
	
	public static long getValues(int[] ints, int index) {
		if (ENUM_VALUES_LENGTH == 1){
			return ints[index];
		} else if (ENUM_VALUES_LENGTH == 2){
			long word = ints[index+1];
			word = (word << 32) | ints[index];
			return word;
		} else {
			throw new Error( "Unsupported Enum size");
		}
	}
	
	@Override
	protected boolean consistentForInternal(LayoutAttrMap<IAttribute> map, int attrId, IAttributeConstraint constraint) {
		// get the bit field
		int index = map.getIndexQuick(attrId);
		long vals = getValues(map.getInts(), index);
		
		// test against constraint
		if (constraint instanceof MultiEnumReverseNodeSelector) {
			MultiEnumReverseNodeSelector emc = (MultiEnumReverseNodeSelector) constraint;
			return emc.consistent(vals);
		}
		EnumMultipleConstraint emc = (EnumMultipleConstraint) constraint;
		return emc.consistent(vals);
		
	}

}
