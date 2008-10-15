/********************************import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumMultipleConstraint;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
e; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.attrs.layout;

import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumMultipleConstraint;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;


public class EnumMultiConstraintCodec extends LayoutConstraintCodec {

	private static final int ENUM_VALUES_LENGTH = (EnumDefinition.MAX_ENTRIES + 7) / 32;

	private static final int LOWEST_COUNT_OFFSET = ENUM_VALUES_LENGTH;

	public static final int LENGTH = LOWEST_COUNT_OFFSET + 1;


	private static EnumMultiConstraintCodec instance = null;
	
	public static synchronized EnumMultiConstraintCodec getInstance() {
		if (instance == null) {
			instance = new EnumMultiConstraintCodec();
		}
		return instance;
	}

	

	@Override
	public void encode(LayoutAttrMap<IAttributeConstraint> map, int attrId, Object value) {
		int index = map.getIndexForIntsWrite(attrId, LENGTH);

		EnumMultipleConstraint constraint = (EnumMultipleConstraint)value;
		
		if (constraint.isIncludesNotSpecified()){
			((LayoutConstraintMap) map).setIncludesNotSpecified(attrId);
		}

		long word = constraint.getBitSet().getWord();
		if (ENUM_VALUES_LENGTH == 1){
			int bits = (int) word; // FIXME: Does this (cast) work properly on if bit 31 is set! (sign bit)a
			map.getInts()[index] = bits;
		} else if (ENUM_VALUES_LENGTH == 2){
			map.getInts()[index] = (int) (word & 0xffffffffL); // FIXME: I don't trust the sign bits here needs testing
			map.getInts()[index + 1] = (int) (word >>> 32);
			
		} else {
			throw new Error( "Unsupported Enum size");
		}
		
		map.getInts()[index + LOWEST_COUNT_OFFSET] = constraint.getLowestCount();
	}
	
	
	@Override
	public IAttributeConstraint getDecoded(LayoutAttrMap<IAttributeConstraint> map, int attrId) {

		int index = map.getIndexQuick(attrId);
		boolean inclNS = ((LayoutConstraintMap) map).getIncludesNotSpecified(attrId);

		long bits = getBits(map.getInts(), index);
		
		int lowestCount = map.getInts()[index + LOWEST_COUNT_OFFSET];
		return new EnumMultipleConstraint( attrId, (short)-1, bits, inclNS, lowestCount );
	}
	
	public static long getBits(int[] ints, int index) {
		if (ENUM_VALUES_LENGTH == 1){
			return ints[index];
		} else if (ENUM_VALUES_LENGTH == 2){
			long word = ints[index + 1];
			word = (word << 32) | ints[index];
			return word;
		} else {
			throw new Error( "Unsupported Enum size");
		}
	}

}
