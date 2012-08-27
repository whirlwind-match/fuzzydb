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

import java.util.Arrays;

public class ComparableByteArray implements Comparable<ComparableByteArray> {

	private static final long serialVersionUID = 2748038788324114274L;

	private byte[] array;
	
	public ComparableByteArray( byte[] array ) {
		this.array = array;
	}

	public byte[] getValue() {
		return array;
	}
	
	/**
	 * Compare sortable arrays, with null contents being a valid comparison
	 * (means that we can do "= null" lookups).
	 * @param o
	 * @return
	 */
	public int compareTo(ComparableByteArray o) {
		
		if ( ComparableByteArray.class.isInstance( o ) ) {
			byte[] rhs = (o ).getValue();
			
			// Handle null entry
			if ( rhs != null && array != null ) {
				if (rhs.length == array.length) {
					for (int i = 0; i < array.length; i++) {
						if (rhs[i] < array[i]) {
							return 1;
						} else if (rhs[i] > array[i]) {
							return -1;
						}
					}
					return 0;
				} else {
				    return array.length - rhs.length;
                }
			}
			if ( rhs != null && array == null ) return 1;
			if ( rhs == null && array != null ) return -1;
			
			assert( rhs == null && array == null );
			return 0; // both null
		}
		
		throw new ClassCastException();	//	Only comparable with itself.
	}

	
	@Override
	public boolean equals(Object obj) {
		if ( byte[].class.isInstance( obj ) ) {
			byte[] rhs = (byte[])obj;
			return Arrays.equals(rhs, array);
		}

		if ( ComparableByteArray.class.isInstance( obj ) ) {
			byte[] rhs = ((ComparableByteArray)obj).getValue();
			return Arrays.equals(rhs, array);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(array);
	}

}
