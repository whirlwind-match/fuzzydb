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


public class WrappedEnum implements WrappedValue {

	private static final long serialVersionUID = -5414348756982095501L;
	Enum value;
	
	public WrappedEnum(Enum<?> value) {
		this.value = value;
	}
	
	@SuppressWarnings("unchecked")
	public int compareTo(WrappedValue o) {
		Enum<?> rhs = ((WrappedEnum)o).getValue();
		int compareTo = value.compareTo(rhs);
		return compareTo;
	}

	public Enum getValue() {
		return value;
	}

	public WrappedValue add(WrappedValue rhs) {
		throw new UnsupportedOperationException("Adding to Enum: " + rhs.getComparable().getClass().toString());
	}

	public WrappedValue div(WrappedValue rhs) {
		throw new UnsupportedOperationException("Dividing Enum by: " + rhs.getComparable().getClass().toString());
	}

	public WrappedValue mult(WrappedValue rhs) {
		throw new UnsupportedOperationException("Multiplying by Enum: " + rhs.getComparable().getClass().toString());
	}

	public WrappedValue sub(WrappedValue rhs) {
		throw new UnsupportedOperationException("Subtracting from Enum: " + rhs.getComparable().getClass().toString());
	}

	public Comparable getComparable() {
		return value;
	}

}
