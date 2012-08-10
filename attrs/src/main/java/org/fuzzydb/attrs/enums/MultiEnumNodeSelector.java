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


/**
 * Partially defined branch constraint to be used only for inserting into nodes.
 * This allows a branch to be specified as either having or not having a given enum value
 */
public class MultiEnumNodeSelector extends BranchConstraint { 

    private static final long serialVersionUID = 1L;
    
    /**
     * The enum value that is consistent with this selector or -1 for any. 
     * e.g. 1 indicates that enum.getBitSet().get(1) should be true.
     * -1 indicates, always consistent. 
     */
    private short enumIndex;
    


	//================ CONSTRUCTORS, FINALISERS AND INITIALISERS ==================
	/**
	 * Default constructor.
	 */
	public MultiEnumNodeSelector(){
	    super( 0 );
	}
	
    public MultiEnumNodeSelector(MultiEnumNodeSelector clonee) {
		super(clonee);
        this.enumIndex = clonee.enumIndex;
	}

    public MultiEnumNodeSelector(int attrId, short enumIndex) {
		super(attrId);
        this.enumIndex = enumIndex;
	}

    //========================= GETTERS AND SETTERS ==============================

	
    @Override
	public boolean consistent(IAttribute val) {
		if (val == null){
			return isIncludesNotSpecified();
		}
		
		if (enumIndex == -1){
			return true;
		}
		
		if (val instanceof EnumMultipleValue){
			EnumMultipleValue enumVal = (EnumMultipleValue)val;
			return enumVal.getBitSet().get(enumIndex);
		}

		throw new RuntimeException("Unsupported type");
	}

    @Override
	public MultiEnumNodeSelector clone() {
        return new MultiEnumNodeSelector( this );
    }

    
    @Override
	public String toString(){
        return "enum[" + enumIndex + "]";
    }

	@Override
	protected boolean expandNonNull(IAttribute value) {
        throw new UnsupportedOperationException("Cannot use MultiEnumNodeSelector for expand");
	}
	
	@Override
	public boolean equals(Object rhs) {
        if (!(rhs instanceof MultiEnumNodeSelector)) {
            return false;
        }
		MultiEnumNodeSelector val = (MultiEnumNodeSelector)rhs;
		return super.equals(val) 
        && this.enumIndex == val.enumIndex;
	}

	@Override
	public boolean isExpandedByNonNull(IAttribute value) {
        throw new UnsupportedOperationException("Cannot use MultiEnumNodeSelector for expand");
	}
	
}
