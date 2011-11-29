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
package com.wwm.attrs.layout;

import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.AttrDefinitionMgr.AttrType;
import com.wwm.db.whirlwind.internal.IAttribute;


public class LayoutCodecManager {

	public static LayoutAttrCodec<IAttribute> getCodec(int attrId) {
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

	public static LayoutConstraintCodec getConstraintCodec(int attrId) {
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
