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

import java.lang.reflect.Field;
import java.util.Date;

/**
 * FIXME: All these need Generifying so that we get things like 
 * expr = factory.add( ComparableExpr<Float>, 1.0f );
 * @author Administrator
 *
 */
public class QueryFactory {

	private Class<?> clazz;

	public QueryFactory(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	// Math Ops
	
	// Expr-Expr
	@SuppressWarnings("unchecked")
	public ComparableExpr add(ComparableExpr lhs, ComparableExpr rhs) { return new AddExpr(lhs, rhs); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(ComparableExpr lhs, ComparableExpr rhs) { return new SubExpr(lhs, rhs); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(ComparableExpr lhs, ComparableExpr rhs) { return new MultExpr(lhs, rhs); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(ComparableExpr lhs, ComparableExpr rhs) { return new DivExpr(lhs, rhs); }

	// Field-Field
	@SuppressWarnings("unchecked")
	public ComparableExpr add(Field lhs, Field rhs) { assert(lhs.getDeclaringClass() == clazz && rhs.getDeclaringClass() == clazz); return new AddExpr(new FieldExpr(lhs.getName()), new FieldExpr(rhs.getName()) ); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(Field lhs, Field rhs) { assert(lhs.getDeclaringClass() == clazz && rhs.getDeclaringClass() == clazz); return new SubExpr(new FieldExpr(lhs.getName()), new FieldExpr(rhs.getName()) ); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(Field lhs, Field rhs) { assert(lhs.getDeclaringClass() == clazz && rhs.getDeclaringClass() == clazz); return new MultExpr(new FieldExpr(lhs.getName()), new FieldExpr(rhs.getName()) ); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(Field lhs, Field rhs) { assert(lhs.getDeclaringClass() == clazz && rhs.getDeclaringClass() == clazz); return new DivExpr(new FieldExpr(lhs.getName()), new FieldExpr(rhs.getName()) ); }

	// Field-Expr
	@SuppressWarnings("unchecked")
	public ComparableExpr add(ComparableExpr lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new AddExpr(lhs, new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(ComparableExpr lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new SubExpr(lhs, new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(ComparableExpr lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new MultExpr(lhs, new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(ComparableExpr lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new DivExpr(lhs, new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public ComparableExpr add(Field lhs, ComparableExpr rhs) { assert(lhs.getDeclaringClass() == clazz); return new AddExpr(new FieldExpr(lhs.getName()), rhs ); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(Field lhs, ComparableExpr rhs) { assert(lhs.getDeclaringClass() == clazz); return new SubExpr(new FieldExpr(lhs.getName()), rhs ); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(Field lhs, ComparableExpr rhs) { assert(lhs.getDeclaringClass() == clazz); return new MultExpr(new FieldExpr(lhs.getName()), rhs ); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(Field lhs, ComparableExpr rhs) { assert(lhs.getDeclaringClass() == clazz); return new DivExpr(new FieldExpr(lhs.getName()), rhs ); }
	
	// Literal-Expr
	@SuppressWarnings("unchecked")
	public ComparableExpr add(ComparableExpr lhs, float rhs) { return new AddExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr add(ComparableExpr lhs, double rhs) { return new AddExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr add(ComparableExpr lhs, int rhs) { return new AddExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr add(ComparableExpr lhs, long rhs) { return new AddExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(ComparableExpr lhs, float rhs) { return new SubExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(ComparableExpr lhs, double rhs) { return new SubExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(ComparableExpr lhs, int rhs) { return new SubExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(ComparableExpr lhs, long rhs) { return new SubExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(ComparableExpr lhs, float rhs) { return new MultExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(ComparableExpr lhs, double rhs) { return new MultExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(ComparableExpr lhs, int rhs) { return new MultExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(ComparableExpr lhs, long rhs) { return new MultExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(ComparableExpr lhs, float rhs) { return new DivExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(ComparableExpr lhs, double rhs) { return new DivExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(ComparableExpr lhs, int rhs) { return new DivExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(ComparableExpr lhs, long rhs) { return new DivExpr(lhs, new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(float lhs, ComparableExpr rhs) { return new DivExpr(new ScalarLiteralExpr(lhs), rhs); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(double lhs, ComparableExpr rhs) { return new DivExpr(new ScalarLiteralExpr(lhs), rhs); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(int lhs, ComparableExpr rhs) { return new DivExpr(new ScalarLiteralExpr(lhs), rhs); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(long lhs, ComparableExpr rhs) { return new DivExpr(new ScalarLiteralExpr(lhs), rhs); }
	
	// Literal-Field
	@SuppressWarnings("unchecked")
	public ComparableExpr add(Field lhs, float rhs) { assert(lhs.getDeclaringClass() == clazz); return new AddExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr add(Field lhs, double rhs) { assert(lhs.getDeclaringClass() == clazz); return new AddExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr add(Field lhs, int rhs) { assert(lhs.getDeclaringClass() == clazz); return new AddExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr add(Field lhs, long rhs) { assert(lhs.getDeclaringClass() == clazz); return new AddExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(Field lhs, float rhs) { assert(lhs.getDeclaringClass() == clazz); return new SubExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(Field lhs, double rhs) { assert(lhs.getDeclaringClass() == clazz); return new SubExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(Field lhs, int rhs) { assert(lhs.getDeclaringClass() == clazz); return new SubExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr sub(Field lhs, long rhs) { assert(lhs.getDeclaringClass() == clazz); return new SubExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(Field lhs, float rhs) { assert(lhs.getDeclaringClass() == clazz); return new MultExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(Field lhs, double rhs) { assert(lhs.getDeclaringClass() == clazz); return new MultExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(Field lhs, int rhs) { assert(lhs.getDeclaringClass() == clazz); return new MultExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr mult(Field lhs, long rhs) { assert(lhs.getDeclaringClass() == clazz); return new MultExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(Field lhs, float rhs) { assert(lhs.getDeclaringClass() == clazz); return new DivExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(Field lhs, double rhs) { assert(lhs.getDeclaringClass() == clazz); return new DivExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(Field lhs, int rhs) { assert(lhs.getDeclaringClass() == clazz); return new DivExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(Field lhs, long rhs) { assert(lhs.getDeclaringClass() == clazz); return new DivExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(float lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new DivExpr(new ScalarLiteralExpr(lhs), new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(double lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new DivExpr(new ScalarLiteralExpr(lhs), new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(int lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new DivExpr(new ScalarLiteralExpr(lhs), new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public ComparableExpr div(long lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new DivExpr(new ScalarLiteralExpr(lhs), new FieldExpr(rhs.getName())); }
	
	// Rel ops
	
	// expr-expr
	@SuppressWarnings("unchecked")
	public LogicExpr lessThan(ComparableExpr lhs, ComparableExpr rhs) { return new LessThanExpr(lhs, rhs); }
	@SuppressWarnings("unchecked")
	public LogicExpr lessThanEqual(ComparableExpr lhs, ComparableExpr rhs) { return new LessThanEqualExpr(lhs, rhs); }
	@SuppressWarnings("unchecked")
	public LogicExpr moreThan(ComparableExpr lhs, ComparableExpr rhs) { return new GreaterThanExpr(lhs, rhs); }
	@SuppressWarnings("unchecked")
	public LogicExpr moreThanEqual(ComparableExpr lhs, ComparableExpr rhs) { return new GreaterThanEqualExpr(lhs, rhs); }
	@SuppressWarnings("unchecked")
	public LogicExpr equal(ComparableExpr lhs, ComparableExpr rhs) { return new EqualToExpr(lhs, rhs); }
	@SuppressWarnings("unchecked")
	public LogicExpr unequal(ComparableExpr lhs, ComparableExpr rhs) { return not(new EqualToExpr(lhs, rhs)); }
	
	// expr [expr-expr]
	@SuppressWarnings("unchecked")
	public LogicExpr autoRange(ComparableExpr term, ComparableExpr low, ComparableExpr high) { return new AutoRangeExpr(term, low, high); }
	@SuppressWarnings("unchecked")
	public LogicExpr incRange(ComparableExpr term, ComparableExpr low, ComparableExpr high) { return new IncRangeExpr(term, low, high); }
	@SuppressWarnings("unchecked")
	public LogicExpr excRange(ComparableExpr term, ComparableExpr low, ComparableExpr high) { return new ExcRangeExpr(term, low, high); }

	// field-expr
	@SuppressWarnings("unchecked")
	public LogicExpr lessThan(Field lhs, ComparableExpr rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanExpr(new FieldExpr(lhs.getName()), rhs); }
	@SuppressWarnings("unchecked")
	public LogicExpr lessThan(ComparableExpr lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new LessThanExpr(lhs, new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public LogicExpr lessThanEqual(Field lhs, ComparableExpr rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanEqualExpr(new FieldExpr(lhs.getName()), rhs); }
	@SuppressWarnings("unchecked")
	public LogicExpr lessThanEqual(ComparableExpr lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new LessThanEqualExpr(lhs, new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public LogicExpr moreThan(Field lhs, ComparableExpr rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanExpr(new FieldExpr(lhs.getName()), rhs); }
	@SuppressWarnings("unchecked")
	public LogicExpr moreThan(ComparableExpr lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new GreaterThanExpr(lhs, new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public LogicExpr moreThanEqual(Field lhs, ComparableExpr rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanEqualExpr(new FieldExpr(lhs.getName()), rhs); }
	@SuppressWarnings("unchecked")
	public LogicExpr moreThanEqual(ComparableExpr lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new GreaterThanEqualExpr(lhs, new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public LogicExpr equal(Field lhs, ComparableExpr rhs) { assert(lhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), rhs); }
	@SuppressWarnings("unchecked")
	public LogicExpr equal(ComparableExpr lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return new EqualToExpr(lhs, new FieldExpr(rhs.getName())); }
	@SuppressWarnings("unchecked")
	public LogicExpr unequal(Field lhs, ComparableExpr rhs) { assert(lhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), rhs)); }
	@SuppressWarnings("unchecked")
	public LogicExpr unequal(ComparableExpr lhs, Field rhs) { assert(rhs.getDeclaringClass() == clazz); return not(new EqualToExpr(lhs, new FieldExpr(rhs.getName()))); }

	// field [expr-expr]
	@SuppressWarnings("unchecked")
	public LogicExpr autoRange(Field term, ComparableExpr low, ComparableExpr high) { assert(term.getDeclaringClass() == clazz); return new AutoRangeExpr(new FieldExpr(term.getName()), low, high); }
	@SuppressWarnings("unchecked")
	public LogicExpr incRange(Field term, ComparableExpr low, ComparableExpr high) { assert(term.getDeclaringClass() == clazz); return new IncRangeExpr(new FieldExpr(term.getName()), low, high); }
	@SuppressWarnings("unchecked")
	public LogicExpr excRange(Field term, ComparableExpr low, ComparableExpr high) { assert(term.getDeclaringClass() == clazz); return new ExcRangeExpr(new FieldExpr(term.getName()), low, high); }
	 
	// field-field
	public LogicExpr lessThan(Field lhs, Field rhs) { assert(lhs.getDeclaringClass() == clazz && rhs.getDeclaringClass() == clazz); return new LessThanExpr(new FieldExpr(lhs.getName()), new FieldExpr(rhs.getName())); }
	public LogicExpr lessThanEqual(Field lhs, Field rhs) { assert(lhs.getDeclaringClass() == clazz && rhs.getDeclaringClass() == clazz); return new LessThanEqualExpr(new FieldExpr(lhs.getName()), new FieldExpr(rhs.getName())); }
	public LogicExpr moreThan(Field lhs, Field rhs) { assert(lhs.getDeclaringClass() == clazz && rhs.getDeclaringClass() == clazz); return new GreaterThanExpr(new FieldExpr(lhs.getName()), new FieldExpr(rhs.getName())); }
	public LogicExpr moreThanEqual(Field lhs, Field rhs) { assert(lhs.getDeclaringClass() == clazz && rhs.getDeclaringClass() == clazz); return new GreaterThanEqualExpr(new FieldExpr(lhs.getName()), new FieldExpr(rhs.getName())); }
	public LogicExpr equal(Field lhs, Field rhs) { assert(lhs.getDeclaringClass() == clazz && rhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), new FieldExpr(rhs.getName())); }
	public LogicExpr unequal(Field lhs, Field rhs) { assert(lhs.getDeclaringClass() == clazz && rhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), new FieldExpr(rhs.getName()))); }
	
	// field-literal
	public LogicExpr lessThan(Field lhs, Comparable<?> rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr lessThan(Field lhs, int rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr lessThan(Field lhs, long rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr lessThan(Field lhs, float rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr lessThan(Field lhs, double rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr lessThan(Field lhs, String rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanExpr(new FieldExpr(lhs.getName()), new StringLiteralExpr(rhs)); }
	public LogicExpr lessThan(Field lhs, Date rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanExpr(new FieldExpr(lhs.getName()), new DateLiteralExpr(rhs)); }
	
	public LogicExpr lessThanEqual(Field lhs, Comparable<?> rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanEqualExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr lessThanEqual(Field lhs, int rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanEqualExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr lessThanEqual(Field lhs, long rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanEqualExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr lessThanEqual(Field lhs, float rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanEqualExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr lessThanEqual(Field lhs, double rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanEqualExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr lessThanEqual(Field lhs, String rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanEqualExpr(new FieldExpr(lhs.getName()), new StringLiteralExpr(rhs)); }
	public LogicExpr lessThanEqual(Field lhs, Date rhs) { assert(lhs.getDeclaringClass() == clazz); return new LessThanEqualExpr(new FieldExpr(lhs.getName()), new DateLiteralExpr(rhs)); }

	public LogicExpr moreThan(Field lhs, Comparable<?> rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr moreThan(Field lhs, int rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr moreThan(Field lhs, long rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr moreThan(Field lhs, float rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr moreThan(Field lhs, double rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr moreThan(Field lhs, String rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanExpr(new FieldExpr(lhs.getName()), new StringLiteralExpr(rhs)); }
	public LogicExpr moreThan(Field lhs, Date rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanExpr(new FieldExpr(lhs.getName()), new DateLiteralExpr(rhs)); }

	public LogicExpr moreThanEqual(Field lhs, Comparable<?> rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanEqualExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr moreThanEqual(Field lhs, int rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanEqualExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr moreThanEqual(Field lhs, long rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanEqualExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr moreThanEqual(Field lhs, float rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanEqualExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr moreThanEqual(Field lhs, double rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanEqualExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr moreThanEqual(Field lhs, String rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanEqualExpr(new FieldExpr(lhs.getName()), new StringLiteralExpr(rhs)); }
	public LogicExpr moreThanEqual(Field lhs, Date rhs) { assert(lhs.getDeclaringClass() == clazz); return new GreaterThanEqualExpr(new FieldExpr(lhs.getName()), new DateLiteralExpr(rhs)); }
	
	public LogicExpr equal(Field lhs, Comparable<?> rhs) { assert(lhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr equal(Field lhs, int rhs) { assert(lhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr equal(Field lhs, long rhs) { assert(lhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr equal(Field lhs, float rhs) { assert(lhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr equal(Field lhs, double rhs) { assert(lhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs)); }
	public LogicExpr equal(Field lhs, String rhs) { assert(lhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), new StringLiteralExpr(rhs)); }
	public LogicExpr equal(Field lhs, Date rhs) { assert(lhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), new DateLiteralExpr(rhs)); }
	public LogicExpr equal(Field lhs, byte[] rhs) { assert(lhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), new ByteArrayLiteralExpr(rhs)); }
	public LogicExpr equal(Field lhs, Enum<?> rhs) { assert(lhs.getDeclaringClass() == clazz); return new EqualToExpr(new FieldExpr(lhs.getName()), new EnumLiteralExpr(rhs));  }
	
	public LogicExpr unequal(Field lhs, Comparable<?> rhs) { assert(lhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs))); }
	public LogicExpr unequal(Field lhs, int rhs) { assert(lhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs))); }
	public LogicExpr unequal(Field lhs, long rhs) { assert(lhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs))); }
	public LogicExpr unequal(Field lhs, float rhs) { assert(lhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs))); }
	public LogicExpr unequal(Field lhs, double rhs) { assert(lhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), new ScalarLiteralExpr(rhs))); }
	public LogicExpr unequal(Field lhs, String rhs) { assert(lhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), new StringLiteralExpr(rhs))); }
	public LogicExpr unequal(Field lhs, Date rhs) { assert(lhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), new DateLiteralExpr(rhs))); }
	public LogicExpr unequal(Field lhs, byte[] rhs) { assert(lhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), new ByteArrayLiteralExpr(rhs))); }
	public LogicExpr unequal(Field lhs, Enum<?> rhs) { assert(lhs.getDeclaringClass() == clazz); return not(new EqualToExpr(new FieldExpr(lhs.getName()), new EnumLiteralExpr(rhs)));  }

	// field [literal-literal]
	@SuppressWarnings("unchecked")
	public LogicExpr autoRange(Field term, Comparable<?> low, Comparable<?> high) { assert(term.getDeclaringClass() == clazz); return new AutoRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr autoRange(Field term, int low, int high) { assert(term.getDeclaringClass() == clazz); return new AutoRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr autoRange(Field term, long low, long high) { assert(term.getDeclaringClass() == clazz); return new AutoRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr autoRange(Field term, float low, float high) { assert(term.getDeclaringClass() == clazz); return new AutoRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr autoRange(Field term, double low, double high) { assert(term.getDeclaringClass() == clazz); return new AutoRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr autoRange(Field term, String low, String high) { assert(term.getDeclaringClass() == clazz); return new AutoRangeExpr(new FieldExpr(term.getName()), new StringLiteralExpr(low), new StringLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr autoRange(Field term, Date low, Date high) { assert(term.getDeclaringClass() == clazz); return new AutoRangeExpr(new FieldExpr(term.getName()), new DateLiteralExpr(low), new DateLiteralExpr(high)); }
	
	@SuppressWarnings("unchecked")
	public LogicExpr incRange(Field term, Comparable<?> low, Comparable<?> high) { assert(term.getDeclaringClass() == clazz); return new IncRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr incRange(Field term, int low, int high) { assert(term.getDeclaringClass() == clazz); return new IncRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr incRange(Field term, long low, long high) { assert(term.getDeclaringClass() == clazz); return new IncRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr incRange(Field term, float low, float high) { assert(term.getDeclaringClass() == clazz); return new IncRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr incRange(Field term, double low, double high) { assert(term.getDeclaringClass() == clazz); return new IncRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr incRange(Field term, String low, String high) { assert(term.getDeclaringClass() == clazz); return new IncRangeExpr(new FieldExpr(term.getName()), new StringLiteralExpr(low), new StringLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr incRange(Field term, Date low, Date high) { assert(term.getDeclaringClass() == clazz); return new IncRangeExpr(new FieldExpr(term.getName()), new DateLiteralExpr(low), new DateLiteralExpr(high)); }
 
	@SuppressWarnings("unchecked")
	public LogicExpr excRange(Field term, Comparable<?> low, Comparable<?> high) { assert(term.getDeclaringClass() == clazz); return new ExcRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr excRange(Field term, int low, int high) { assert(term.getDeclaringClass() == clazz); return new ExcRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr excRange(Field term, long low, long high) { assert(term.getDeclaringClass() == clazz); return new ExcRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr excRange(Field term, float low, float high) { assert(term.getDeclaringClass() == clazz); return new ExcRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr excRange(Field term, double low, double high) { assert(term.getDeclaringClass() == clazz); return new ExcRangeExpr(new FieldExpr(term.getName()), new ScalarLiteralExpr(low), new ScalarLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr excRange(Field term, String low, String high) { assert(term.getDeclaringClass() == clazz); return new ExcRangeExpr(new FieldExpr(term.getName()), new StringLiteralExpr(low), new StringLiteralExpr(high)); }
	@SuppressWarnings("unchecked")
	public LogicExpr excRange(Field term, Date low, Date high) { assert(term.getDeclaringClass() == clazz); return new ExcRangeExpr(new FieldExpr(term.getName()), new DateLiteralExpr(low), new DateLiteralExpr(high)); }
	
	// Logical Ops
	
	public LogicExpr and(LogicExpr lhs, LogicExpr rhs) { return new AndExpr(lhs, rhs); }
	public LogicExpr or(LogicExpr lhs, LogicExpr rhs) { return new OrExpr(lhs, rhs); }
	public LogicExpr xor(LogicExpr lhs, LogicExpr rhs) { return new XorExpr(lhs, rhs); }
	public LogicExpr not(LogicExpr expr) { return new NotExpr(expr); }
	
	
}
