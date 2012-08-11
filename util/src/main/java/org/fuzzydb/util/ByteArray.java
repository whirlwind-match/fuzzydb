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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * Maintain an efficient byte[] array
 * NOTE: Not thread safe.  Just use it from one thread!
 * @author Neale
 */
public class ByteArray implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;

	private byte[] bytes;
	
	/**
	 * How much of the currently allocated array we've used.
	 */
	private int count;
	
	public ByteArray() {
		bytes = null;	// only for use prior to readObject() 
	}
	
	public ByteArray(int initialSize) {
		bytes = new byte[initialSize];
	}
	
	
	final public void putBoolean( int index, boolean bool ) {
		assertCapacity( index );
		bytes[index] = bool ? (byte)1 : (byte)0;
	}

	final public void putByte( int index, byte b ) {
		assertCapacity( index );
		bytes[index] = b;
	}
	
	final public void putShort(int index, short s) {
		assertCapacity( index + 1);
		bytes[index + 1] = (byte) (s >>> 0);
		bytes[index + 0] = (byte) (s >>> 8);
	}

	/**
	 * Write int (lsb first)
	 */
	final public void putInt( int index, int i ) {
		assertCapacity( index + 3);
		bytes[index + 3] = (byte) (i >>> 0);
		bytes[index + 2] = (byte) (i >>> 8);
		bytes[index + 1] = (byte) (i >>> 16);
		bytes[index + 0] = (byte) (i >>> 24);
	}


	/**
	 * Write long (lsb first)
	 */
	public void putLong(int index, long l) {
		assertCapacity( index + 7);
		bytes[index + 7] = (byte) (l >>> 0);
		bytes[index + 6] = (byte) (l >>> 8);
		bytes[index + 5] = (byte) (l >>> 16);
		bytes[index + 4] = (byte) (l >>> 24);
		bytes[index + 3] = (byte) (l >>> 32);
		bytes[index + 2] = (byte) (l >>> 40);
		bytes[index + 1] = (byte) (l >>> 48);
		bytes[index + 0] = (byte) (l >>> 56);
	}

	final public void putFloat( int index, float f ) {
		assertCapacity( index + 3);
		int i = Float.floatToIntBits(f);
		bytes[index + 3] = (byte) (i >>> 0);
		bytes[index + 2] = (byte) (i >>> 8);
		bytes[index + 1] = (byte) (i >>> 16);
		bytes[index + 0] = (byte) (i >>> 24);
	}
	
	/**
	 * OOH. Cringe.  This is very inefficient, and is actually putArray( index, bytes[] )
	 */
	final public void join(ByteArray bytes) {
		byte[] a = bytes.getArray();

	    for (byte b: a) {
	    	int i = getIndexForWrite(1);
	    	putByte(i, b);
	    }
	}
	
	/**
	 * Reserve enough room to be able to add length to the buffer.
	 * @return index where calling function is allowed to write 'length' bytes
	 */
	final public synchronized int getIndexForWrite( int length ) {
		int index = count;
		count += length;
		ensureCapacity(count);
		return index;
	}

	
	/**
	 * Development-time check to ensure developer has allocated capacity.
	 */
	private void assertCapacity(int lastIndexToWrite) {
		assert( lastIndexToWrite < bytes.length );
	}

	
	private void ensureCapacity( int neededSize ) {
		if (neededSize <= bytes.length) {
			return;
		}
		// not enough capacity for that index, so ensure enough for index, plus 50%
		byte[] newBytes = new byte[neededSize];
		System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
		bytes = newBytes;
	}


	final public byte[] getArray() {
		return bytes;
	}


	final public boolean getBoolean(int index) {
		return bytes[index] != 0;
	}

	/**
	 * Get the byte at index
	 */
	final public byte getByte(int index) {
		return bytes[index];
	}

	final public short getShort(int index) {
		return (short) (((bytes[index + 1] & 0xFF) << 0) + 
		((bytes[index + 0] & 0xFF) << 8));
	}

	/**
	 * Get the int from the bytes starting at index
	 */
	final public int getInt(int index) {
		return ((bytes[index + 3] & 0xFF) << 0) +
		   ((bytes[index + 2] & 0xFF) << 8) +
		   ((bytes[index + 1] & 0xFF) << 16) +
		   ((bytes[index + 0] & 0xFF) << 24);
	}

	/**
	 * Get the long from the bytes starting at index
	 */
	public long getLong(int index) {
		return ((bytes[index + 7] & 0xFFL) << 0) +
		   ((bytes[index + 6] & 0xFFL) << 8) +
		   ((bytes[index + 5] & 0xFFL) << 16) +
		   ((bytes[index + 4] & 0xFFL) << 24) +
		   ((bytes[index + 3] & 0xFFL) << 32) +
		   ((bytes[index + 2] & 0xFFL) << 40) +
		   ((bytes[index + 1] & 0xFFL) << 48) +
		   ((bytes[index + 0] & 0xFFL) << 56);
	}

	/**
	 * Get the float from the bytes starting at index
	 */
	final public float getFloat( int index) {
		int i = ((bytes[index + 3] & 0xFF) << 0) +
			((bytes[index + 2] & 0xFF) << 8) +
			((bytes[index + 1] & 0xFF) << 16) +
			((bytes[index + 0] & 0xFF) << 24);
		return Float.intBitsToFloat(i);
	}

	
	final public static int getInt(byte[] array, int index) {
		return ((array[index + 3] & 0xFF) << 0) +
		   ((array[index + 2] & 0xFF) << 8) +
		   ((array[index + 1] & 0xFF) << 16) +
		   ((array[index + 0] & 0xFF) << 24);
	}


	final public int size() {
		return count;
	}

	// NOTE: Not intended to be used by Serialisation API directly
	public void customWriteObject(ObjectOutputStream out) throws IOException {
		out.writeInt(count);
		out.write(bytes, 0, count); // write the valid bytes
	}

	public void customReadObject(ObjectInputStream in) throws IOException {
		count = in.readInt();
		DataInputStream din = new DataInputStream(in);
		bytes = new byte[count];
		din.readFully(bytes); // ensure we get them all in
	}
	
	
	/**
	 * Implement equals that compares the used part of the byte array
	 */
	@Override
	public boolean equals(Object o) {
		ByteArray obj = (ByteArray) o;
		if (count != obj.count) return false;
		
		// If same length then can do direct compare
		if (bytes.length == obj.bytes.length) {
			return bytes.equals(obj);
			
		}
		
		int len = (count < obj.count) ? count : obj.count;
		for (int i = 0; i < len; i++) {
			if ( bytes[i] != obj.bytes[i] ) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public ByteArray clone() throws CloneNotSupportedException {
		ByteArray clone = (ByteArray) super.clone();
		// supposedly 5x faster than bytes.clone() .. and fixed in Java 7
		clone.bytes = new byte[bytes.length];
		System.arraycopy(bytes, 0, clone.bytes, 0, bytes.length);
//		clone.bytes = bytes.clone();
		return clone;
	}
}

