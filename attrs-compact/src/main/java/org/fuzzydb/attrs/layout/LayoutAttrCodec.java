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
package org.fuzzydb.attrs.layout;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;



public class LayoutAttrCodec<T extends IAttribute> {

	
	public void encode(LayoutAttrMap<T> map, int attrId, Object value) {
		
		throw new UnsupportedOperationException();
	}

	
	/**
	 * 
	 * @param map
	 * @param attrId
	 * @return attribute - NOT NULL (must already have established that the attribute exists in this map, using map.hasAttribute()
	 */
	public T getDecoded(LayoutAttrMap<T> map, int attrId) {
		// This needs implementing in each codec
		// Algo:
		// - 
		
		throw new UnsupportedOperationException();
	}

	/**
	 * default inefficient version.  Need to override into codecs to look
	 * directly into map
	 */
	public final boolean consistent(LayoutAttrMap<T> map, int attrId, IAttributeConstraint constraint) {
		if (!map.hasAttribute(attrId)) {
			if (constraint == null){
				return true;	// no attribute + no constraint, this is the right branch
			}
			// FIXME: Check this in other impls - they just return false
			// no attribute but there is a constraint, wrong branch - need the one with no contraint
			return constraint.isIncludesNotSpecified(); 
		}
		if (constraint == null) return false;	// there is an attribute matching split id, must select a constrained branch
				
		return consistentForInternal(map, attrId, constraint);
	}


	/**
	 * This should be overridden for each codec
	 */
	protected boolean consistentForInternal(LayoutAttrMap<T> map, int attrId, IAttributeConstraint constraint) {
		IAttribute att = map.findAttr(attrId);
		return constraint.consistent(att);
	}

}
