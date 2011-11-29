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
package com.wwm.attrs.unused;


import com.wwm.attrs.internal.BranchConstraint;
import com.wwm.attrs.internal.Value;
import com.wwm.attrs.util.Range;
import com.wwm.db.whirlwind.internal.IAttribute;


/**
 * This is really "RangeBranchConstraint" - built with two Comparable objects
 * @author ac
 *
 */
public abstract class GenericRangeConstraint extends BranchConstraint implements IRange {
	
	protected Comparable<Object> min;
	protected Comparable<Object> max;

	/**
	 * @return Returns the max.
	 */
	public final Comparable<Object> getMax() {
		return max;
	}
	
	
	/**
	 * @return Returns the min.
	 */
	public final Comparable<Object> getMin() {
		return min;
	}

	/**
	 * @param min
	 * @param max
	 */
	public GenericRangeConstraint(int attrId, Comparable<Object> min, Comparable<Object> max) {
	    super( attrId );
		this.min = min;
		this.max = max;
	}
	
	
	public final boolean contains (Comparable<Object> val) {
		return Range.contains( min, val, max );

	}
	
	
	public final boolean contains (Comparable<Object> min, Comparable<Object> max) {
		return contains(min) && contains(max);
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.BranchConstraint#consistent(likemynds.db.indextree.attributes.Have)
	 */
	@Override
	public final boolean consistent (IAttribute val) {
		if (val == null){
			return isIncludesNotSpecified();
		}
		if (val instanceof Value) {
			return contains( ((Value) val).getValue() );
		} else {
			assert(false);	// The supplied attribute isn't a value
			return false;
		}
	}
}
