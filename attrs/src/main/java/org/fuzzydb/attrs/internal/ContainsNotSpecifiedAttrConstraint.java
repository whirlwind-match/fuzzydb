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

import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeConstraint;

/**
 * Dummy "NodeAnnotation" for dealing only with InclduesNotspecified
 * Used in NodeAnnotationContainer on each Branch (and duplicated on LeafNode), to specify the full range of values
 * actually contained within the respective node.
 *  
 */
public class ContainsNotSpecifiedAttrConstraint implements IAttributeConstraint {

	private static final long serialVersionUID = 425720663301485948L;

//	private boolean includesNotSpecified = false; // Node contains items where this attribue is not specified
	private int attrId;
	
	@SuppressWarnings("unused")
	private ContainsNotSpecifiedAttrConstraint(){
		// explicitly no default constructor
	}
	
//	private NodeAnnotation(BranchConstraint bc, boolean includesNotSpecified) {
//		this.bc = bc;
//		this.includesNotSpecified = includesNotSpecified;
//		this.attrId = bc.getAttrId();
//	}

	public ContainsNotSpecifiedAttrConstraint(int attrId) {
//		this.includesNotSpecified = true;
		this.attrId = attrId;
	}
	

//	public NodeAnnotation(boolean includesNotSpecified, int attrId) {
//		this.includesNotSpecified = includesNotSpecified;
//		this.attrId = attrId;
//	}

	/**
	 * @return true if node contains items where this attribute is not specified.
	 */
	public boolean isIncludesNotSpecified() {
		return true; // includesNotSpecified;
	}

	public void setIncludesNotSpecified(boolean includesNotSpecified) {
		assert(includesNotSpecified == true); // must be true
//		this.includesNotSpecified = includesNotSpecified;
	}
	
	public int getAttrId() {
		return attrId;
	}
	
	/**
	 * To support when putting into an array
	 */
	public void setAttrId(int attrId) {
		this.attrId = attrId;
	}
	
    @Override
	public IAttributeConstraint clone() {
    	return new ContainsNotSpecifiedAttrConstraint(attrId);
    }
	
    @Override
    public String toString() {
    	return "incl null = true"; // + includesNotSpecified ;
    }

	public Object asSimpleAttribute() {
		return this;
	}

	public boolean expand(Attribute att) {
		throw new UnsupportedOperationException(); // can't expand... only replace
	}

	public boolean isExpandedBy(IAttribute value) {
		return (value != null); // A null node will always be expanded by a non-null attribute
	}

	public boolean consistent(IAttribute value) {
		return (value == null); // Value is consistent only if it is not specified
	}

	public boolean equals(IAttributeConstraint rhs) {
		throw new UnsupportedOperationException();
	}

	public boolean expand(IAttribute value) {
		throw new UnsupportedOperationException();
	}

	public int compareAttribute(IAttribute rhs) {
		throw new UnsupportedOperationException();
	}
}
