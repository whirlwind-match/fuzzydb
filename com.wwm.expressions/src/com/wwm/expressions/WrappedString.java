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


public class WrappedString implements WrappedValue {

	private static final long serialVersionUID = 1L;
	
	private String str;
	
	public WrappedString( String str ) {
		this.str = str;
	}

	public String getValue() {
		return str;
	}
	
	/**
	 * Compare sortable strings, with null contents being a valid comparison
	 * (means that we can do "= null" lookups).
	 * @param o
	 * @return
	 */
	public int compareTo(WrappedValue o) {
		
		if ( WrappedString.class.isInstance( o ) ) {
			String rhs = ((WrappedString)o ).getValue();
			
			// Handle null entry
			if ( rhs != null && str != null ) return str.compareTo( rhs );
			if ( rhs != null && str == null ) return 1;
			if ( rhs == null && str != null ) return -1;
			
			assert( rhs == null && str == null );
			return 0; // both null
		}
		
		return 0;
	}

	
	@Override
	public boolean equals(Object obj) {
		if ( String.class.isInstance( obj ) ) {
			return ((String)obj).equals( str );
		}

		if ( WrappedString.class.isInstance( obj ) ) {
			return ((WrappedString)obj).getValue().equals( str );
		}
		return false;
	}	
	
	@Override
	public int hashCode() {
		return str.hashCode();
	}
	
	
	@Override
	public String toString() {
		return str;
	}

	public Comparable getComparable() {
		return str;
	}

	public WrappedValue add(WrappedValue rhs) {
		throw new UnsupportedOperationException("Adding to String: " + rhs.getComparable().getClass().toString());
	}

	public WrappedValue div(WrappedValue rhs) {
		throw new UnsupportedOperationException("Dividing String by: " + rhs.getComparable().getClass().toString());
	}

	public WrappedValue mult(WrappedValue rhs) {
		throw new UnsupportedOperationException("Multiplying by String: " + rhs.getComparable().getClass().toString());
	}

	public WrappedValue sub(WrappedValue rhs) {
		throw new UnsupportedOperationException("Subtracting from String: " + rhs.getComparable().getClass().toString());
	}
}
