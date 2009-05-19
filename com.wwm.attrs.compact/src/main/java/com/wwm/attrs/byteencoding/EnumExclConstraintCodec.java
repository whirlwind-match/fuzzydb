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

import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumExclusiveConstraint;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.util.ByteArray;

public class EnumExclConstraintCodec extends CompactConstraintCodec {


	private static final int LENGTH_OFFSET = PAYLOAD_OFFSET;
	private static final int ENUM_VALUES_OFFSET = PAYLOAD_OFFSET + 1;
	private static final int ENUM_VALUES_LENGTH = (EnumDefinition.MAX_ENTRIES + 7) / 8;

	private static final byte EEC_SIZE = ENUM_VALUES_OFFSET + ENUM_VALUES_LENGTH;


	static private EnumExclConstraintCodec instance;

	public static synchronized CompactAttrCodec getInstance() {
		if (instance == null) {
			instance = new EnumExclConstraintCodec();
		}
		return instance;
	}

	@Override
	public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {
		EnumExclusiveConstraint enumValue = (EnumExclusiveConstraint)value;
		
		// Find if there's one to overwrite
		int i = findAttrInBuf(bytes, attrId);
		
		// If not, allocate some space on the end
		if ( i < 0 ) {
			i = bytes.getIndexForWrite(EEC_SIZE);
		}

		// Write out the data that is needed.  
		// Note: enumDefId is sent to the server to allow the scorer to assert() that it is scoring against the right defs
		bytes.putByte(i + LENGTH_OFFSET, EEC_SIZE);
		setConstraintAttrId(bytes, i, attrId, enumValue.isIncludesNotSpecified());
		
		long word = enumValue.getBitSet().getWord();
		if (ENUM_VALUES_LENGTH == 4){
			int bits = (int) (word & 0xFFFFFFFFL); // FIXME: Does this (cast) work properly on if bit 31 is set! (sign bit)
			bytes.putInt(i + ENUM_VALUES_OFFSET, bits );
		} else if (ENUM_VALUES_LENGTH == 8){
			long bitfield = word;
			bytes.putLong(i + ENUM_VALUES_OFFSET, bitfield );
		} else {
			throw new Error( "Unsupported Enum size");
		}
		
	}

	@Override
	public IAttribute getDecoded(ByteArray bytes, int index) {
		int headerWord = getHeaderWord( bytes, index);
		
		int attrId = getAttrId( headerWord );
        boolean inclNS = getIncludesNotSpecified( headerWord );
		long bits = getValue(bytes, index);
		
		return new EnumExclusiveConstraint( attrId, (short)-1, bits, inclNS );
	}

	public static long getValue(ByteArray bytes, int index) {
		if (ENUM_VALUES_LENGTH == 4){
			return bytes.getInt(index + ENUM_VALUES_OFFSET );
		} else if (ENUM_VALUES_LENGTH == 8){
			return bytes.getLong(index + ENUM_VALUES_OFFSET );
		} else {
			throw new Error( "Unsupported Enum size");
		}
	}

}
