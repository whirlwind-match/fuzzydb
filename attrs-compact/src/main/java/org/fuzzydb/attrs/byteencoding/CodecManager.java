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

import org.fuzzydb.attrs.internal.AttrDefinitionMgr;
import org.fuzzydb.attrs.internal.AttrDefinitionMgr.AttrType;

import com.wwm.util.ByteArray;

public class CodecManager {

    public static CompactAttrCodec getCodec(ByteArray bytes, int index) {
        // If found, decode entry using the caching decoder
    	int headerWord = CompactAttrCodec.getHeaderWord( bytes, index);
        int attrId = CompactAttrCodec.getAttrId(headerWord);
        CompactAttrCodec codec = 
            CompactAttrCodec.isConstraint(headerWord)
                    ? CodecManager.getConstraintCodec( attrId )
                    : CodecManager.getCodec( attrId );
        return codec;
    }

    
    public static CompactConstraintCodec getConstraintCodec(ByteArray bytes, int index) {
		return (CompactConstraintCodec)getCodec(bytes, index);
	}

    
    public static CompactAttrCodec getCodec(int attrId) {
		
		AttrType type = AttrDefinitionMgr.getAttrType(attrId);
		
		switch (type) {
		case booleanValue:
			return BooleanCodec.getInstance();
		case floatValue:
			return FloatCodec.getInstance();
		case floatRangePrefValue:
			return FloatRangePreferenceCodec.getInstance();
		case enumExclusiveValue:
			return EnumCodec.getInstance();
		case enumMultiValue:
			return EnumMultiValueCodec.getInstance();
		case vectorValue:
			return EcefVectorCodec.getInstance();
		default:
			return null;
		}
	}

    
	public static CompactAttrCodec getConstraintCodec(int attrId) {
		AttrType type = AttrDefinitionMgr.getAttrType(attrId);
		
		switch (type) {
		case booleanValue:
			return BooleanConstraintCodec.getInstance();
		case floatValue:
			return FloatConstraintCodec.getInstance();
		case floatRangePrefValue:
			return DimensionRangeCodec.getInstance();
		case enumExclusiveValue:
			return EnumExclConstraintCodec.getInstance();
		case enumMultiValue:
			return EnumMultiConstraintCodec.getInstance();
		case vectorValue:
			return DimensionRangeCodec.getInstance();
		default:
			return null;
		}
	}


}
