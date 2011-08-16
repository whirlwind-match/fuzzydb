/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.db.whirlwind.internal;


/**
 * Interface to an object that represents a range of values that may or may not match.
 * Specified cases include:
 * - a range of values (e.g. 0.5 to 0.75, or Red, Blue, White (enum) )
 * - a range of values plus the 'not specified' case (the null case should either succeed or fail somewhere)
 * - no values, just a match on 'not specified'.
 * 
 * SOME USEFUL COMMENTS FOUND SOMEWHERE RELATING TO OLD BranchConstraint/NodeAnnotation IMPL         
 *  na == null All Items under this node have null for this attribute
 *  na.hasValue() do any Items under this node have this attribute
 *  na.isIncludesNotSpecified Some Items under this node have null for this attribute
 *  
 *  NOW:  na == null -> same meaning (that attribute doesn't exist anywhere
 *        na != null -> some or all non-nulls exist below
 *        na.isIncludesNotSpecified -> also has nulls
 */
public interface IAttributeConstraint extends IAttribute, Cloneable {

	public IAttributeConstraint clone();

	/**
	 * Check if the given value satisfies this constraint
	 * @return true if value satisfies this constraint (this must include correct behaviour for value=null)
	 */
	public boolean consistent(IAttribute value);

	/**
	 * Expand the constraint to just include the specified value.
	 */
	public boolean expand(IAttribute value);

	/**
	 * Check if the constraint already covers the given constraint (not sure how this differs from consistent)
	 * NOTE: We think this is always !Consistent()
	 */
	public boolean isExpandedBy(IAttribute value);

	
	/**
	 * Test whether the range of values covered includes the 'not specified' case.
	 */
	public boolean isIncludesNotSpecified();

	public void setIncludesNotSpecified(boolean includesNotSpecified);

}
