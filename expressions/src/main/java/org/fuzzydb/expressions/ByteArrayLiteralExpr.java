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

public class ByteArrayLiteralExpr extends LiteralExpr<ComparableByteArray> {

	private static final long serialVersionUID = -5076367762194675202L;

	protected final ComparableByteArray value;
	
	public ByteArrayLiteralExpr(byte[] rhs) {
		super();
		this.value = new ComparableByteArray(rhs);
	}
	
	@Override
	public ComparableByteArray evaluate(ExprContext context) {
		return value;
	}

	public ComparableByteArray getValue() {
		return value;
	}

}
