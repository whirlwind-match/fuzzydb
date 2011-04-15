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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Collates Strings and generates a MD5 Hash String from them.
 * @author jc
 *
 */
public class MD5Hasher {

	private String InputSting = ""; 

	public MD5Hasher(String input) {
		resetString(input);
	}
	
	/**
	 * Resets the source String for the MD5 Hash
	 */
	public void resetString(String input) {
		InputSting = input;
	}

    /**
     * Adds to the source String for the MD5 Hash
     */
    public void addString(String input) {
        InputSting = InputSting + input; 
    }

    /**
     * Adds to the source String for the MD5 Hash
     */
    public void addBytes(byte[] input) {
        InputSting = InputSting + input; 
    }
	
	/**
	 * Generates the MD5 Hash from the source String 
	 */
	public String getMD5() {
		MessageDigest md5hasher = null;
		try {
			md5hasher = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e); // can't continue
		}

		byte[] md5data;
		try {
			md5data = md5hasher.digest(InputSting.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);	// should never happen as all srings can be encoded UTF-8
		}
		
		StringBuilder hexStr = new StringBuilder();
		for (byte i: md5data) {
			hexStr = hexStr.append(String.format("%X",i));
		}
		return hexStr.toString();
	}	
}
