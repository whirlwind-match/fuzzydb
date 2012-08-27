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

import java.util.Date;

public class DateLiteralExpr extends LiteralExpr<Scalar> {

	private static final long serialVersionUID = 5124785538672459991L;
	protected final WrappedDate value;
	
	public DateLiteralExpr(Date rhs) {
		super();
		this.value = new WrappedDate(rhs);
	}
	
	@Override
	public Scalar evaluate(ExprContext context) {
		return new Scalar(value);
	}

	public WrappedDate getValue() {
		return value;
	}
}
