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


import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeConstraint;

import com.wwm.util.ByteArray;


public abstract class CompactConstraintCodec extends CompactAttrCodec {

    static public void setConstraintAttrId(ByteArray bytes, int index, int attrId, boolean includesNotSpecified) {
        assert(attrId < 65536);
        int inclNotSpecified = includesNotSpecified ? INCL_NOT_SPECD_YES : INCL_NOT_SPECD_NO;
        bytes.putShort( index + ATTR_ID_OFFSET, (short)(attrId | inclNotSpecified | FN_CONSTRAINT) );
    }

	/**
	 * Perform an in-place expand of the constraint at position <code>index</code>
	 * of the supplied ByteArray.  The constraint is expanded to include the
	 * attribute <code>attr</code> and we return true if this action
	 * actually expanded the constraint.
	 */
	public boolean expand(ByteArray bytes, int index, IAttribute attr) {

		if (attr == null){
			return checkAndSetInclNotSpecified(bytes, index);
		}
		
		// default impl is read-modify-write
        IAttributeConstraint c = ((IAttributeConstraint) getDecoded( bytes, index )).clone(); 
        boolean expanded = c.expand(attr); // expand
        encodeToByteArray(bytes, attr.getAttrId(), c); // re-encode
        return expanded;
	}

	/**
	 * Check the inclNotSpecified bit, set it, and return true if we changed it
	 * @param bytes
	 * @param index
	 * @return
	 */
	protected boolean checkAndSetInclNotSpecified(ByteArray bytes, int index) {
		int headerWord = getHeaderWord( bytes, index);
		boolean prev = getIncludesNotSpecified(headerWord);
		if (prev) {
			return false; // it was already set
		} else {
			headerWord |= INCL_NOT_SPECD_YES;
			bytes.putShort( index + ATTR_ID_OFFSET, (short)headerWord );
			return true; // we did change it
		}
	}

	static public boolean getIncludesNotSpecified( int headerWord ){
		return (headerWord & INCL_NOT_SPECD_MASK) == INCL_NOT_SPECD_YES;
	}

	static public boolean getIncludesNotSpecified( ByteArray bytes, int index) {
		int headerWord = getHeaderWord( bytes, index);
		return getIncludesNotSpecified(headerWord);
	}

}
