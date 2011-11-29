/******************************************************************************
 * WARNING: NEED TO SORT OUT SOURCE OF THIS.
 * OpenJDK7 is GPL2, so the file below could be a contribution to OpenJDK.
 * http://www.docjar.com/html/api/java/io/BufferedInputStream.java.html
 *****************************************************************************/
package com.wwm.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Derived from Sun's BufferedInputStream, as they suggest we do in the source for that file :O)
 *
 */
public class ReadAheadInputStream extends FilterInputStream {

	private static int defaultBufferSize = 65536;
	private int readThruThreshold;
	private Adler64 checksum = new Adler64();
	private long byteCount = 0;
	
	/**
	 * Internal buffer
	 */
	protected volatile byte buf[];

	/**
	 * Atomic updater to provide compareAndSet for buf. This is
	 * necessary because closes can be asynchronous. We use nullness
	 * of buf[] as primary indicator that this stream is closed. (The
	 * "in" field is also nulled out on close.)
	 */
	private static final AtomicReferenceFieldUpdater<ReadAheadInputStream, byte[]> bufUpdater = AtomicReferenceFieldUpdater
			.newUpdater(ReadAheadInputStream.class, byte[].class, "buf");

	/**
	 * The index one greater than the index of the last valid byte in 
	 * the buffer. 
	 * This value is always
	 * in the range <code>0</code> through <code>buf.length</code>;
	 * elements <code>buf[0]</code>  through <code>buf[count-1]
	 * </code>contain buffered input data obtained
	 * from the underlying  input stream.
	 */
	protected int count;

	/**
	 * The current position in the buffer. This is the index of the next 
	 * character to be read from the <code>buf</code> array. 
	 * <p>
	 * This value is always in the range <code>0</code>
	 * through <code>count</code>. If it is less
	 * than <code>count</code>, then  <code>buf[pos]</code>
	 * is the next byte to be supplied as input;
	 * if it is equal to <code>count</code>, then
	 * the  next <code>read</code> or <code>skip</code>
	 * operation will require more bytes to be
	 * read from the contained  input stream.
	 *
	 * @see     java.io.BufferedInputStream#buf
	 */
	protected int pos;



	/**
	 * Check to make sure that underlying input stream has not been
	 * nulled out due to close; if not return it;
	 */
	private InputStream getInIfOpen() throws IOException {
		InputStream input = in;
		if (input == null)
			throw new IOException("Stream closed");
		return input;
	}

	/**
	 * Check to make sure that buffer has not been nulled out due to
	 * close; if not return it;
	 */
	private byte[] getBufIfOpen() throws IOException {
		byte[] buffer = buf;
		if (buffer == null)
			throw new IOException("Stream closed");
		return buffer;
	}

	/**
	 * Creates a <code>BufferedInputStream</code>
	 * and saves its  argument, the input stream
	 * <code>in</code>, for later use. An internal
	 * buffer array is created and  stored in <code>buf</code>.
	 *
	 * @param   in   the underlying input stream.
	 */
	public ReadAheadInputStream(InputStream in) {
		this(in, defaultBufferSize);
	}

	/**
	 * Creates a <code>BufferedInputStream</code>
	 * with the specified buffer size,
	 * and saves its  argument, the input stream
	 * <code>in</code>, for later use.  An internal
	 * buffer array of length  <code>size</code>
	 * is created and stored in <code>buf</code>.
	 *
	 * @param   in     the underlying input stream.
	 * @param   size   the buffer size.
	 * @exception IllegalArgumentException if size <= 0.
	 */
	public ReadAheadInputStream(InputStream in, int size) {
		super(in);
		if (size <= 0) {
			throw new IllegalArgumentException("Buffer size <= 0");
		}
		buf = new byte[size];
		readThruThreshold = size / 2;
	}

	/**
	 * Entirely fill the buffer with data (or whatever the read call will give us)
	 */
	private void fill() throws IOException {
		// preconditions
		assert( pos == count ); // Should only have got here when we've exhausted the buffer
		
		byte[] buffer = getBufIfOpen();
		
		// Reset our position
		pos = 0;
		
		// Attempt to read whole buffer
		count = getInIfOpen().read(buffer, 0, buffer.length);
	}

	/**
	 * See
	 * the general contract of the <code>read</code>
	 * method of <code>InputStream</code>.
	 *
	 * @return     the next byte of data, or <code>-1</code> if the end of the
	 *             stream is reached.
	 * @exception  IOException  if an I/O error occurs.
	 * @see        java.io.FilterInputStream#in
	 */
	@Override
	public synchronized int read() throws IOException {
		if (pos >= count) {
			fill();
			if (pos >= count)
				return -1;
		}
		int rval = getBufIfOpen()[pos++] & 0xff;
		checksum.update(rval);
		byteCount += 1;
		return rval;
	}

	/**
	 * Read as much as is wanted (len) or available (count-pos) from our buffer into
	 * the requested array, b.
	 */
	private int readFromBuffer(byte[] b, int off, int len) throws IOException {
		// preconditions
		assert( count > pos ); // Shouldn't be called if nowt available
		
		int avail = count - pos;

		// Number to read = min( avail, len )
		int cnt = (avail < len) ? avail : len;
		System.arraycopy(getBufIfOpen(), pos, b, off, cnt); // Copy from buf[pos] to b[off] for cnt bytes
		pos += cnt;
		
		//postconditions
		assert (pos <= count );
		return cnt;
	}

