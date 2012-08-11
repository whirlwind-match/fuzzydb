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

import org.fuzzydb.attrs.enums.EnumExclusiveValue;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.util.ByteArray;


public class EnumCodec extends CompactAttrCodec {

	static final byte EXCLUSIVE_ENUM_SIZE = ATTR_ID_SIZE + 2; // (note: not full header, no length byte) 1 for enumDefId, 1 for enum index
	private static final int ENUM_DEF_OFFSET = PAYLOAD_OFFSET;
	private static final int ENUM_DEF_LENGTH = 1; // byte
	private static final int ENUM_VALUE_OFFSET = ENUM_DEF_OFFSET + ENUM_DEF_LENGTH;

	static private EnumCodec instance;

	public static synchronized CompactAttrCodec getInstance() {
		if (instance == null) {
			instance = new EnumCodec();
		}
		return instance;
	}

	@Override
	public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {
		EnumExclusiveValue enumValue = (EnumExclusiveValue)value;
		
		// Find if there's one to overwrite
		int i = findAttrInBuf(bytes, attrId);
		
		// If not, allocate some space on the end
		if ( i < 0 ) {
			i = bytes.getIndexForWrite(EXCLUSIVE_ENUM_SIZE);
		}

		// Write out the data that is needed.  
		// Note: enumDefId is sent to the server to allow the scorer to assert() that it is scoring against the right defs
		setAttrId(bytes, i, attrId);
		bytes.putByte(i + ENUM_DEF_OFFSET, (byte) enumValue.getEnumDefId() );
		bytes.putByte(i + ENUM_VALUE_OFFSET, (byte) enumValue.getEnumIndex() );
	}

	@Override
	public IAttribute getDecoded(ByteArray bytes, int index) {
		int attrId = getAttrId( getHeaderWord( bytes, index) );
		byte val = bytes.getByte(index + ENUM_VALUE_OFFSET );
		byte defId = bytes.getByte(index + ENUM_DEF_OFFSET );

		return new EnumExclusiveValue( attrId, defId, val );
	}

	public static byte getValue(ByteArray bytes, int index) {
		return bytes.getByte(index + ENUM_VALUE_OFFSET);
	}
	
}
