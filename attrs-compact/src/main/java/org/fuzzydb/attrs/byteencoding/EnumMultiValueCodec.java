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
import org.fuzzydb.attrs.enums.EnumMultipleValue;
import org.fuzzydb.core.whirlwind.internal.IAttribute;

import com.wwm.util.ByteArray;

public class EnumMultiValueCodec extends CompactAttrCodec {


	private static final int LENGTH_OFFSET = PAYLOAD_OFFSET;
	private static final int ENUM_DEF_OFFSET = LENGTH_OFFSET + 1;
	private static final int ENUM_DEF_LENGTH = 1; // byte
	private static final int ENUM_VALUES_OFFSET = ENUM_DEF_OFFSET + ENUM_DEF_LENGTH;
	private static final int ENUM_VALUES_LENGTH = (EnumDefinition.MAX_ENTRIES + 7) / 8;

	private static final byte EMV_SIZE = ENUM_VALUES_OFFSET + ENUM_VALUES_LENGTH;


	static private EnumMultiValueCodec instance;

	public static synchronized CompactAttrCodec getInstance() {
		if (instance == null) {
			instance = new EnumMultiValueCodec();
		}
		return instance;
	}

	@Override
	public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {
		EnumMultipleValue enumValue = (EnumMultipleValue)value;
		
		// Find if there's one to overwrite
		int i = findAttrInBuf(bytes, attrId);
		
		// If not, allocate some space on the end
		if ( i < 0 ) {
			i = bytes.getIndexForWrite(EMV_SIZE);
		}

		// Write out the data that is needed.  
		// Note: enumDefId is sent to the server to allow the scorer to assert() that it is scoring against the right defs
		bytes.putByte(i + LENGTH_OFFSET, EMV_SIZE);
		setAttrId(bytes, i, attrId);
		bytes.putByte(i + ENUM_DEF_OFFSET, (byte) enumValue.getEnumDefId() );
		
		if (ENUM_VALUES_LENGTH == 4){
			int bits = (int) enumValue.getBitSet().getWord();
			bytes.putInt(i + ENUM_VALUES_OFFSET, bits );
		} else if (ENUM_VALUES_LENGTH == 8){
			long bitfield = enumValue.getBitSet().getWord();
			bytes.putLong(i + ENUM_VALUES_OFFSET, bitfield );
		} else {
			throw new RuntimeException( "Unsupported Enum size");
		}
		
	}

	@Override
	public IAttribute getDecoded(ByteArray bytes, int index) {
		int attrId = getAttrId( getHeaderWord( bytes, index) );
		byte defId = bytes.getByte(index + ENUM_DEF_OFFSET );

		long vals = getValues(bytes, index) & 0xffffffffL;
		
		return new EnumMultipleValue( attrId, defId, vals );
	}

	public static long getValues(ByteArray bytes, int index) {
		if (ENUM_VALUES_LENGTH == 4){
			return bytes.getInt(index + ENUM_VALUES_OFFSET );
		} else if (ENUM_VALUES_LENGTH == 8){
			return bytes.getLong(index + ENUM_VALUES_OFFSET);
		} else {
			throw new RuntimeException( "Unsupported Enum size");
		}
	}

}
