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

import com.wwm.db.core.exceptions.ArchException;

public class AddExpr extends BinaryExpr {

	private static final long serialVersionUID = 3906934456379324210L;

	public AddExpr(ComparableExpr<Scalar> left, ComparableExpr<Scalar> right) {
		super(left, right);
	}

	@Override
	public Scalar evaluate(ExprContext context) throws ArchException {
		Scalar l = left.evaluate(context);
		Scalar r = right.evaluate(context);
		return l.add(r);
	}

}
