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

import java.util.zip.Checksum;

/**This is a home-rolled version of the Adler32 checksum algo.
 * It has been expanded for 64 bit operation using the same rules.
 * This is not guaranteed to be the same algo as some other Adler64.
 */
public final class Adler64 implements Checksum {

	private long s1;	// These are used as unsigned 32 bit
	private long s2;
	
	private final static long crcBase = 4294967291l;	// prime less than 2^32
	
	public Adler64() {
		super();
		reset();
	}
	
	public final void update(int b) {
		s1 += b;
		if (s1>=crcBase) s1 -= crcBase;	// same as mod % but with no risk of costly 64 bit math
		s2 += s1;
		if (s2>=crcBase) s2 -= crcBase;
	}

	public final void update(byte[] b, int off, int len) {
		for (int i=0; i<len; i++) {
			update(b[off+i]);
		}
	}

	public final long getValue() {
		return (s2 << 32) | s1;
	}

	public final void reset() {
		s1 = 1;
		s2 = 0;
	}
}
