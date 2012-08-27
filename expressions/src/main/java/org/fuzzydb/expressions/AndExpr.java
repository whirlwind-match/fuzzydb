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

import org.fuzzydb.core.exceptions.ArchException;

public class AndExpr extends BoolExpr {

	private static final long serialVersionUID = 3256721788489840948L;

	public AndExpr(LogicExpr left, LogicExpr right) {
		super(left,right);
	}

	@Override
	public boolean evaluate(ExprContext context) {
		return left.evaluate(context) && right.evaluate(context);
	}
}
