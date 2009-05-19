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


import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;


public class CompactConstraintMap extends CompactAttrMap<IAttributeConstraint> implements
		IConstraintMap {

	private static final long serialVersionUID = 1L;


	
//	TODO IMPLEMENT CONSISTENT() and double-check that expand should only be one or other after a while (should after split) 
	
	
    /**
     * Fetch, expand and replace the attribute
     * TODO: Change to modify the attribute in place
     */
	public boolean expand(IAttribute attr, int attrId) {

		// Find attr from within our ByteArray
		int index = CompactAttrCodec.findAttrInBuf( getByteArray(), attrId );
		if (index >= 0){
	        CompactConstraintCodec codec = CodecManager.getConstraintCodec( getByteArray(), index );
	        try {
				return codec.expand(getByteArray(), index, attr);
			} catch (UnsupportedOperationException e) {
				// If this codec doesn't support it, then fall back to slow method
				// NOTE: DO need clone as we may be getting a cached object
		        IAttributeConstraint c = ((IAttributeConstraint) codec.getDecoded( getByteArray(), index )).clone(); 
		        boolean expanded = c.expand(attr); // expand
		        putAttr(c); // re-encode
		        return expanded;

			}
		}
		throw new Error("Wasn't expecting to get here");
	}

    @Override
    public CompactConstraintMap clone() {
        return (CompactConstraintMap) super.clone();
    }

}
