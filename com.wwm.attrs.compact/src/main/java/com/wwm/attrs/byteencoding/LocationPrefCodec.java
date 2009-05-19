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

import com.wwm.attrs.location.EcefVector;
import com.wwm.attrs.location.RangePreference;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.dimensions.IDimensions;
import com.wwm.util.ByteArray;



public class LocationPrefCodec extends CompactAttrCodec {

	static final byte LOCATION_PREF_SIZE = ATTR_ID_SIZE + 17; // (note not full header, no length byte) 12 for vector, 1 for prefClose, 4 for range

	private static final int ECEFVECTOR_VALUE_OFFSET = PAYLOAD_OFFSET;
	private static final int RANGE_OFFSET = ECEFVECTOR_VALUE_OFFSET + 12;
	private static final int PREFER_CLOSE_OFFSET = RANGE_OFFSET + 4;  
	
	static LocationPrefCodec instance = null;
	
	public static synchronized CompactAttrCodec getInstance() {
		if (instance == null) {
			instance = new LocationPrefCodec();
		}
		return instance;
	}


	@Override
	public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {

		RangePreference rangePref = (RangePreference)value;

		// Find if there's one to overwrite
		int i = findAttrInBuf(bytes, attrId);
		
		// If not, allocate some space on the end
		if ( i < 0 ) {
			i = bytes.getIndexForWrite(LOCATION_PREF_SIZE);
		}
		setAttrId(bytes, i, attrId);
		
		// Write in whatever form has been stored as IDimensions (in fact... 
		writeIDimensions(bytes, i + ECEFVECTOR_VALUE_OFFSET, rangePref);		
		bytes.putFloat(i + RANGE_OFFSET, rangePref.getRange() );
		bytes.putBoolean(i + PREFER_CLOSE_OFFSET, rangePref.isPreferClose() );  // TODO: Can save a byte here by encoding preferClose as -ve range
	}


	// TODO Move to IDimensionCodec
	protected void writeIDimensions(ByteArray bytes, int index,
			IDimensions iDim) {
		for (int d = 0; d < 3; d++) {
			bytes.putFloat(index + 4 * d, iDim.getDimension(d));
		}
	}

	
	@Override
	public IAttribute getDecoded(ByteArray bytes, int index) {

		int attrId = getAttrId( getHeaderWord( bytes, index) );
		
		EcefVector vector = new EcefVector( attrId, 0f, 0f, 0f );
		populateIDimensions(bytes, index + ECEFVECTOR_VALUE_OFFSET, vector);		

		float range = bytes.getFloat(index + RANGE_OFFSET);
		boolean preferClose = bytes.getBoolean(index);
		IAttribute attr = new RangePreference( attrId, vector, range, preferClose );
		
		return attr;
	}

	/**
	 * Populates an object implementing the IDimensions interface with
	 * the appropriate number of floats read from supplied ByteArray
	 * @param bytes
	 * @param index
	 * @param idim
	 */
	// TODO Move to IDimensionCodec and replace calls to original with + IDIMOFFSET..
	protected void populateIDimensions(ByteArray bytes, int index, IDimensions idim) {
		for (int i = 0; i < idim.getNumDimensions(); i++) {
			float floatValue = bytes.getFloat(index + i * 4);
			idim.setDimension(i, floatValue);
		}
	}

	
	/**
	 * Allow an known attribute type at a given location in a ByteArray 
	 * to be decoded quickly and easily.
	 * (e.g. used in CompactSimilarFloatValueScorer)
	 */
//	public static float getValue(ByteArray bytes, int index) {
//		return bytes.getFloat(index + FLOAT_VALUE_OFFSET);
//	}


}
