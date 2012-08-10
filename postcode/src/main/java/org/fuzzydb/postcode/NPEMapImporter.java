/******************************************************************************
 * Copyright (c) 2005-2009 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.postcode;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.wwm.util.CsvReader;
import com.wwm.util.FileUtils;
import com.wwm.util.CsvReader.GarbageLineException;
import com.wwm.util.CsvReader.NoSuchColumnException;
import com.wwm.util.CsvReader.UnsupportedTypeException;

/**
 * This application converts [postcode root]\npemap.org.uk.outward-part.csv and outputs to [postcode root]\jibble
 */
public class NPEMapImporter {

	private static final String postcodeColName = "postcode";
	private static final String latitudeColName = "latitude";
	private static final String longitudeColName = "longitude";
	
	public static final String dataFile = "postcodes-short.dat";
	private static final String sourceFile = "npemap.org.uk.outward-part.csv";

	public static void main(String[] args) {
		NPEMapImporter j = new NPEMapImporter();
//		String root = Settings.getInstance().getPostcodeRoot();
//		j.convert(root + File.separatorChar + jibbleSourceFile, root + File.separatorChar + jibbleDataFile);
		j.convert("data" + File.separatorChar + sourceFile, "data" + File.separatorChar + dataFile);
	}

	public NPEMapImporter() {
		super();
	}


	private void convert(String in, String out) {
		TreeMap<String, PostcodeResult> map = new TreeMap<String, PostcodeResult>();
		// Read file in
		try {
			CsvReader reader = null;
			try {
				reader = new CsvReader(in, false, false);
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
				reader.setColumn(latitudeColName, Float.class);
				reader.setColumn(longitudeColName, Float.class);
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
						PostcodeResult result = new PostcodeResult("", "", (Float)data.get(latitudeColName), (Float)data.get(longitudeColName));
						map.put(((String)data.get(postcodeColName)).toUpperCase(), result);
						codesRead++;
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
		
		try {
			FileUtils.writeObjectToGZip(out, map);
		} catch (IOException e) {
			return;
		}
		System.out.println("Conversion complete.");
	}

}
