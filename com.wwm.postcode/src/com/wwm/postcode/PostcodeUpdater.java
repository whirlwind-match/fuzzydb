/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.postcode;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.wwm.db.core.Settings;
import com.wwm.util.CsvReader;
import com.wwm.util.StringUtils;
import com.wwm.util.CsvReader.GarbageLineException;
import com.wwm.util.CsvReader.UnsupportedTypeException;

public class PostcodeUpdater {

	private final String oldPostcodeColName = "OldPostcode";
	private final String newPostcodeColName = "NewPostcode";
	
	private final HashMap<String, String> postcodeMap = new HashMap<String, String>();
	
	public PostcodeUpdater() {
		String root = Settings.getInstance().getPostcodeRoot();
		addFileData(root + File.separatorChar + "16Sep_PU43_no_quote.txt");
	}
	
	/**Convert an old postcode into a new one, depending on the translation files that have been provided.
	 * @param oldCode An old postcode. Can be any case and include or omit spaces.
	 * @return If a translation is available, returns the conversion. Otherwise, null is returned.
	 * The returned postcode is correctly cased and spaced.
	 */
	public String convert(String oldCode) {
		oldCode = StringUtils.stripSpaces(oldCode);
		oldCode = oldCode.toUpperCase();
		return postcodeMap.get(oldCode);
	}
	
	private void addFileData(String in) {
		try {
			CsvReader reader = null;
			try {
				reader = new CsvReader(in, true, false);
			} catch (EOFException e) {
				System.out.println(in + ": File is empty!");
				return;
			} catch (FileNotFoundException e) {
				System.out.println(in + ": File not found! You might be missing some translation information.");
				return;
			} catch (IOException e) {
				System.out.println(in + ": IOException " + e.getMessage());
				return;
			}

			
			reader.setColumn(oldPostcodeColName, String.class, 0);
			reader.setColumn(newPostcodeColName, String.class, 1);
			
			int codesRead = 0;
			int linesIgnored = 0;
			try {
				System.out.println("Reading data from " + in + "...");
				for (;;) {
					try {
						Map<String, Object> data = reader.readLine();
						String oldCode = (String)data.get(oldPostcodeColName);
						String newCode = (String)data.get(newPostcodeColName);
						
						oldCode = StringUtils.stripSpaces(oldCode.toUpperCase());
						newCode = StringUtils.stripSpaces(newCode.toUpperCase());
						
						postcodeMap.put(oldCode, newCode);
						codesRead++;
					} catch (GarbageLineException e) {
						linesIgnored++;
					}
				}
			} catch (EOFException e) {
				System.out.println("Imported " + codesRead + " postcode translations OK, ignored " + linesIgnored + " incomplete lines.");
			} catch (IOException e) {
				System.out.println(in + ": IOException " + e.getMessage());
				return;
			}
			
		} catch (UnsupportedTypeException e) {
			System.out.println("Internal error. " + e.getMessage());
			return;
		}
	}
}
