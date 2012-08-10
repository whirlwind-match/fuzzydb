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


import org.fuzzydb.attrs.internal.Attribute;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;


public class LayoutConstraintCodec extends LayoutAttrCodec<IAttributeConstraint> {

	final public boolean expand(LayoutConstraintMap map, IAttribute attr, int attrId) {
		// if attr == null, then check and set contains nulls, and return true if we expanded
		if (attr == null){
			return checkAndSetInclNotSpecified( map, attrId );
		}

		// FIXME: Review comments below and ensure use of hasAttribute is consistent elsewhere
		// (note: inclNotSpecified being set doesn't mean that hasAttribute is true)
		// If constraint isn't present, then create one and encode it 
		if (!map.hasAttribute(attrId)){
			IAttributeConstraint c = ((Attribute) attr).createAnnotation();
			encode(map, attrId, c);
			return true;
		}
		
		boolean expanded = expandInternal(map, attr, attrId);
		return expanded;
	}


	/**
	 * Expand map using attribute attr. This internal variant, does not have to deal with null, and
	 * createAnnotation() which is done for the first constraint
	 * @param map
	 * @param attr
	 * @param attrId
	 * @return
	 */
	protected boolean expandInternal(LayoutConstraintMap map, IAttribute attr, int attrId) {
		// default impl is read-modify-write
        IAttributeConstraint c = getDecoded( map, attrId ).clone(); 
        boolean expanded = c.expand(attr); // expand
        encode(map, attrId, c); // re-encode
		return expanded;
	}

	
	/**
	 * Check the inclNotSpecified bit, set it, and return true if we changed it
	 * @param bytes
	 * @param attrId
	 * @return
	 */
	protected boolean checkAndSetInclNotSpecified(LayoutConstraintMap map, int attrId) {
		boolean prev = map.getIncludesNotSpecified(attrId);
		if (prev) {
			return false; // it was already set
		} else {
			map.setIncludesNotSpecified(attrId);
			return true; // we did change it
		}
	}
}
