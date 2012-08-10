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



import org.fuzzydb.attrs.internal.BranchConstraint;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.util.BitSet64;



/**
 * @author ac
 * 
 */
public class EnumExclusiveConstraint extends BranchConstraint {

	private static final long serialVersionUID = 1L;

	private BitSet64 bits = new BitSet64();


	
	/**
	 * @return a BitSet64 where bit[i] indicates the enumValue with index, i, exists in this constraint.
	 */
	public BitSet64 getBitSet() {
		return bits;
	}

	
	/* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.BranchConstraint#consistent(likemynds.db.indextree.attributes.Attribute)
	 */
	@Override
	public boolean consistent(IAttribute attribute) {
		if (attribute == null){
			return isIncludesNotSpecified();
		}
		EnumExclusiveValue v = (EnumExclusiveValue)attribute;
		return bits.get( v.getEnumIndex() );
	}

	
	public boolean consistent(int enumIndex) {
		return bits.get( enumIndex );
	}
	
	
	/**
	 * @param attrId
	 */
	EnumExclusiveConstraint(int attrId, short index) {
		super(attrId);
		bits.set( index );
	}

	public EnumExclusiveConstraint(int attrId, short defId, long words, boolean inclNS) {
		super(attrId);
		bits.setWord( words );
		setIncludesNotSpecified(inclNS);
	}
	


	@Deprecated // use clone() etc
	private EnumExclusiveConstraint(EnumExclusiveConstraint clonee) {
		super(clonee);
		bits = (BitSet64)clonee.bits.clone();
	}
	
	@Override
	protected boolean expandNonNull(IAttribute value) {
		EnumExclusiveValue v = (EnumExclusiveValue)value;
		if (!bits.get(v.getEnumIndex())) {
			bits.set(v.getEnumIndex());
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object rhs) {
        if (!(rhs instanceof EnumExclusiveConstraint)) {
            return false;
        }
 		EnumExclusiveConstraint val = (EnumExclusiveConstraint) rhs;
		assert(getAttrId() == val.getAttrId() && super.equals(val));
		return bits.equals(val.bits);
	}

	@Override
	public String toString () {
		return "{ " +  bits.toString() + " }";
	}

	@Override
	public EnumExclusiveConstraint clone() {
		return new EnumExclusiveConstraint(this);
	}


}
