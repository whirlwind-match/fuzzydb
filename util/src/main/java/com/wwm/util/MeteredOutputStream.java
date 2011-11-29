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

import java.io.IOException;
import java.io.OutputStream;

/**
 * A simple filter which keeps a count of the number of bytes written to the stream.
 * The count may be retrieved or reset at any time.
 */

public class MeteredOutputStream extends OutputStream {

	private long byteCount = 0;
	OutputStream out;
	
	/**Constructs a new byte counting filter on an existing output stream
	 * @param out The stream to byte count
	 */
	public MeteredOutputStream(OutputStream out) {
		this.out = out;
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		byteCount += len;
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		if (b != null) {
			out.write(b, 0, b.length);
			byteCount += b.length;
		}
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		out.write(b);
		byteCount++;
	}

	/**
	 * Gets the number of bytes written to the stream since the filter was constructed, or the count was reset.
	 * @return The number of bytes written
	 * @see #resetByteCount()
	 */
	public long getByteCount() {
		return byteCount;
	}
	
	/**
	 * Resets the byte counter to zero.
	 */
	public void resetByteCount() {
		byteCount = 0;
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}
}
