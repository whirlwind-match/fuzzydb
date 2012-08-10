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
package org.fuzzydb.attrs.bool;


import org.fuzzydb.attrs.internal.BranchConstraint;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;



/**
 * Constraint for specifying what is contained in a given branch
 * @author ac
 */
public class BooleanConstraint extends BranchConstraint /*implements IBooleanValue*/ {
	
	public enum State {
		hasTrue, hasFalse, hasBoth;
	}

	private static final long serialVersionUID = 3256440291987699764L;
	State	state;

    public BooleanConstraint() {
        super(0);
    }

	public BooleanConstraint(int attrId, boolean isTrue) {
		super(attrId);
		this.state = isTrue ? State.hasTrue : State.hasFalse;
	}

	public BooleanConstraint(int attrId, State state) {
		super(attrId);
		this.state = state;
	}

	
	public BooleanConstraint(BooleanConstraint clonee) {
		super(clonee);
		this.state = clonee.state;
	}
	
    public BooleanConstraint(int attrId, State state, boolean hasNulls) {
        super(attrId, hasNulls);
        this.state = state;
    }

    /* (non-Javadoc)
	 * @see likemynds.db.indextree.depricated.AttributePreference#score(likemynds.db.indextree.SearchAttributeContainer)
	 */
	public float score(IAttributeMap<IAttribute> c) {
	    BooleanValue sv = (BooleanValue)c.findAttr(attrId);
		if (sv != null) {
			switch (state) {
			case hasTrue:
				return sv.isTrue() ? 1.0f : 0.0f;
			case hasFalse:
				return sv.isTrue() ? 0.0f : 1.0f;
			case hasBoth:
				return 1.0f;
			}
		}

		return 1.0f;
	}

	
	
	
	/* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.BranchConstraint#consistentWant(likemynds.db.indextree.attributes.Want)
	 */
	@Override
	public boolean consistent(IAttribute value) {
		
		if (value == null){
			return isIncludesNotSpecified();
		}
		
		BooleanValue bv = (BooleanValue)value;
		boolean isTrue = bv.isTrue();
		return consistent(isTrue);
	}

	
	public final boolean consistent(boolean isTrue) {
		switch (state) {
		case hasTrue:
			return isTrue;
		case hasFalse:
			return !isTrue;
		case hasBoth:
			return true;
		}
		assert(false);
		return false;
	}


	/* (non-Javadoc)
	 * @see org.fuzzydb.attrs.bool.IBooleanValue#isTrue()
	 */
	public State getState() {
		return state;
	}

	@Override
	protected boolean expandNonNull(IAttribute value) {
		
		BooleanValue bv = (BooleanValue)value;
		switch (state) {
		case hasBoth:
			return false;
		case hasTrue:
			if (!bv.isTrue()) {
				state = State.hasBoth;
				return true;
			}
			return false;
		case hasFalse:
			if (bv.isTrue()) {
				state = State.hasBoth;
				return true;
			}
			return false;
		}
		assert(false);
		return false;
	}

	@Override
	public boolean equals(Object rhs) {
		if (rhs == null || rhs instanceof BooleanConstraint == false){
			return false;
		}
		BooleanConstraint val = (BooleanConstraint)rhs;
		return (this.state == val.state && super.equals(val));
	}

    @Override
    public int hashCode() {
    	// Add something unique'ish for true
    	switch (state) {
		case hasFalse:
			return super.hashCode() + 0x30000074;
		case hasTrue:
			return super.hashCode() + 0;
		case hasBoth:
			return super.hashCode() + 0x80000041;
    	}
		throw new RuntimeException(); // Shouldn't have got here
    }

	@Override
	public final BooleanConstraint clone() {
		return new BooleanConstraint(this);
	}

	@Override
	public String toString() {
		return this.state.toString();
	}
}
