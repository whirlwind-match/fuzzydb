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

import java.util.HashMap;
import java.util.Map;

import com.wwm.attrs.bool.BooleanConstraint;
import com.wwm.attrs.bool.BooleanValue;
import com.wwm.attrs.bool.BooleanConstraint.State;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.util.ByteArray;



public class BooleanConstraintCodec extends CompactConstraintCodec {

	static final byte BOOLEAN_SIZE = ATTR_ID_SIZE + 1; 	//overhead + 1 for bool

	private static final int BOOLEAN_STATE_OFFSET = PAYLOAD_OFFSET;

	
	private final Map<Integer, BooleanConstraint> map = new HashMap<Integer, BooleanConstraint>();
	
	static BooleanConstraintCodec instance = null;
	
	public static synchronized BooleanConstraintCodec getInstance() {
		if (instance == null) {
			instance = new BooleanConstraintCodec();
		}
		return instance;
	}


	@Override
	public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {

		BooleanConstraint booleanConstraint = (BooleanConstraint)value;

		// Find if there's one to overwrite
		int i = findAttrInBuf(bytes, attrId);
		
		// If not, allocate some space on the end
		if ( i < 0 ) {
			i = bytes.getIndexForWrite(BOOLEAN_SIZE);
		}
		setConstraintAttrId(bytes, i, attrId, booleanConstraint.isIncludesNotSpecified() );
        int val = booleanConstraint.getState().ordinal();
		bytes.putByte(i + BOOLEAN_STATE_OFFSET, (byte)val );
	}

	
	@Override
	public IAttribute getDecoded(ByteArray bytes, int index) {
		// Lookup in cache. Our cache can be a int -> state map
		// NOTE: we know that we have 24 bit for attrId, so can use top bits for the rest.

		int headerWord = getHeaderWord( bytes, index);
		int attrId = getAttrId( headerWord );
		
		byte encodedByte = bytes.getByte(index + BOOLEAN_STATE_OFFSET);
        int key = encodedByte << 24 | headerWord;
		
//		*** FIXME: This should be something that is an interface to the byte array, so that when it constraint is modified, the byte
//		array gets modified ***
		BooleanConstraint value = map.get( key );
		if (value == null) {
			synchronized (map) {
				final State state = State.values()[encodedByte];
                final boolean hasNulls = getIncludesNotSpecified(headerWord);
				value = new BooleanConstraint( attrId, state, hasNulls );
				map.put(key, value);
			}
		}
		return value;
	}
	
	// FIXME: Test this by calling constraintMap.expand(attrId, null) when no constraint exists.. (this case may not yet exist.. should break)
	@Override
	public boolean expand(ByteArray bytes, int index, IAttribute attr) {
		
		// if attr == null, then check and set contains nulls, and return true if we expanded
		if (attr == null){
			return checkAndSetInclNotSpecified( bytes, index );
		}
		
		// if attr != null, then check if this item is set, and modify if necessary, and return true if we expanded
		byte constraintStateByte = bytes.getByte(index + BOOLEAN_STATE_OFFSET);
		if (constraintStateByte == State.hasBoth.ordinal()){
			return false; // never expand it
		}
		
		BooleanValue boolVal = (BooleanValue)attr;
		// If expanding with same thing that already set, then return false
		if (boolVal.isTrue() && constraintStateByte == State.hasTrue.ordinal()){
			return false;
		}
		if (!boolVal.isTrue() && constraintStateByte == State.hasFalse.ordinal()){
			return false;
		}
		
		// One of them must have been set
		assert ( constraintStateByte == State.hasTrue.ordinal() || constraintStateByte == State.hasFalse.ordinal() );
		bytes.putByte( index + BOOLEAN_STATE_OFFSET, (byte)State.hasBoth.ordinal() );
		return true;
	}
	

	/**
	 * For scorers to compare byte
	 * @param bytes
	 * @param index - index of start of attribute
	 * @return
	 */
	public static byte getValue(ByteArray bytes, int index) {
		return bytes.getByte(index + BOOLEAN_STATE_OFFSET);
	}
}
