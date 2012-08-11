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

import java.util.HashMap;
import java.util.Map;

import org.fuzzydb.attrs.bool.BooleanValue;
import org.fuzzydb.attrs.internal.AttrDefinitionMgr;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.dto.attributes.BooleanAttribute;
import org.fuzzydb.util.ByteArray;



public class BooleanCodec extends CompactAttrCodec {

	static final byte BOOLEAN_SIZE = ATTR_ID_SIZE + 1; 	// 1 for bool (not full header, as doesn't include a length byte)

	private static final int BOOLEAN_FLAG_OFFSET = PAYLOAD_OFFSET;

	private final Map<Integer, BooleanValue> map = new HashMap<Integer, BooleanValue>();
	
	static BooleanCodec instance = null;
	
	public static synchronized BooleanCodec getInstance() {
		if (instance == null) {
			instance = new BooleanCodec();
		}
		return instance;
	}


	@Override
	public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {

		// Find if there's one to overwrite
		int i = findAttrInBuf(bytes, attrId);
		
		// If not, allocate some space on the end
		if ( i < 0 ) {
			i = bytes.getIndexForWrite(BOOLEAN_SIZE);
		}
		setAttrId(bytes, i, attrId); // override this so that true/false/includesnotspecified gets included in attrId encoding
		if (value instanceof BooleanValue){
			bytes.putBoolean(i + BOOLEAN_FLAG_OFFSET, ((BooleanValue) value).isTrue());
		} else if (value instanceof BooleanAttribute) {
			bytes.putBoolean(i + BOOLEAN_FLAG_OFFSET, ((BooleanAttribute) value).getValue());
		} else {
			bytes.putBoolean(i + BOOLEAN_FLAG_OFFSET, (Boolean)value);
		}
	}

	
	@Override
	public IAttribute getDecoded(ByteArray bytes, int index) {
		// Lookup in cache. Our cache can be a int -> boolean map
		// NOTE: we know that we have 24 bit for attrId, so can use top bits for the rest.

		int attrId = getAttrId( getHeaderWord( bytes, index) );
		
		int key = bytes.getByte(index + BOOLEAN_FLAG_OFFSET) << 24 | AttrDefinitionMgr.getAttrIndex( attrId );
		
		BooleanValue value = map.get( key );
		if (value == null) {
			synchronized (map) {
				final boolean isTrue = bytes.getByte(index + BOOLEAN_FLAG_OFFSET) != 0;
				value = new BooleanValue( attrId, isTrue );
				map.put(key, value);
			}
		}
		return value;
	}


	/**
	 * 
	 * @param bytes
	 * @param index - index of start of attribute
	 * @return
	 */
	public static byte getValue(ByteArray bytes, int index) {
		return bytes.getByte(index + BOOLEAN_FLAG_OFFSET);
	}


}
