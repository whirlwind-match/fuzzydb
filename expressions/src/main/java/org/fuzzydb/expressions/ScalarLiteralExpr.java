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

public class ScalarLiteralExpr extends LiteralExpr<Scalar> {

	private static final long serialVersionUID = 3256446919139078199L;
	protected final Scalar value;

	public ScalarLiteralExpr(Comparable<?> value) {
		super();
		this.value = new Scalar(value);
	}
	
	
	public ScalarLiteralExpr(int i){
		this.value = new Scalar(i);
	}

	public ScalarLiteralExpr(long l){
		this.value = new Scalar(l);
	}

	public ScalarLiteralExpr(float f){
		this.value = new Scalar(f);
	}

	public ScalarLiteralExpr(double d){
		this.value = new Scalar(d);
	}

	@Override
	public Scalar evaluate(ExprContext context) {
		return value;
	}

	public Comparable<?> getValue() {
		return value;
	}
}
