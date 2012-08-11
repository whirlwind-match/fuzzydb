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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ChecksumBufferedOutputStream extends BufferedOutputStream {

	private Adler64 checksum = new Adler64();
	private long byteCount = 0;
	
	public ChecksumBufferedOutputStream(OutputStream out) {
		super(out);
	}
	
    public ChecksumBufferedOutputStream(OutputStream out, int size) {
		super(out, size);
    }
    
	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
		checksum.update(b, off, len);
		byteCount += len;
	}

//	@Override
//	public synchronized void write(int b) throws IOException {
//		super.write(b);
//		checksum.update(b);
//	}

//	@Override
//	public void write(byte[] b) throws IOException {
//		super.write(b);
//		checksum.update(b);
//	}

	public long getChecksumValue() {
		return checksum.getValue();
	}

	public long getByteCount() {
		return byteCount;
	}
}
