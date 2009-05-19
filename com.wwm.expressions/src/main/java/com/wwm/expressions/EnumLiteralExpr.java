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

public class EnumLiteralExpr extends LiteralExpr<Scalar> {

	private static final long serialVersionUID = -5076367762194675202L;

	protected final WrappedEnum value;
	
	public EnumLiteralExpr(Enum rhs) {
		super();
		this.value = new WrappedEnum(rhs);
	}
	
	@Override
	public Scalar evaluate(ExprContext context) {
		return new Scalar(value);
	}

	public WrappedEnum getValue() {
		return value;
	}

}
