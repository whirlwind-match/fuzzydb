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

import com.wwm.model.dimensions.IDimensions;
import com.wwm.util.ByteArray;



/**
 * Provides encoding for anything that supports the IDimension interface
 */
abstract class IDimensionCodec extends CompactAttrCodec {

	private static final int LENGTH_OFFSET = PAYLOAD_OFFSET;

	private static final byte IDIM_BASE_SIZE = LENGTH_OFFSET + 1; // + numDimensions * 4 bytes:  1 for size, 4 for attrId, n*4 for n floats

	private static final int IDIM_VALUE_OFFSET = LENGTH_OFFSET + 1;
	

	@Override
	public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {

		IDimensions idim = (IDimensions)value;
		byte dims = (byte) idim.getNumDimensions();
		byte size = (byte) (IDIM_BASE_SIZE + 4 * dims);
		
		// Find if there's one to overwrite
		int i = findAttrInBuf(bytes, attrId);
		
		// If not, allocate some space on the end
		if ( i < 0 ) {
			i = bytes.getIndexForWrite(size);
		}
		bytes.putByte(i + LENGTH_OFFSET, size);
		setAttrId(bytes, i, attrId);
		
		// Write in whatever form has been stored as IDimensions (in fact... 
		for (int d = 0; d < dims; d++) {
			bytes.putFloat(i + IDIM_VALUE_OFFSET + 4 * d, idim.getDimension(d));
		}		
	}		
	

	/**
	 * Populates an object implementing the IDimensions interface with
	 * the appropriate number of floats read from supplied ByteArray
	 * @param bytes
	 * @param index
	 * @param idim
	 */
	protected void populateIDimensions(ByteArray bytes, int index, IDimensions idim) {
		for (int i = 0; i < idim.getNumDimensions(); i++) {
			float floatValue = getDimension(bytes, index, i);
			idim.setDimension(i, floatValue);
		}
	}


	static public float getDimension(ByteArray bytes, int index, int dimension) {
		float floatValue = bytes.getFloat(index + IDIM_VALUE_OFFSET + dimension * 4);
		return floatValue;
	}

}
