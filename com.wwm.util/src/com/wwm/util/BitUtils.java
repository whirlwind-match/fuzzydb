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
package com.wwm.util;


public class BitUtils {

	/**
	 * Get the indexes of the values
	 * @param bitfield
	 * @return
	 */
	public static short[] bitFieldToIndexArray(long bitfield) {
		int entries = countBitsSet( bitfield );
		short[] vals = new short[entries];
		int currEntry = 0;
		for (short i = 0; i < Long.SIZE; i++) {
			if ((bitfield & 0x01) != 0){
				vals[currEntry++] = i;
			}
			bitfield >>>= 1;
		}
		return vals;
	}

	/**
	 * Get the indexes of the values
	 * @param bitfield
	 * @return
	 */
	public static short[] bitFieldToIndexArray(int bitfield) {
		int entries = countBitsSet( bitfield );
		short[] vals = new short[entries];
		int currEntry = 0;
		for (short i = 0; i < Long.SIZE; i++) {
			if ((bitfield & 0x01) != 0){
				vals[currEntry++] = i;
			}
			bitfield >>>= 1;
		}
		return vals;
	}

	// NOTE: Diff version for Int, as otherwise will get top bits filled on cast from int -> long
	public static int countBitsSet(int bitfield) {
		int count = 0;
		for (short i = 0; i < Integer.SIZE; i++) {
			if ((bitfield & 0x01) != 0){
				count++;
			}
			bitfield >>>= 1;
		}
		return count;
	}

	public static int countBitsSet(long bitfield) {
		int count = 0;
		for (short i = 0; i < Long.SIZE; i++) {
			if ((bitfield & 0x01) != 0){
				count++;
			}
			bitfield >>>= 1;
		}
		return count;
	}

	/**
	 * Encode a set of shorts into a bitfield as a long
	 * @param bitfield
	 * @return
	 */
	public static long indexArrayToLongBits(short[] values) {
		long bitfield = 0;
		for (int i = 0; i < values.length; i++) {
			bitfield |= 0x01 << values[i];
		}
		return bitfield;
	}

	public static int indexArrayToIntBits(short[] values) {
		int bitfield = 0;
		for (int i = 0; i < values.length; i++) {
			bitfield |= 0x01 << values[i];
		}
		return bitfield;
	}

}
