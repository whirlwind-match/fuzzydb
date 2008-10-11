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

public class WrappedDouble implements WrappedValue {

	private static final long serialVersionUID = 3761972657335054897L;
	private double value;
	public WrappedDouble(double value) {
		super();
		this.value = value;
	}
	
	public WrappedValue add(WrappedValue rhs) {
		if (rhs instanceof WrappedInteger) {
			return new WrappedDouble(this.value + ((WrappedInteger)rhs).getValue());
		}
		if (rhs instanceof WrappedLong) {
			return new WrappedDouble(this.value + ((WrappedLong)rhs).getValue());
		}
		if (rhs instanceof WrappedFloat) {
			return new WrappedDouble(this.value + ((WrappedFloat)rhs).getValue());
		}
		if (rhs instanceof WrappedDouble) {
			return new WrappedDouble(this.value + ((WrappedDouble)rhs).getValue());
		}
		assert(false);
		return null;
	}

	public double getValue() {
		return value;
	}

	public WrappedValue sub(WrappedValue rhs) {
		if (rhs instanceof WrappedInteger) {
			return new WrappedDouble(this.value - ((WrappedInteger)rhs).getValue());
		}
		if (rhs instanceof WrappedLong) {
			return new WrappedDouble(this.value - ((WrappedLong)rhs).getValue());
		}
		if (rhs instanceof WrappedFloat) {
			return new WrappedDouble(this.value - ((WrappedFloat)rhs).getValue());
		}
		if (rhs instanceof WrappedDouble) {
			return new WrappedDouble(this.value - ((WrappedDouble)rhs).getValue());
		}
		assert(false);
		return null;
	}

	public WrappedValue mult(WrappedValue rhs) {
		if (rhs instanceof WrappedInteger) {
			return new WrappedDouble(this.value * ((WrappedInteger)rhs).getValue());
		}
		if (rhs instanceof WrappedLong) {
			return new WrappedDouble(this.value * ((WrappedLong)rhs).getValue());
		}
		if (rhs instanceof WrappedFloat) {
			return new WrappedDouble(this.value * ((WrappedFloat)rhs).getValue());
		}
		if (rhs instanceof WrappedDouble) {
			return new WrappedDouble(this.value * ((WrappedDouble)rhs).getValue());
		}
		assert(false);
		return null;
	}

	public WrappedValue div(WrappedValue rhs) {
		if (rhs instanceof WrappedInteger) {
			return new WrappedDouble(this.value / ((WrappedInteger)rhs).getValue());
		}
		if (rhs instanceof WrappedLong) {
			return new WrappedDouble(this.value / ((WrappedLong)rhs).getValue());
		}
		if (rhs instanceof WrappedFloat) {
			return new WrappedDouble(this.value / ((WrappedFloat)rhs).getValue());
		}
		if (rhs instanceof WrappedDouble) {
			return new WrappedDouble(this.value / ((WrappedDouble)rhs).getValue());
		}
		assert(false);
		return null;
	}

	public int compareTo(WrappedValue rhs) {
		double rval;
		if (rhs instanceof WrappedInteger) {
			rval = ((WrappedInteger)rhs).getValue();
			if (rval == this.value) return 0;
			return rval > this.value ? -1 : 1;
		}
		if (rhs instanceof WrappedLong) {
			rval = ((WrappedLong)rhs).getValue();
			if (rval == this.value) return 0;
			return rval > this.value ? -1 : 1;
		}
		if (rhs instanceof WrappedFloat) {
			rval = ((WrappedFloat)rhs).getValue();
			if (rval == this.value) return 0;
			return rval > this.value ? -1 : 1;
		}
		if (rhs instanceof WrappedDouble) {
			rval = ((WrappedDouble)rhs).getValue();
			if (rval == this.value) return 0;
			return rval > this.value ? -1 : 1;
		}
		return -1;
	}
	
	
	@Override
	public String toString() {
		return Double.toString(value);
	}
	
	public Comparable getComparable() {
		return value;
	}
	
}
