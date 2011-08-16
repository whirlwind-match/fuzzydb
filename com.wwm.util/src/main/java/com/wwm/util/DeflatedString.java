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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * A compressed String object. The static methods have best performance.<BR>
 * <BR>
 * Typical savings for 5000 short strings, there is upside if strings are longer:<br>
 * <br>
 * Normal (String)<br>
 * 4MB storage<br>
 *<br>
 * Deflated (DeflatedString) - toString() works<br>
 * 2.4MB	60%<br>
 *<br>
 * Encoded (byte[]) - toString and runtime types unavailable<br>
 * 1.8MB	45%<br>
 * <br>
 * @author ac
 *
 */
public class DeflatedString {
	private static final int noCompressionLength = 50;	// Do not attempt to compress strings shorter than this, performance aid
	
	private static final byte UTF8_CODED = 0;
	private static final byte DEFLATE_CODED = 1;
	private static final byte EMPTY_CODED = 2;
	private static final String emptyString = "";
	
	byte[] encodedData;
	
	public DeflatedString() {
		super();
		encodedData = null;
	}

	/**Construct a new DeflatedString with the specified value
	 * @param value The String to encode
	 */
	public DeflatedString(String value) {
		super();
		set(value);
	}
	
	/** Encode the specified string to the Deflated string format.
	 * @param value The String to encode 
	 * @return A byte array coded with DeflatedString's internal format
	 */
	public static byte[] encode(String value) {
		byte[] rval;
		if (value==null) {
			return null;
		}
		if (value.length()==0) {
			rval = new byte[1];
			rval[0] = EMPTY_CODED;
			return rval;
		}
		byte[] utf8;
		try {
			utf8 = value.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			// Can't normally happen
			throw new RuntimeException("Fatal error in DeflatedString, UTF8 coding not supported");
		}
		if (value.length() > noCompressionLength) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(DEFLATE_CODED);
			DeflaterOutputStream d = new DeflaterOutputStream(baos);
			try {
				d.write(utf8);
				d.finish();
				d.close();
			} catch (IOException e) {
				// Can't normally happen
				throw new RuntimeException("Fatal error in DeflatedString, streams not working");
			}
			byte[] encoded = baos.toByteArray();
			if (encoded.length < (utf8.length+1)) {
				rval = encoded;
				return rval;
			}
		}
		rval = new byte[utf8.length+1];
		rval[0] = UTF8_CODED;
		System.arraycopy(utf8, 0, rval, 1, utf8.length);
		return rval;
	}
	
	/**
	 * Get the raw encoded data
	 * @return A byte array coded with DeflatedString's internal format
	 */
	public byte[] getCoded() {
		return encodedData;
	}
	
	/**
	 * Decode the byte array to a String. The byte array must be in DeflatedString format, or else an exception is thrown.
	 * @param data The coded data to decode
	 * @return A String
	 * @throws DataFormatException The data is not in the correct format.
	 * @see #getCoded()
	 */
	public static String decode(byte[] data) throws DataFormatException {
		try {
			if (data == null) {
				return null;
			}
			if (data.length < 1) {
				throw new DataFormatException();
			}
			if (data[0] == EMPTY_CODED) {
				return emptyString;
			}
			
			if (data.length < 2) {
				throw new DataFormatException();
			}

			if (data[0] == UTF8_CODED) {
				return new String(data, 1, data.length-1, "UTF8");
			}
			
			if (data[0] == DEFLATE_CODED) {
				ByteArrayInputStream bais = new ByteArrayInputStream(data, 1, data.length-1);
				InflaterInputStream iis = new InflaterInputStream(bais);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] tmp = new byte[1024];
				int bytesMoved;
				try {	
					do {
						bytesMoved = iis.read(tmp);
						if (bytesMoved>0) baos.write(tmp, 0, bytesMoved);
					} while (bytesMoved > -1);
				
					return new String(baos.toByteArray(), "UTF8");
				} catch (IOException e) {
					throw new DataFormatException("Problem with compressed data");
				}
			}
			throw new DataFormatException("Unknown coding type");
		} catch (UnsupportedEncodingException e) {
			// Can't normally happen
			throw new RuntimeException("Fatal error in DeflatedString, UTF8 coding not supported");
		}
	}
	
	/**
	 * Determine if real compression was used to encode the string.
	 * @return true if the internal format is deflated, false if it is UTF8, null, or empty coded
	 */
	public boolean isCompressed() {
		return (encodedData != null && encodedData[0]==DEFLATE_CODED);
	}
	
	/**
	 * Sets the value of this object. The supplied String is encoded and stored, the previous value is overwritten.
	 * @param value The String to encode
	 */
	public void set(String value) {
		encodedData = encode(value);
	}
	
	/**
	 * Decodes the String value from the compressed internal buffer.
	 * @return The decoded String
	 * @throws DataFormatException The data is corrupt
	 */
	public String get() throws DataFormatException {
		return decode(encodedData);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			return get();

		} catch (DataFormatException e) {
			return e.toString();
		}
	}
}
