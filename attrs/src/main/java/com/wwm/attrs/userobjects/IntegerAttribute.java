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
package com.wwm.attrs.userobjects;


import com.wwm.attrs.internal.Attribute;
import com.wwm.attrs.simple.IntegerConstraint;
import com.wwm.db.whirlwind.internal.IAttribute;



public class IntegerAttribute extends Attribute<IntegerAttribute> {

	private static final long serialVersionUID = 1L;
	int value;
	
	public IntegerAttribute(int attrId, int value) {
		super(attrId);
		this.value = value;
	}		

	public IntegerAttribute(IntegerAttribute clonee) {
		super(clonee);
		this.value = clonee.value;
	}


	@Override
	public IntegerConstraint createAnnotation() {
		return new IntegerConstraint( getAttrId(), value, value );
	}

	public void setValue(int i) {
		value = i;
	}

	public int getValue() {
		return value;
	}
	
	public int compareAttribute(IAttribute rhs) {
		return compareTo(rhs);
	}

	@Override
	public IntegerAttribute clone() {
		return new IntegerAttribute(this);
	}

	public int compareTo(Object o) {
        
    	if (o instanceof IntegerAttribute) {
    		IntegerAttribute fv = (IntegerAttribute)o;
    	    if (value < fv.value) return -1;
    	    else if (value == fv.value) return 0;
    	    else return 1;
    	}
    
    	throw new ClassCastException("Illegal type for compareTo");
    }

	@Override
	public boolean equals(Object o) {
		IntegerAttribute rhs = (IntegerAttribute)o;
		return value == rhs.value && attrId == rhs.attrId;
	}

}
