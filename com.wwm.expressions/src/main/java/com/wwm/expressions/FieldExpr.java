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

public class FieldExpr extends UnaryExpr<Scalar> {

	private static final long serialVersionUID = 3763097453469185585L;
	private final String fieldName;
	
	public FieldExpr(String fieldName) {
		super();
		this.fieldName = fieldName;
	}

	@Override
	public Scalar evaluate(ExprContext context) {
		Comparable<?> field = context.getField(fieldName);
		if (field instanceof Scalar) { 
			return (Scalar)field;
		}
		return new Scalar(field);
	}

	public String getFieldName() {
		return fieldName;
	}
}
