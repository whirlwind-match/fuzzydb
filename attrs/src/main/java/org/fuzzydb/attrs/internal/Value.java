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

public abstract class Value extends Attribute implements Comparable<Value> {

	protected Comparable<Object> val;

	/**
	 * @param selectorClass -
	 *
	 */
	public Value(int attrId, Comparable<Object> val) {
		super(attrId);
		this.val = val;
	}

	public Value(Value clonee) {
		super(clonee);
		this.val = clonee.val;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Value v) {

		return val.compareTo(v.val);
	}
	/**
	 * @return Returns the val.
	 */
	public Comparable<Object> getValue() {
		return val;
	}

	public int compareAttribute(org.fuzzydb.core.whirlwind.internal.IAttribute rhs) {
		return val.compareTo(rhs);
	}
}
