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


/**
 * Class that defines a constraint against which different attributes
 * can be tested as to whether they are consistent with that constraint.
 * This could be a range, or it could be a set of enum haves
 *
 * TODO Establish whether a BranchConstraint can overlap others (I think it can)
 *
 * @author Neale
 */
public abstract class BranchConstraint extends BaseAttribute implements IAttributeConstraint, Cloneable  { // Although we don't need getSelectorClass() (selector is like a constraint...)

	private static final long serialVersionUID = 2046031653159409861L;

	private boolean includesNotSpecified = false; // Node contains items where this attribue is not specified

	/**
	 * @param attrId - the attribute ID of the attribute this BranchConstraint constrains.
	 */
	protected BranchConstraint(int attrId) {
		super(attrId);
	}

	protected BranchConstraint(BranchConstraint clonee) {
		super(clonee);
		includesNotSpecified = clonee.includesNotSpecified;
	}

	protected BranchConstraint(int attrId, boolean includesNotSpecified) {
        super(attrId);
        this.includesNotSpecified = includesNotSpecified;
    }

    /* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.IAttributeConstraint#clone()
	 */
	public abstract @Override BranchConstraint clone();


	/* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.IAttributeConstraint#consistent(org.fuzzydb.client.core.whirlwind.internal.IAttribute)
	 */
	public abstract boolean consistent(IAttribute value);

	/* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.IAttributeConstraint#expand(org.fuzzydb.client.core.whirlwind.internal.IAttribute)
	 */
	public boolean expand(IAttribute value) {
		if (value == null){
			if (isIncludesNotSpecified()){
				return false; // already contained null entries
			} else {
				setIncludesNotSpecified(true);
				return true; // we expanded this node
			}
		}
		return expandNonNull(value);
	}
	
	
	/**
	 * This must be implemented to deal with non-null cases
	 * @param value
	 * @return true if value expanded the constraint
	 */
	protected abstract boolean expandNonNull(IAttribute value);

	
	/* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.IAttributeConstraint#canExpand(org.fuzzydb.client.core.whirlwind.internal.IAttribute)
	 */
	public boolean isExpandedBy(IAttribute value){
		// add support for null here, just in case consistent doesn't support it
		if (value == null){
			return !includesNotSpecified; // If there are not yet any nulls, then a null will expand it.
		}
		return isExpandedByNonNull(value);
	}
	
	// override this for tricky bits
	protected boolean isExpandedByNonNull(IAttribute value) {
		return !consistent( value );
	}

	public Object asSimpleAttribute() {
		return this; // Default: Needs overriding for DB1 attrs.
	}

	/* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.IAttributeConstraint#isIncludesNotSpecified()
	 */
	public boolean isIncludesNotSpecified() {
		return includesNotSpecified;
	}

	/* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.IAttributeConstraint#setIncludesNotSpecified(boolean)
	 */
	public void setIncludesNotSpecified(boolean includesNotSpecified) {
		this.includesNotSpecified = includesNotSpecified;
	}
    
    @Override
    public boolean equals(Object obj) {
        
        if (!(obj instanceof BranchConstraint)) {
            return false;
        }
        
        BranchConstraint bc = (BranchConstraint)obj;
        return (bc.attrId == attrId && bc.includesNotSpecified == includesNotSpecified);
    }
    
    
    public int compareAttribute(IAttribute rhs) {
    	throw new UnsupportedOperationException("compareAttribute not supported on constraints");
    }
}
