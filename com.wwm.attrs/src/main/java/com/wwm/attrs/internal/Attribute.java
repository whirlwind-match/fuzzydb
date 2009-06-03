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
package com.wwm.attrs.internal;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;



/**
 * @author ac
 *
 */
public abstract class Attribute<T extends Attribute<T>> extends BaseAttribute implements IAttribute<T>, Cloneable {

	private static final long serialVersionUID = 371895097117898864L;

    /**
     * Constructors protected on abstract class
	 * @param attrId
	 */
	protected Attribute(int attrId) {
		super(attrId);
	}

	protected Attribute(Attribute rhs) {
		super(rhs);
	}
	
	@Override
    public abstract T clone() throws CloneNotSupportedException;
	

	/**Generates a minimalist constraint that only just encapulates this attribute
	 * @return non-null
	 */
	public abstract IAttributeConstraint createAnnotation();
	
	public Object getAsDb2Attribute() {
		return this; // Default: Needs overriding for DB1 attrs.
	}
}
