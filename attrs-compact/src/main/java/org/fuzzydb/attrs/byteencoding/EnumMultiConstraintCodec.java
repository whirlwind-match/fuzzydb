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
package org.fuzzydb.attrs.byteencoding;

import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.attrs.enums.EnumMultipleConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttribute;

import com.wwm.util.ByteArray;

public class EnumMultiConstraintCodec extends CompactConstraintCodec {

	private static final int LENGTH_OFFSET = PAYLOAD_OFFSET;
	private static final int ENUM_VALUES_OFFSET = PAYLOAD_OFFSET + 1;
	private static final int ENUM_VALUES_LENGTH = (EnumDefinition.MAX_ENTRIES + 7) / 8;
	private static final int LOWEST_COUNT_OFFSET = ENUM_VALUES_OFFSET + ENUM_VALUES_LENGTH; 

	private static final byte EMC_SIZE = LOWEST_COUNT_OFFSET + 1;


	static private EnumMultiConstraintCodec instance;

	public static synchronized CompactAttrCodec getInstance() {
		if (instance == null) {
			instance = new EnumMultiConstraintCodec();
		}
		return instance;
	}

	@Override
	public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {
		EnumMultipleConstraint enumValue = (EnumMultipleConstraint)value;
		
		// Find if there's one to overwrite
		int i = findAttrInBuf(bytes, attrId);
		
		// If not, allocate some space on the end
		if ( i < 0 ) {
			i = bytes.getIndexForWrite(EMC_SIZE);
		}

		// Write out the data that is needed.  
		// Note: enumDefId is sent to the server to allow the scorer to assert() that it is scoring against the right defs
		bytes.putByte(i + LENGTH_OFFSET, EMC_SIZE);
		setConstraintAttrId(bytes, i, attrId, enumValue.isIncludesNotSpecified());
		
		long word = enumValue.getBitSet().getWord();
		if (ENUM_VALUES_LENGTH == 4){
			int bits = (int) word; // FIXME: Does this work properly on if bit 31 is set! (sign bit)a
			bytes.putInt(i + ENUM_VALUES_OFFSET, bits );
		} else if (ENUM_VALUES_LENGTH == 8){
			bytes.putLong(i + ENUM_VALUES_OFFSET, word );
		} else {
			throw new RuntimeException( "Unsupported Enum size");
		}
	
		bytes.putByte(i + LOWEST_COUNT_OFFSET, (byte)enumValue.getLowestCount() );
		
	}

	@Override
	public IAttribute getDecoded(ByteArray bytes, int index) {
		int headerWord = getHeaderWord( bytes, index);
		
		int attrId = getAttrId( headerWord );
        boolean inclNS = getIncludesNotSpecified( headerWord );
		long bits = getValue(bytes, index);
		
		int lowestCount = bytes.getByte(index + LOWEST_COUNT_OFFSET);
		
		return new EnumMultipleConstraint( attrId, (short)-1, bits, inclNS, lowestCount );
	}

	public static long getValue(ByteArray bytes, int index) {
		if (ENUM_VALUES_LENGTH == 4){
			return bytes.getInt(index + ENUM_VALUES_OFFSET );
		} else if (ENUM_VALUES_LENGTH == 8){
			return bytes.getLong(index + ENUM_VALUES_OFFSET );
		} else {
			throw new RuntimeException( "Unsupported Enum size");
		}
	}

}
