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


public class WrappedComparable implements WrappedValue {
	private static final long serialVersionUID = 1L;

	private Comparable comparable;
	
	public WrappedComparable( Comparable<?> comparable ) {
		this.comparable = comparable;
	}

	public Comparable getValue() {
		return comparable;
	}
	
	/**
	 * Compare sortable strings, with null contents being a valid comparison
	 * (means that we can do "= null" lookups).
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int compareTo(WrappedValue o) {
		
		if ( WrappedComparable.class.isInstance( o ) ) {
			WrappedComparable rhs = (WrappedComparable)o;
			
			// FIXME: rhs should be rhs.comparable.. which would show up if we used generics properly.
			// Handle null entry
			if ( rhs != null && comparable != null ) return comparable.compareTo( rhs );
			if ( rhs != null && comparable == null ) return 1;
			if ( rhs == null && comparable != null ) return -1;
			
			assert( rhs == null && comparable == null );
			return 0; // both null
		}
		
		return 0; // FIXME: Error, also see string
	}

	
	@Override
	public boolean equals(Object obj) {
		if ( WrappedComparable.class.isInstance( obj ) ) {
			return ((String)obj).equals( comparable );
		}

		return false;
	}	
	
	@Override
	public int hashCode() {
		return comparable.hashCode();
	}
	
	@Override
	public String toString() {
		return comparable.toString();
	}

	@SuppressWarnings("unchecked")
	public Comparable getComparable() {
		return comparable;
	}

	public WrappedValue add(WrappedValue rhs) {
		throw new UnsupportedOperationException("Adding to Comparable: " + rhs.getComparable().getClass().toString());
	}

	public WrappedValue div(WrappedValue rhs) {
		throw new UnsupportedOperationException("Dividing Comparable by: " + rhs.getComparable().getClass().toString());
	}

	public WrappedValue mult(WrappedValue rhs) {
		throw new UnsupportedOperationException("Multiplying by Comparable: " + rhs.getComparable().getClass().toString());
	}

	public WrappedValue sub(WrappedValue rhs) {
		throw new UnsupportedOperationException("Subtracting from Comparable: " + rhs.getComparable().getClass().toString());
	}
}
