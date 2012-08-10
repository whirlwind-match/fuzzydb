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


import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.attrs.enums.EnumExclusiveConstraint;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;

public class EnumExclConstraintCodec extends LayoutConstraintCodec {

	private static final int ENUM_VALUES_LENGTH = (EnumDefinition.MAX_ENTRIES + 7) / 32;


	public static final int LENGTH = ENUM_VALUES_LENGTH;


	private static EnumExclConstraintCodec instance = null;
	
	public static synchronized EnumExclConstraintCodec getInstance() {
		if (instance == null) {
			instance = new EnumExclConstraintCodec();
		}
		return instance;
	}

	

	@Override
	public void encode(LayoutAttrMap<IAttributeConstraint> map, int attrId, Object value) {
		int index = map.getIndexForIntsWrite(attrId, LENGTH);

		EnumExclusiveConstraint constraint = (EnumExclusiveConstraint)value;
		
		if (constraint.isIncludesNotSpecified()){
			((LayoutConstraintMap) map).setIncludesNotSpecified(attrId);
		}

		long word = constraint.getBitSet().getWord();
		if (ENUM_VALUES_LENGTH == 1){
			int bits = (int) (word & 0xFFFFFFFFL); // FIXME: Does this (cast) work properly on if bit 31 is set! (sign bit)
			map.getInts()[index] = bits;
//		} else if (ENUM_VALUES_LENGTH == 2){
//			long bitfield = (words.length == 0 ? 0 : words[0]);
//			map.getInts()[index] = (int) (bitfield & 0xffffffffL); // FIXME: I don't trust the sign bits here needs testing
//			map.getInts()[index + 1] = (int) (bitfield >>> 32);
//			
		} else {
			throw new RuntimeException( "Unsupported Enum size");
		}
	}
	
	
	@Override
	public IAttributeConstraint getDecoded(LayoutAttrMap<IAttributeConstraint> map, int attrId) {

		int index = map.getIndexQuick(attrId);
		boolean inclNS = ((LayoutConstraintMap) map).getIncludesNotSpecified(attrId);

		long bits = getBits(map.getInts(), index);
		
		return new EnumExclusiveConstraint( attrId, (short)-1, bits, inclNS );
	}
	
	public static long getBits(int[] ints, int index) {
		if (ENUM_VALUES_LENGTH == 1){
			return ints[index];
//		} else if (ENUM_VALUES_LENGTH == 2){
//			long word = ints[index + 1];
//			word = (word << 32) | ints[index];
//			return word;
		} else {
			throw new RuntimeException( "Unsupported Enum size");
		}
	}
	
	@Override
	protected boolean expandInternal(LayoutConstraintMap map, IAttribute attr, int attrId) {

		/* Needs to implement to following:
					if (!bits.get(v.getEnumIndex())) {
						bits.set(v.getEnumIndex());
						return true;
					}
					return false;
		*/
		
		EnumExclusiveValue v = (EnumExclusiveValue)attr;
		int index = map.getIndexQuick(attrId);
		int ints[] = map.getInts();
		int enumIndex = v.getEnumIndex();
		
		if ( bitIsSet( ints[index], enumIndex) ){
			return false;
		}
		ints[index] |= 1 << enumIndex; 
		return true;
	}



	private boolean bitIsSet(int i, int index) {
		return (i & (1 << index)) != 0;
	}

}
