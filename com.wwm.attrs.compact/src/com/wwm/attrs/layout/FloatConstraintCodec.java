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

import com.wwm.attrs.simple.FloatConstraint;
import com.wwm.attrs.simple.FloatHave;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;


public class FloatConstraintCodec extends LayoutConstraintCodec {


    private static final int FLOAT_MIN_VALUE_OFFSET = 0; // length length
    private static final int FLOAT_MAX_VALUE_OFFSET = 1;

    private static final int FLOAT_CONSTRAINT_LENGTH = 2;

	private static FloatConstraintCodec instance = null;
	
	public static synchronized FloatConstraintCodec getInstance() {
		if (instance == null) {
			instance = new FloatConstraintCodec();
		}
		return instance;
	}

	
	@Override
	public void encode(LayoutAttrMap<IAttributeConstraint> map, int attrId, Object value) {
		int index = map.getIndexForFloatsWrite(attrId, FLOAT_CONSTRAINT_LENGTH);

    	FloatConstraint constraint = (FloatConstraint)value;
		
		if (constraint.isIncludesNotSpecified()){
			((LayoutConstraintMap) map).setIncludesNotSpecified(attrId);
		}

		map.getFloats()[index + FLOAT_MIN_VALUE_OFFSET] = constraint.getMin();
		map.getFloats()[index + FLOAT_MAX_VALUE_OFFSET] = constraint.getMax();
	}
	
	
	@Override
	public IAttributeConstraint getDecoded(LayoutAttrMap<IAttributeConstraint> map, int attrId) {

		int index = map.getIndexQuick(attrId);
		boolean inclNS = ((LayoutConstraintMap) map).getIncludesNotSpecified(attrId);

		float min = map.getFloats()[index + FLOAT_MIN_VALUE_OFFSET];
		float max = map.getFloats()[index + FLOAT_MAX_VALUE_OFFSET];
	
		return new FloatConstraint( attrId, min, max, inclNS );
	}
	
	@Override
	protected boolean expandInternal(LayoutConstraintMap map, IAttribute attr, int attrId) {
		float v = ((FloatHave) attr).getValue();
		int index = map.getIndexQuick(attrId);

		float min = map.getFloats()[index + FLOAT_MIN_VALUE_OFFSET];
		if (v < min) {
			map.getFloats()[index + FLOAT_MIN_VALUE_OFFSET] = v;
			return true;
		}
		float max = map.getFloats()[index + FLOAT_MAX_VALUE_OFFSET];
		if (v > max) {
			map.getFloats()[index + FLOAT_MAX_VALUE_OFFSET] = v;
			return true;
		}
		return false;
	}
	
}
