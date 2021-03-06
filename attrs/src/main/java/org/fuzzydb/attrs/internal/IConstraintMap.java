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


import java.io.Serializable;

import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;



public interface IConstraintMap extends IAttributeMap<IAttributeConstraint>, Serializable {

	/**
	 * Create constraint, or expand existing constraint
	 * @param attr - null allowed
	 */
	public boolean expand(IAttribute attr, int attrId);
    
    public abstract IConstraintMap clone();

}
