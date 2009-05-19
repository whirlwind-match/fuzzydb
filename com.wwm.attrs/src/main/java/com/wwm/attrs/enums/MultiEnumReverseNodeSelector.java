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
package com.wwm.attrs.enums;



import com.wwm.attrs.internal.BranchConstraint;
import com.wwm.db.whirlwind.internal.IAttribute;


/**
 * Partially defined branch constraint to be used only for inserting into nodes.
 * This allows a branch to be specified as either having or not having a given enum value
 */
public class MultiEnumReverseNodeSelector extends BranchConstraint { 

    private static final long serialVersionUID = 1L;
    
    /**
     * The lowest value allowed in the node adorned by this seelctor
     */
    private short lowestAllowed;
    


	//================ CONSTRUCTORS, FINALISERS AND INITIALISERS ==================
	/**
	 * Default constructor.
	 */
	public MultiEnumReverseNodeSelector(){
	    super( 0 );
	}
	
    public MultiEnumReverseNodeSelector(MultiEnumReverseNodeSelector clonee) {
		super(clonee);
        this.lowestAllowed = clonee.lowestAllowed;
	}

    public MultiEnumReverseNodeSelector(int attrId, int lowestAllowed) {
		super(attrId);
        this.lowestAllowed = (short)lowestAllowed;
	}

    //========================= GETTERS AND SETTERS ==============================

	
    @Override
	public boolean consistent(IAttribute val) {
		if (val == null){
			return isIncludesNotSpecified();
		}
		
		// return true if the number of bits set is >= lowestAllowed
		if (val instanceof EnumMultipleValue){
			EnumMultipleValue enumVal = (EnumMultipleValue)val;
			return enumVal.getBitSet().cardinality() >= lowestAllowed;
		}

		throw new Error("Unsupported type");
	}
    
    /**
     * To support direct use from Layout and Compact codecs
     */
    public final boolean consistent( long attrBits ){
    	return Long.bitCount(attrBits) >= lowestAllowed;
    }
    

    @Override
	public MultiEnumReverseNodeSelector clone() {
        return new MultiEnumReverseNodeSelector( this );
    }

    
    @Override
	public String toString(){
        return "enum[" + lowestAllowed + "]";
    }

	@Override
	protected boolean expandNonNull(IAttribute value) {
        throw new UnsupportedOperationException("Cannot use MultiEnumReverseNodeSelector for expand");
	}
	
	@Override
	public boolean equals(Object rhs) {
        if (!(rhs instanceof MultiEnumReverseNodeSelector)) {
            return false;
        }
		MultiEnumReverseNodeSelector val = (MultiEnumReverseNodeSelector)rhs;
		return super.equals(val) 
        && this.lowestAllowed == val.lowestAllowed;
	}

	@Override
	public boolean isExpandedByNonNull(IAttribute value) {
        throw new UnsupportedOperationException("Cannot use MultiEnumReverseNodeSelector for expand");
	}
	
}
