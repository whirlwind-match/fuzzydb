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
package org.fuzzydb.expressions;

public abstract class RangeExpr<T> extends LogicExpr {

	protected ComparableExpr<T> term;
	protected ComparableExpr<T> low;
	protected ComparableExpr<T> high;

	public RangeExpr(ComparableExpr<T> term, ComparableExpr<T> low, ComparableExpr<T> high) {
		this.term = term;
		this.low = low;
		this.high = high;
	}

	public ComparableExpr<T> getTerm() {
		return term;
	}

	public ComparableExpr<T> getLow() {
		return low;
	}	

	public ComparableExpr<T> getHigh() {
		return high;
	}		
}
