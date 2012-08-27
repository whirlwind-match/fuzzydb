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

public class StringLiteralExpr extends LiteralExpr<String> {

	private static final long serialVersionUID = 1L;

	protected final String value;
	
	public StringLiteralExpr(String rhs) {
		super();
		this.value = rhs;
	}
	
	public String getValue() {
		return value;
	}

	@Override
	public String evaluate(ExprContext context) {
		return value;
	}

}
