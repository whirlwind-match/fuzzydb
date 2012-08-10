/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.attrs.internal;



import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;

public class ConstraintMap extends AttributeMap<IAttributeConstraint> implements
		IConstraintMap {

	private static final long serialVersionUID = 1L;


    /**
     * Fetch, expand and replace the attribute
     * TODO: Change to modify the attribute in place
     */
	public boolean expand(IAttribute attr, int attrId) {
        // The inefficient way, but will do for now
        IAttributeConstraint c = findAttr(attrId); // decode
        if (c == null) {
        	c = ((Attribute)attr).createAnnotation();
            putAttr(c); // re-encode
        	return true;
        } else {
	        boolean expanded = c.expand(attr); // expand
	        putAttr(c); // re-encode
	        return expanded;
        }
	}

    @Override
    public IConstraintMap clone() {
        return (IConstraintMap) super.clone();
    }

}
