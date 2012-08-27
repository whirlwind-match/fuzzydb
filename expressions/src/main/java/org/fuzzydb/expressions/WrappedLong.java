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


public class WrappedLong implements WrappedValue {

	private static final long serialVersionUID = 3258126968678266935L;
	private long value;
	public WrappedLong(long value) {
		super();
		this.value = value;
	}
	
	
	public WrappedValue add(WrappedValue rhs) {
		if (rhs instanceof WrappedInteger) {
			return new WrappedLong(this.value + ((WrappedInteger)rhs).getValue());
		}
		if (rhs instanceof WrappedLong) {
			return new WrappedLong(this.value + ((WrappedLong)rhs).getValue());
		}
		if (rhs instanceof WrappedFloat) {
			return new WrappedFloat(this.value + ((WrappedFloat)rhs).getValue());
		}
		if (rhs instanceof WrappedDouble) {
			return new WrappedDouble(this.value + ((WrappedDouble)rhs).getValue());
		}
		throw new UnsupportedOperationException("Adding to Long: " + rhs.getComparable().getClass().toString());
	}

	
	public long getValue() {
		return value;
	}

	
	public WrappedValue sub(WrappedValue rhs) {
		if (rhs instanceof WrappedInteger) {
			return new WrappedLong(this.value - ((WrappedInteger)rhs).getValue());
		}
		if (rhs instanceof WrappedLong) {
			return new WrappedLong(this.value - ((WrappedLong)rhs).getValue());
		}
		if (rhs instanceof WrappedFloat) {
			return new WrappedFloat(this.value - ((WrappedFloat)rhs).getValue());
		}
		if (rhs instanceof WrappedDouble) {
			return new WrappedDouble(this.value - ((WrappedDouble)rhs).getValue());
		}
		throw new UnsupportedOperationException("Subtracting from Long: " + rhs.getComparable().getClass().toString());
	}

	
	public WrappedValue mult(WrappedValue rhs) {
		if (rhs instanceof WrappedInteger) {
			return new WrappedLong(this.value * ((WrappedInteger)rhs).getValue());
		}
		if (rhs instanceof WrappedLong) {
			return new WrappedLong(this.value * ((WrappedLong)rhs).getValue());
		}
		if (rhs instanceof WrappedFloat) {
			return new WrappedFloat(this.value * ((WrappedFloat)rhs).getValue());
		}
		if (rhs instanceof WrappedDouble) {
			return new WrappedDouble(this.value * ((WrappedDouble)rhs).getValue());
		}
		throw new UnsupportedOperationException("Multiplying by Long: " + rhs.getComparable().getClass().toString());
	}

	
	public WrappedValue div(WrappedValue rhs) {
		if (rhs instanceof WrappedInteger) {
			return new WrappedLong(this.value / ((WrappedInteger)rhs).getValue());
		}
		if (rhs instanceof WrappedLong) {
			return new WrappedLong(this.value / ((WrappedLong)rhs).getValue());
		}
		if (rhs instanceof WrappedFloat) {
			return new WrappedFloat(this.value / ((WrappedFloat)rhs).getValue());
		}
		if (rhs instanceof WrappedDouble) {
			return new WrappedDouble(this.value / ((WrappedDouble)rhs).getValue());
		}
		throw new UnsupportedOperationException("Dividing Long by: " + rhs.getComparable().getClass().toString());
	}

	
	public int compareTo(WrappedValue rhs) {
		long rval;
		double rvald;
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
			rvald = ((WrappedFloat)rhs).getValue();
			if (rvald == this.value) return 0;
			return rvald > this.value ? -1 : 1;
		}
		if (rhs instanceof WrappedDouble) {
			rvald = ((WrappedDouble)rhs).getValue();
			if (rvald == this.value) return 0;
			return rvald > this.value ? -1 : 1;
		}
		return -1;
	}
	
	
	@Override
	public String toString() {
		return Long.toString(value);
	}


	public Comparable getComparable() {
		return value;
	}

}