	/**
	 * Reads bytes from this byte-input stream into the specified byte array,
	 * starting at the given offset.
	 *
	 * <p> This method implements the general contract of the corresponding
	 * <code>{@link InputStream#read(byte[], int, int) read}</code> method of
	 * the <code>{@link InputStream}</code> class.  As an additional
	 * convenience, it attempts to read as many bytes as possible by repeatedly
	 * invoking the <code>read</code> method of the underlying stream.  This
	 * iterated <code>read</code> continues until one of the following
	 * conditions becomes true: <ul>
	 *
	 *   <li> The specified number of bytes have been read,
	 *
	 *   <li> The <code>read</code> method of the underlying stream returns
	 *   <code>-1</code>, indicating end-of-file, or
	 *
	 *   <li> The <code>available</code> method of the underlying stream
	 *   returns zero, indicating that further input requests would block.
	 *
	 * </ul> If the first <code>read</code> on the underlying stream returns
	 * <code>-1</code> to indicate end-of-file then this method returns
	 * <code>-1</code>.  Otherwise this method returns the number of bytes
	 * actually read.
	 *
	 * <p> Subclasses of this class are encouraged, but not required, to
	 * attempt to read as many bytes as possible in the same fashion.
	 *
	 * @param      b     destination buffer.
	 * @param      off   offset at which to start storing bytes.
	 * @param      len   maximum number of bytes to read.
	 * @return     the number of bytes read, or <code>-1</code> if the end of
	 *             the stream has been reached.
	 * @exception  IOException  if an I/O error occurs.
	 */
	@Override
	public synchronized int read(byte b[], int off, int len) throws IOException {
		getBufIfOpen(); // Check for closed stream
		if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		// If buffer isn't empty then return what's required from what we have
		if ( pos < count ) {
			int rval = readFromBuffer( b, off, len );
			if (rval > 0 ) {
				checksum.update(b, off, rval);
				byteCount += rval;
			}
			return rval;
		}
		
		// Buffer is empty
		// If len requested is >= than readThruThreshold, then passThru read
		if ( len >= readThruThreshold ) {
			int rval = getInIfOpen().read( b, off, len );
			if (rval > 0 ) {
				checksum.update(b, off, rval);
				byteCount += rval;
			}
			return rval;			
		}
		
		// Otherwise, fill buffer, and then read from buffer
		if (pos >= count) {
			fill();
			if (pos >= count)
				return -1;
		}

		int rval = readFromBuffer( b, off, len );
		if (rval > 0 ) {
			checksum.update(b, off, rval);
			byteCount += rval;
		}
		return rval;
	}

	
	/**
	 * See the general contract of the <code>skip</code>
	 * method of <code>InputStream</code>.
	 *
	 * @param      n   the number of bytes to be skipped.
	 * @return     the actual number of bytes skipped.
	 * @exception  IOException  if an I/O error occurs.
	 */
	@Override
	public synchronized long skip(long n) {
		throw(new RuntimeException("ReadAheadInputStream.skip() not implemented due to checksum calculations"));
//		getBufIfOpen(); // Check for closed stream
//		if (n <= 0) {
//			return 0;
//		}
//		long avail = count - pos;
//
//		// If nothing in buffer, skip on stream 
//		if (avail <= 0) {
//				return getInIfOpen().skip(n);
//		}
//
//		// Otherwise skip up to remainder of buffer length of what's required on buffer
//		long skipped = (avail < n) ? avail : n;
//		pos += skipped;
//		return skipped;
	}

	
	/**
	 * Returns the number of bytes that can be read from this input 
	 * stream without blocking. 
	 * <p>
	 * The <code>available</code> method of 
	 * <code>BufferedInputStream</code> returns the sum of the number
	 * of bytes remaining to be read in the buffer 
	 * (<code>count&nbsp;- pos</code>) 
	 * and the result of calling the <code>available</code> method of the 
	 * underlying input stream. 
	 *
	 * @return     the number of bytes that can be read from this input
	 *             stream without blocking.
	 * @exception  IOException  if an I/O error occurs.
	 * @see        java.io.FilterInputStream#in
	 */
	@Override
	public synchronized int available() throws IOException {
		return getInIfOpen().available() + (count - pos);
	}



	/**
	 * Closes this input stream and releases any system resources 
	 * associated with the stream. 
	 *
	 * @exception  IOException  if an I/O error occurs.
	 */
	@Override
	public void close() throws IOException {
		byte[] buffer;
		while ((buffer = buf) != null) {
			if (bufUpdater.compareAndSet(this, buffer, null)) {
				InputStream input = in;
				in = null;
				if (input != null)
					input.close();
				return;
			}
			// Else retry in case a new buf was CASed in fill()
		}
	}

	public long getChecksumValue() {
		return checksum.getValue();
	}

	public long getByteCount() {
		return byteCount;
	}
}
