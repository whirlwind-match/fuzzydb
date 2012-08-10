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
package org.fuzzydb.attrs.enums;

import java.io.Serializable;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IMergeable;


/**
 * An single IMMUTABLE value instance of a given enumeration, where only one (exclusive)
 * option can be chosen (e.g. of the Enumeration {NoSmoke, GivingUp, Smoke}
 * only one can be relevant for a single person.
 * @author ac
 */
public class EnumExclusiveValue extends EnumValue implements IMergeable, Comparable<EnumExclusiveValue>, Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final short WANT_NULL_VALUE = -2; // So we can use bit 0 for want null in main
	
	/** The index value in super.definition of this instance */
	protected short enumIndex; // FIXME: Should be byte!, as should enumDefId


	public EnumExclusiveValue(int attrId, short enumDefId, short enumIndex) {
		super(attrId, enumDefId);
		this.enumIndex = enumIndex;
	}
	
	/**
	 * copy constructor
	 */
	protected EnumExclusiveValue(EnumExclusiveValue enumValue) {
		super( enumValue.attrId, enumValue.enumDefId );
		this.enumIndex = enumValue.enumIndex;
	}

	@Override
	public boolean isWantNull() {
			return enumIndex == WANT_NULL_VALUE;
	}
	
	public short getEnumIndex() {
		return enumIndex;
	}


	/**
	 * Implement Comparable interface to allow values to be sorted
	 */
	public int compareTo( EnumExclusiveValue rval) {
		
		assert(rval.getAttrId() == this.getAttrId()); // Should only be called on matching ID
		
		// Compare definitions
		int defComp = this.enumDefId - rval.enumDefId;
		if (defComp != 0) return defComp;
		
		// If definitions same, then compare value
		return this.enumIndex - rval.enumIndex;
	}
	
	@Override
	public EnumExclusiveConstraint createAnnotation() {
		return new EnumExclusiveConstraint(getAttrId(), this.enumIndex);
	}

	public int compareAttribute(IAttribute rhs) {
		assert(rhs instanceof EnumExclusiveValue);
		return compareTo((EnumExclusiveValue) rhs);
	}
		
	@Override
	public EnumExclusiveValue clone() {
		return new EnumExclusiveValue( this );
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + enumIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnumExclusiveValue other = (EnumExclusiveValue) obj;
		if (enumIndex != other.enumIndex)
			return false;
		return true;
	}
}
