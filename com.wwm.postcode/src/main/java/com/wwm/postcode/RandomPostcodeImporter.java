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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import com.wwm.db.core.Settings;
import com.wwm.util.CsvReader;
import com.wwm.util.FileUtils;
import com.wwm.util.CsvReader.GarbageLineException;
import com.wwm.util.CsvReader.NoSuchColumnException;
import com.wwm.util.CsvReader.UnsupportedTypeException;

public class RandomPostcodeImporter {
	private static final String postcodeColName = "Postcode";
	private static final String eastingColName = "GridEast";
	private static final String northingColName = "GridNorth";
	private static final String townColName = "AuthName";
	
	public static final String randomCodesFile = "randomPostcodes";
	private static final String postzonSourceFile = "PostZon_2005_2-PcodeComma.csv"; // "PostZon.csv";
	
	public static final int blocksize = 2;	// Number of characters of postcode to put in single file, bigger = less files, max 4, min 1
	public static final int minPostcodeLen = 5;
	
	private PostcodeUpdater postcodeUpdater = new PostcodeUpdater();
	
	public static void main(String[] args) {
		RandomPostcodeImporter c = new RandomPostcodeImporter();
		String root = Settings.getInstance().getPostcodeRoot();
		c.convert(root + File.separatorChar + postzonSourceFile, root + File.separatorChar + randomCodesFile);
	}

	public RandomPostcodeImporter() {
		super();
	}

	public static String stripSpaces(String code) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < code.length(); i++) {
			char c = code.charAt(i);
			if (c != ' ') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private void convert(String in, String out) {
		ArrayList<String> postcodes = new ArrayList<String>();		
		// Read file in
		try {
			CsvReader reader = null;
			try {
				reader = new CsvReader(in, true, false);
			} catch (EOFException e) {
				System.out.println(in + ": File is empty!");
				return;
			} catch (FileNotFoundException e) {
				System.out.println(in + ": File not found!");
				return;
			} catch (IOException e) {
				System.out.println(in + ": IOException " + e.getMessage());
				return;
			}
			
			try {
				reader.setColumn(postcodeColName, String.class);
				reader.setColumn(eastingColName, Integer.class);
				reader.setColumn(northingColName, Integer.class);
				reader.setColumn(townColName, String.class);
			} catch (NoSuchColumnException e) {
				System.out.println("Missing column in " + in + ": " + e.getMessage());
				return;
			}
			int codesRead = 0;
			int linesIgnored = 0;
			try {
				System.out.println("Reading data...");
				for (;;) {
					try {
						Map<String, Object> data = reader.readLine();
						// Ignore stuff we don't have a town or location for - they'll not work for random
						if (data.get(townColName) == null || data.get(eastingColName) == null || data.get(northingColName) == null ) {
							linesIgnored++; // Things like PO boxes have a postcode but no location
							continue;
						}

						
						String postcode = stripSpaces((String)data.get(postcodeColName)).toUpperCase();
						if (postcode.length() < minPostcodeLen) throw new GarbageLineException("Postcode too short:" + postcode);
						
						String updatedPostcode = postcodeUpdater.convert(postcode);
						if (updatedPostcode != null)
						{
							postcode = stripSpaces(updatedPostcode);
						}
						
						while (postcode.length() < 7) {
							postcode = postcode + ' ';
						}
						postcodes.add(postcode);
						codesRead++;
						if (codesRead>0 && codesRead%100000 == 0) System.out.println("Read " + codesRead + " codes...");
					} catch (GarbageLineException e) {
						linesIgnored++;
					}
				}
			} catch (EOFException e) {
				System.out.println("Imported " + codesRead + " postcodes OK, ignored " + linesIgnored + " incomplete lines.");
			} catch (IOException e) {
				System.out.println(in + ": IOException " + e.getMessage());
				return;
			}
		} catch (UnsupportedTypeException e) {
			System.out.println("Internal error. " + e.getMessage());
			return;
		}
		
		System.out.println("Building array...");
		byte[] coded = new byte[postcodes.size()*7];
		int index = 0;
		for (String code : postcodes) {
			assert(code.length() == 7);
			byte[] utf8;
			try {
				utf8 = code.getBytes("UTF8");
			} catch (UnsupportedEncodingException e) {
				throw new Error("Internal error: " + e);
			}
			assert(utf8.length == 7);
			System.arraycopy(utf8, 0, coded, index, 7);
			index += 7;
		}
		
		assert(index == coded.length);
		System.out.println("Writing file...");
		
		
		try {
			FileUtils.writeObjectToGZip(out, coded);
		} catch (IOException e) {
			return;
		}
		
		System.out.println("Conversion complete.");
	}

}
