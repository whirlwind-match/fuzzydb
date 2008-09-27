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

import java.util.TreeSet;

/**Checks screen names are suitable for display.
 * Uses an internal whitelist dictionary.
 * 
 * Construction is expensive, construct one per application instance and keep it.
 * 
 */
public class ScreenNameValidator extends ScreenNameValidatorDB {

	TreeSet<String> malesDB = new TreeSet<String>();
	TreeSet<String> femalesDB = new TreeSet<String>();
	boolean built = false;
	
	public ScreenNameValidator() {
		build(); 
	}

	private void build() {
		if (!built) {
			build(males, malesDB);
			build(females, femalesDB);
			built = true;
		}
	}
	
	/**Validates a name - can be male or female
	 * @param name The name to check. The name can be in any capitalisation but the
	 * caller should ensure it stores the returned string.
	 * @return corrected case String if the name is allowable for either sex, or null if illegal
	 */
	public String checkName(String name) {
		build();
		String rval = checkName(name, malesDB);
		if (rval == null) {
			rval = checkName(name, femalesDB);
		}
		return rval;
	}

	/**Validates a name against a sex
	 * @param name The name to check. The name can be in any capitalisation but the
	 * caller should ensure it stores the returned string.
	 * @param isMale true for male, false for female
	 * @return corrected case String if the name is allowable for either sex, or null if illegal
	 */
	public String checkName(String name, boolean isMale) {
		build();
		return checkName(name, isMale ? malesDB : femalesDB);
	}

	private static String checkName(String name, TreeSet<String> db) {
		if (db.contains(name.toLowerCase())) {
			return fixCase(name);
		}
		return null;
	}
	
	private static String fixCase(String name) {
		if (name.length() < 2) {
			return name.toUpperCase();
		}
		String fixed = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
		return fixed;
	}
	
	private void build(String array[], TreeSet<String> output) {
		for (int i = 0; i < array.length; i++) {
			String name = array[i];
			output.add(name);
		}
	}
}
