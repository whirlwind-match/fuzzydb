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


import org.fuzzydb.attrs.simple.FloatValue;
import org.fuzzydb.core.whirlwind.internal.IAttribute;

import com.wwm.model.attributes.FloatAttribute;
import com.wwm.util.ByteArray;


public class FloatCodec extends CompactAttrCodec {

	static final byte FLOAT_SIZE = ATTR_ID_SIZE + 4; // (note not full header, no length byte) 4 for float

	private static final int FLOAT_VALUE_OFFSET = PAYLOAD_OFFSET;
	
	
	static FloatCodec instance = null;
	
	public static synchronized CompactAttrCodec getInstance() {
		if (instance == null) {
			instance = new FloatCodec();
		}
		return instance;
	}


	@Override
	public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {

		// Find if there's one to overwrite
		int i = findAttrInBuf(bytes, attrId);
		
		// If not, allocate some space on the end
		if ( i < 0 ) {
			i = bytes.getIndexForWrite(FLOAT_SIZE);
		}
		setAttrId(bytes, i, attrId);
		if (value instanceof FloatAttribute) {
			bytes.putFloat(i + FLOAT_VALUE_OFFSET, ((FloatAttribute)value).getValue());
		} else if (value instanceof FloatValue) {
			bytes.putFloat(i + FLOAT_VALUE_OFFSET, ((FloatValue) value).getValue());
		} else { // assume Float
			bytes.putFloat(i + FLOAT_VALUE_OFFSET, ((Float)value) );
		}
	}

	
	@Override
	public IAttribute getDecoded(ByteArray bytes, int index) {

		int attrId = getAttrId( getHeaderWord( bytes, index) );
		
		float floatValue = bytes.getFloat(index + FLOAT_VALUE_OFFSET);
		IAttribute attr = new FloatValue( attrId, floatValue );
		
		return attr;
	}


	/**
	 * Allow an known attribute type at a given location in a ByteArray 
	 * to be decoded quickly and easily.
	 * (e.g. used in CompactSimilarFloatValueScorer)
	 */
	public static float getValue(ByteArray bytes, int index) {
		return bytes.getFloat(index + FLOAT_VALUE_OFFSET);
	}


}
