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
package org.fuzzydb.core.whirlwind.internal;

import java.io.Serializable;

/**
 * Internal Attribute representation, as required by the server.
 */
public interface IAttribute extends Serializable, Cloneable, org.fuzzydb.core.marker.IAttribute {

    /**
     * Return the Attribute ID
     */
    int getAttrId();
    
    void setAttrId(int attrId);
    
	/**
	 * TODO: FInd out why this isn't using Comparable interface...
	 * Called to compare attribute values for possible substitution.
	 * Always called with the same type (attribute id)
	 */
	public int compareAttribute(IAttribute rhs);

    
    public IAttribute clone() throws CloneNotSupportedException;
    
    /**
     * Ensure that the attribute is a DB2 API attribute, allowing DB1 API attrs to
     * be migrated.
     */
	Object asSimpleAttribute();
}
