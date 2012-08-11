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
import org.fuzzydb.util.CsvTokeniser;

import junit.framework.Assert;
import junit.framework.TestCase;


public class TestCsvTokeniser extends TestCase {

	public void testOneString() {
		String test = "Some String";
		CsvTokeniser ct = new CsvTokeniser(test);
		String rval = ct.next();
		Assert.assertEquals("Some String", rval);
		rval = ct.next();
		Assert.assertNull(rval);
	}

	public void testTwoStrings() {
		String test = "Some String,Some Other String";
		CsvTokeniser ct = new CsvTokeniser(test);
		String rval = ct.next();
		Assert.assertEquals("Some String", rval);
		rval = ct.next();
		Assert.assertEquals("Some Other String", rval);
		rval = ct.next();
		Assert.assertNull(rval);
	}
	
	public void testThreeStrings() {
		String test = "Some String,Some Other String,A Really Long String At The End";
		CsvTokeniser ct = new CsvTokeniser(test);
		String rval = ct.next();
		Assert.assertEquals("Some String", rval);
		rval = ct.next();
		Assert.assertEquals("Some Other String", rval);
		rval = ct.next();
		Assert.assertEquals("A Really Long String At The End", rval);
		rval = ct.next();
		Assert.assertNull(rval);
	}
	
	public void testEmptyString() {
		String test = "";
		CsvTokeniser ct = new CsvTokeniser(test);
		String rval = ct.next();
		Assert.assertEquals("", rval);
		rval = ct.next();
		Assert.assertNull(rval);
	}
	
	public void testTwoEmptyFields() {
		String test = ",";
		CsvTokeniser ct = new CsvTokeniser(test);
		String rval = ct.next();
		Assert.assertEquals("", rval);
		rval = ct.next();
		Assert.assertEquals("", rval);
		rval = ct.next();
		Assert.assertNull(rval);
	}

	public void testStringAndTwoEmptyFields() {
		String test = "Hello,,,World";
		CsvTokeniser ct = new CsvTokeniser(test);
		String rval = ct.next();
		Assert.assertEquals("Hello", rval);
		rval = ct.next();
		Assert.assertEquals("", rval);
		rval = ct.next();
		Assert.assertEquals("", rval);
		rval = ct.next();
		Assert.assertEquals("World", rval);
		rval = ct.next();
		Assert.assertNull(rval);
	}

	public void testTwoEmptyFieldsAndAString() {
		String test = ",,Hello";
		CsvTokeniser ct = new CsvTokeniser(test);
		String rval = ct.next();
		Assert.assertEquals("", rval);
		rval = ct.next();
		Assert.assertEquals("", rval);
		rval = ct.next();
		Assert.assertEquals("Hello", rval);
		rval = ct.next();
		Assert.assertNull(rval);
		//rval = ct.next();
		//rval = ct.next();
		//rval = ct.next();
	}

	public void testOneQuotedStringStrippingQuotes() {
		String test = "\"Some String\"";
		CsvTokeniser ct = new CsvTokeniser(test);
		String rval = ct.next();
		Assert.assertEquals("Some String", rval);
		rval = ct.next();
		Assert.assertNull(rval);
	}

	public void testOneQuotedDifficultStringStrippingQuotes() {
		String test = "\"Some String \"\"containing\"\" quotes, and \"\"commas,\"\" which is hard\",Followed by a regular string";
		CsvTokeniser ct = new CsvTokeniser(test);
		String rval = ct.next();
		Assert.assertEquals("Some String \"containing\" quotes, and \"commas,\" which is hard", rval);
		rval = ct.next();
		Assert.assertEquals("Followed by a regular string", rval);
		rval = ct.next();
		Assert.assertNull(rval);
	}
	
}
