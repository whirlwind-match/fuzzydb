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

import org.fuzzydb.attrs.simple.FloatConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttribute;

import com.wwm.util.ByteArray;


public class FloatConstraintCodec extends CompactConstraintCodec {

	private static final int LENGTH_OFFSET = PAYLOAD_OFFSET;
    private static final byte LENGTH = ATTR_ID_SIZE + 1 + 8; // length byte + 2 * float

    private static final int FLOAT_MIN_VALUE_OFFSET = LENGTH_OFFSET + 1; // length length
    private static final int FLOAT_MAX_VALUE_OFFSET = FLOAT_MIN_VALUE_OFFSET + 4;
    
    static FloatConstraintCodec instance = null;
        
    public static synchronized CompactAttrCodec getInstance() {
        if (instance == null) {
            instance = new FloatConstraintCodec();
        }
        return instance;
    }
    
    @Override
    public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {

    	FloatConstraint floatConstraint = (FloatConstraint)value;
        
    	// Find if there's one to overwrite
        int i = findAttrInBuf(bytes, attrId);
        
        // If not, allocate some space on the end
        if ( i < 0 ) {
            i = bytes.getIndexForWrite(LENGTH);
        }
        bytes.putByte(i + LENGTH_OFFSET, LENGTH);
        setConstraintAttrId(bytes, i, attrId, floatConstraint.isIncludesNotSpecified() );
        float minVal = floatConstraint.getMin();
        float maxVal = floatConstraint.getMax();
        bytes.putFloat(i + FLOAT_MIN_VALUE_OFFSET, minVal);
        bytes.putFloat(i + FLOAT_MAX_VALUE_OFFSET, maxVal);        
    }

    @Override
    public IAttribute getDecoded(ByteArray bytes, int index) {

        int headerWord = getHeaderWord( bytes, index);
		int attrId = getAttrId( headerWord );
        
        float minValue = bytes.getFloat(index + FLOAT_MIN_VALUE_OFFSET);
        float maxValue = bytes.getFloat(index + FLOAT_MAX_VALUE_OFFSET);
        boolean inclNS = getIncludesNotSpecified( headerWord );
        FloatConstraint value = new FloatConstraint(attrId, minValue, maxValue, inclNS);

        return value;
    }
    
    static public float getMin( ByteArray bytes, int index) {
        float minValue = bytes.getFloat(index + FLOAT_MIN_VALUE_OFFSET);
        return minValue;
    }

    static public float getMax( ByteArray bytes, int index) {
        float maxValue = bytes.getFloat(index + FLOAT_MAX_VALUE_OFFSET);
        return maxValue;
    }
}
