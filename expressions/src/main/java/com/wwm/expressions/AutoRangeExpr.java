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
package com.wwm.expressions;

import org.fuzzydb.core.exceptions.ArchException;

public class AutoRangeExpr<T extends Comparable<?>> extends RangeExpr<T> {

	private static final long serialVersionUID = 3257570602776080694L;

	public AutoRangeExpr(ComparableExpr<T> term, ComparableExpr<T> low, ComparableExpr<T> high) {
		super(term, low, high);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean evaluate(ExprContext context) {
		Comparable t = term.evaluate(context);
		Comparable l = low.evaluate(context);
		Comparable h = high.evaluate(context);
		return l.compareTo(t) <= 0 && t.compareTo(h) < 0;
	}
}
