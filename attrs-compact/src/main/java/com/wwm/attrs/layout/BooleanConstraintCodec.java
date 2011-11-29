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

import com.wwm.attrs.bool.BooleanConstraint;
import com.wwm.attrs.bool.BooleanValue;
import com.wwm.attrs.bool.BooleanConstraint.State;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;


public class BooleanConstraintCodec extends LayoutConstraintCodec {

	private static final int ENCODED_LENGTH = 1;
	
	private static final int STATE_MASK = 0x03; // bits 0-1
	
	private static BooleanConstraintCodec instance = null;
	
	public static synchronized BooleanConstraintCodec getInstance() {
		if (instance == null) {
			instance = new BooleanConstraintCodec();
		}
		return instance;
	}

	

	@Override
	public void encode(LayoutAttrMap<IAttributeConstraint> map, int attrId, Object value) {
		// FIXME: For now, we don't bother with any bit shifting, thus meaning that we encode it as an int (how wasteful!)
		int index = map.getIndexForIntsWrite(attrId, ENCODED_LENGTH);
		int[] ints = map.getInts();

		BooleanConstraint booleanConstraint = (BooleanConstraint)value;

		int val = booleanConstraint.getState().ordinal();
		assert val <= 3;
		if ( booleanConstraint.isIncludesNotSpecified() ){
			((LayoutConstraintMap)map).setIncludesNotSpecified(attrId);
		}

		ints[index] = val;
	}
	
	
	@Override
	public IAttributeConstraint getDecoded(LayoutAttrMap<IAttributeConstraint> map, int attrId) {

		int index = map.getIndexQuick(attrId);
		int[] ints = map.getInts();

		int ordinal = ints[index] & STATE_MASK;
		State state = State.values()[ordinal];
		boolean hasNulls = ((LayoutConstraintMap)map).getIncludesNotSpecified(attrId);
		return new BooleanConstraint(attrId, state, hasNulls);
	}
	
	@Override
	protected boolean expandInternal(LayoutConstraintMap map, IAttribute attr, int attrId) {
		
		int index = map.getIndexQuick(attrId);
		int[] ints = map.getInts();

		// if attr != null, then check if this item is set, and modify if necessary, and return true if we expanded
		int constraintStateVal = ints[index] & STATE_MASK;
		if (constraintStateVal == State.hasBoth.ordinal()){
			return false; // never expand it
		}
		
		BooleanValue boolVal = (BooleanValue)attr;
		// If expanding with same thing that already set, then return false
		if (boolVal.isTrue() && constraintStateVal == State.hasTrue.ordinal()){
			return false;
		}
		if (!boolVal.isTrue() && constraintStateVal == State.hasFalse.ordinal()){
			return false;
		}
		
		// One of them must have been set
		assert ( constraintStateVal == State.hasTrue.ordinal() || constraintStateVal == State.hasFalse.ordinal() );
		ints[index] = State.hasBoth.ordinal();
		return true;
	}
}
