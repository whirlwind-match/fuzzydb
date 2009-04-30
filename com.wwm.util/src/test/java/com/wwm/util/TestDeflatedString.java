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


import java.util.zip.DataFormatException;

import junit.framework.Assert;

import org.junit.Test;

public class TestDeflatedString {

	@Test public void testNull() throws DataFormatException {
		DeflatedString d = new DeflatedString(null);
		Assert.assertNull(d.get());
		Assert.assertFalse(d.isCompressed());
	}
	
	@Test public void testEmpty() throws DataFormatException {
		DeflatedString d = new DeflatedString("");
		Assert.assertTrue(d.get().length()==0);
		Assert.assertFalse(d.isCompressed());
	}
	
	@Test public void testOneChar() throws DataFormatException {
		DeflatedString d = new DeflatedString("a");
		Assert.assertTrue(d.get().equals("a"));
		Assert.assertFalse(d.isCompressed());
	}
	
	@Test public void testTenChars() throws DataFormatException {
		DeflatedString d = new DeflatedString("aaaaaaaaaa");
		Assert.assertTrue(d.get().equals("aaaaaaaaaa"));
		Assert.assertFalse(d.isCompressed());
	}
	
	@Test public void testTenWords() throws DataFormatException {
		DeflatedString d = new DeflatedString("repetition repetition repetition repetition repetition repetition repetition repetition repetition repetition");
		Assert.assertTrue(d.get().equals("repetition repetition repetition repetition repetition repetition repetition repetition repetition repetition"));
		Assert.assertTrue(d.isCompressed());
	}
	
	@Test public void testSmallMessage() throws DataFormatException {
		DeflatedString d = new DeflatedString("Hello hotlips69, how are you doing? I like your profile. I'd like to get to know you.");
		Assert.assertTrue(d.get().equals("Hello hotlips69, how are you doing? I like your profile. I'd like to get to know you."));
		Assert.assertTrue(d.isCompressed());
	}
	
	@Test public void testCompressionPerformance() {
		String message = "Hello hotlips69, how are you doing? I like your profile. I'd like to get to know you.";
		final int testLoops = 500;
		
		float duration = 0f;
		for (int outerloop=0; outerloop<2; outerloop++ ) {
			long start = System.currentTimeMillis();
		
			for (int count=0; count<testLoops; count++) {
				@SuppressWarnings("unused")
				DeflatedString d = new DeflatedString(message);
			}
			long delta = System.currentTimeMillis()-start;
			duration = (float)delta / testLoops;
		}
		
		System.out.println("time: " + duration + "ms per short string compression");
	}

	@Test public void testDecompressionPerformance() throws DataFormatException {
		final int testLoops = 500;
		String message = "Hello hotlips69, how are you doing? I like your profile. I'd like to get to know you.";
		DeflatedString d = new DeflatedString(message);
		float duration = 0f;
		for (int outerloop=0; outerloop<2; outerloop++ ) {
			long start = System.currentTimeMillis();
			
			for (int count=0; count<testLoops; count++) {
				d.get();
				//Assert.assertTrue(d.get().equals("Hello hotlips69, how are you doing? I like your profile. I'd like to get to know you."));
				//Assert.assertTrue(d.isCompressed());
			}
			long delta = System.currentTimeMillis()-start;
			duration = (float)delta / testLoops;
		}
		System.out.println("time: " + duration + "ms per short string decompression");
	}
	
	@Test public void testStaticNull() throws DataFormatException {
		String message = null;
		byte[] encoded = DeflatedString.encode(message);
		String message2 = DeflatedString.decode(encoded);
		Assert.assertNull(message2);
	}

	@Test public void testStaticEmpty() throws DataFormatException {
		String message = "";
		byte[] encoded = DeflatedString.encode(message);
		String message2 = DeflatedString.decode(encoded);
		Assert.assertTrue(message.equals(message2));
	}

	@Test public void testStaticOneChar() throws DataFormatException {
		String message = "B";
		byte[] encoded = DeflatedString.encode(message);
		String message2 = DeflatedString.decode(encoded);
		Assert.assertTrue(message.equals(message2));
	}
	
	@Test public void testStaticShort() throws DataFormatException {
		String message = "Hello hotlips";
		byte[] encoded = DeflatedString.encode(message);
		String message2 = DeflatedString.decode(encoded);
		Assert.assertTrue(message.equals(message2));
	}
	
	@Test public void testStaticMedium() throws DataFormatException {
		String message = "Hello hotlips69, how are you doing? I like your profile. I'd like to get to know you.";
		byte[] encoded = DeflatedString.encode(message);
		String message2 = DeflatedString.decode(encoded);
		Assert.assertTrue(message.equals(message2));
	}

	@Test public void testStaticLong() throws DataFormatException {
		String message = "Hello hotlips69, how are you doing? I like your profile. I'd like to get to know you. I saw your porno collection and thought you'd be the right one for me. Do you have your own whips? My mother lets me keep some in my room, I have the torture rack and handcuffs too. Will the guy in your pics be joining us? I like his stiletos and fishnets. I know 43 years is a bit of an age gap but I like older women.";
		byte[] encoded = DeflatedString.encode(message);
		String message2 = DeflatedString.decode(encoded);
		Assert.assertTrue(message.equals(message2));
	}
	
}
