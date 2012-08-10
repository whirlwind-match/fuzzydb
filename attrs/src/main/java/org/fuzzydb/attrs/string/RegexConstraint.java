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
package org.fuzzydb.attrs.string;


import org.fuzzydb.attrs.internal.BranchConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttribute;



public class RegexConstraint extends BranchConstraint {

    private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.BranchConstraint#consistent(likemynds.db.indextree.attributes.Attribute)
	 */
	@Override
	public boolean consistent(IAttribute attribute) {
		if (attribute == null){
			return isIncludesNotSpecified();
		}
		return true;
	}
	
	/**
	 * @param attrId
	 */
	public RegexConstraint(int attrId) {
		super(attrId);
	}

	public RegexConstraint(RegexConstraint clonee) {
		super(clonee);
	}
	
	@Override
	protected boolean expandNonNull(IAttribute value) {
	    return true;
	}

	@Override
	public boolean equals(Object rhs) {
        if(!(rhs instanceof RegexConstraint)) {
            return false;
        }
 		RegexConstraint val = (RegexConstraint) rhs;
		return getAttrId() == val.getAttrId()
        && super.equals(val);
	}

	@Override
	public String toString () {
		return "RegexConstraint";
	}

	@Override
	public RegexConstraint clone() {
		return new RegexConstraint(this);
	}


	@Override
	public boolean isExpandedByNonNull(IAttribute value) {
	    return true;
	}
}
