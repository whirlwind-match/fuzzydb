/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.util;

import java.io.Serializable;

/**
 * A fixed length BitSet for the most part functionally equivalent to Sun's
 * BitSet.
 * 
 * This has been clean room coded, as Sun version does not allow getWord()
 * and setWord() to be implemented by sub-classing.
 */
public class BitSet64 implements Cloneable, Serializable {

	private long bits;

	private static final long serialVersionUID = 7997698588986878753L;

	private static final int NUM_BITS = 64;

	public BitSet64() {
		bits = 0;
	}


	public BitSet64(long bits) {
		this.bits = bits;
	}


	public boolean isEmpty() {
		return (bits == 0);
	}


	public int cardinality() {
		return Long.bitCount(bits);
	}


	@Override
	public int hashCode() {
		long hash = 63 + bits + cardinality();
		return (int)(hash ^ (hash >> 32));
	}

	public int size() {
		return NUM_BITS;
	}

	@Override
	public boolean equals(Object rhs) {
		if (!(rhs instanceof BitSet64)) {
			return false;
		}

		if (this == rhs) {
			return true;
		}

		BitSet64 set = (BitSet64) rhs;
		if (bits == set.bits){
			return true;
		}
		return false;
	}

	@Override
	public Object clone() {
		BitSet64 clone = new BitSet64(bits);
		return clone;
	}


	@Override
	public String toString() {
		return String.valueOf(bits);
	}


	public long getWord() {
		return bits;
	}

	public void setWord(long word) {
		this.bits = word;
	}


	public boolean get(int index) {
		return (bits & (1L << index)) != 0L;
	}

	
	public void set(int index) {
		bits |= (1L << index);
	}


	public void and(BitSet64 otherBits) {
		bits &= otherBits.bits;
	}


	public void or(BitSet64 otherBits) {
		bits |= otherBits.bits;
		
	}


	public int length() {
		return NUM_BITS - Long.numberOfLeadingZeros(bits);
	}
}
