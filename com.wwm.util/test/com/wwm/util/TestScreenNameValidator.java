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


import junit.framework.Assert;

import org.junit.Test;

public class TestScreenNameValidator {
	
	ScreenNameValidator validator = new ScreenNameValidator();

	@Test public void testSimpleNames() {
		Assert.assertTrue(validator.checkName("Mary").equals("Mary"));
		Assert.assertTrue(validator.checkName("Jane").equals("Jane"));
		Assert.assertTrue(validator.checkName("Liz").equals("Liz"));
		Assert.assertTrue(validator.checkName("Sharon").equals("Sharon"));

		Assert.assertTrue(validator.checkName("Victor").equals("Victor"));
		Assert.assertTrue(validator.checkName("Adrian").equals("Adrian"));
		Assert.assertTrue(validator.checkName("Mohammed").equals("Mohammed"));
		Assert.assertTrue(validator.checkName("Joe").equals("Joe"));
	}

	@Test public void testSimpleNamesCaseCorrection() {
		Assert.assertTrue(validator.checkName("mary").equals("Mary"));
		Assert.assertTrue(validator.checkName("JANE").equals("Jane"));
		Assert.assertTrue(validator.checkName("lIZ").equals("Liz"));
		Assert.assertTrue(validator.checkName("ShArOn").equals("Sharon"));

		Assert.assertTrue(validator.checkName("vICTOR").equals("Victor"));
		Assert.assertTrue(validator.checkName("adrian").equals("Adrian"));
		Assert.assertTrue(validator.checkName("mOhAmMeD").equals("Mohammed"));
		Assert.assertTrue(validator.checkName("JOE").equals("Joe"));
	}

	@Test public void testSimpleNamesSexed() {
		Assert.assertTrue(validator.checkName("Mary", false).equals("Mary"));
		Assert.assertTrue(validator.checkName("Jane", false).equals("Jane"));
		Assert.assertTrue(validator.checkName("Liz", false).equals("Liz"));
		Assert.assertTrue(validator.checkName("Sharon", false).equals("Sharon"));

		Assert.assertTrue(validator.checkName("Victor", true).equals("Victor"));
		Assert.assertTrue(validator.checkName("Adrian", true).equals("Adrian"));
		Assert.assertTrue(validator.checkName("Mohammed", true).equals("Mohammed"));
		Assert.assertTrue(validator.checkName("Joe", true).equals("Joe"));
	}

	@Test public void testSimpleNamesCaseCorrectionSexed() {
		Assert.assertTrue(validator.checkName("mary", false).equals("Mary"));
		Assert.assertTrue(validator.checkName("JANE", false).equals("Jane"));
		Assert.assertTrue(validator.checkName("lIZ", false).equals("Liz"));
		Assert.assertTrue(validator.checkName("ShArOn", false).equals("Sharon"));

		Assert.assertTrue(validator.checkName("vICTOR", true).equals("Victor"));
		Assert.assertTrue(validator.checkName("adrian", true).equals("Adrian"));
		Assert.assertTrue(validator.checkName("mOhAmMeD", true).equals("Mohammed"));
		Assert.assertTrue(validator.checkName("JOE", true).equals("Joe"));
	}
	
	@Test public void testBogusNames() {
		Assert.assertNull(validator.checkName("Bazza"));
		Assert.assertNull(validator.checkName("BadNurse"));
		Assert.assertNull(validator.checkName("randyrod"));
		Assert.assertNull(validator.checkName("asdfghjk"));
	}
	
	@Test public void testBogusNamesSexed() {
		Assert.assertNull(validator.checkName("Bazza", true));
		Assert.assertNull(validator.checkName("BadNurse", false));
		Assert.assertNull(validator.checkName("randyrod", true));
		Assert.assertNull(validator.checkName("asdfghjk", false));
	}
	
	@Test public void testCrossedSex() {
		Assert.assertNull(validator.checkName("Mary", true));
		Assert.assertNull(validator.checkName("Victor", false));
		Assert.assertNull(validator.checkName("WENDY", true));
		Assert.assertNull(validator.checkName("brad", false));
	}
	
}
