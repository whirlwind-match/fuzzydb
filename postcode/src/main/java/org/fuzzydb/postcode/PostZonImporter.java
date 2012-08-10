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
package org.fuzzydb.postcode;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.fuzzydb.core.Settings;

import com.wwm.geo.LatLongDegs;
import com.wwm.geo.OsgbGridCoord;
import com.wwm.postcode.PostcodeResult;
import com.wwm.util.CsvReader;
import com.wwm.util.FileUtils;
import com.wwm.util.StringUtils;
import com.wwm.util.CsvReader.GarbageLineException;
import com.wwm.util.CsvReader.NoSuchColumnException;
import com.wwm.util.CsvReader.UnsupportedTypeException;

/**This application assumes the file [postcode root]\PostZon.csv exists
 * It generates postzon data to [postcode root]\postzon\*
 * 
 * This needs a lot of memory, as it loads everything in one go then spits it back out.
 * At least 300MB
 * 
 * @deprecated org.fuzzydb.postcode.uk.full plug-in provides low memory footprint version.
 */
@Deprecated
public class PostZonImporter {
	private static final String postcodeColName = "Postcode";
	private static final String eastingColName = "GridEast";
	private static final String northingColName = "GridNorth";
	private static final String townColName = "AuthName";
	
	public static final String postzonDataDir = "postzon";
	private static final String postzonSourceFile = "PostZon_2005_2-PcodeComma.csv"; // "PostZon.csv";
	
	public static final int blocksize = 2;	// Number of characters of postcode to put in single file, bigger = less files, max 4, min 1
	private TreeMap<String, String> locationCache = new TreeMap<String, String>();	// used to reduce mem overhead a bit by unifying string instances - this was found to have a massive effect, reducing overhead from 800MB to 300MB
	
	public static void main(String[] args) {
		PostZonImporter c = new PostZonImporter();
		String root = Settings.getInstance().getPostcodeRoot();
		c.convert(root + File.separatorChar + postzonSourceFile, root + File.separatorChar + postzonDataDir);
	}

	public PostZonImporter() {
		super();
	}

	private void convert(String in, String out) {
		TreeMap<Integer, TreeMap<String, PostcodeResult>> map = new TreeMap<Integer, TreeMap<String, PostcodeResult>>();
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
						Integer easting = (Integer)data.get(eastingColName);
						Integer northing = (Integer)data.get(northingColName);
						String town = (String)data.get(townColName);
						int comma = town.indexOf(',');
						if (comma > -1) {
							town = town.substring(0, comma);
						}
						if (locationCache.containsKey(town)) {
							town = locationCache.get(town);
						} else {
							locationCache.put(town, town);
						}
						OsgbGridCoord grid = new OsgbGridCoord(easting*10, northing*10);	// postzon uses 100m grid refs, the convertor needs 10m points, so x10 to upgrade
						LatLongDegs ll = grid.toLatLongDegs();
						PostcodeResult result = new PostcodeResult(town, "", (float)ll.lat, (float)ll.lon);
						String postcode = StringUtils.stripSpaces((String)data.get(postcodeColName)).toUpperCase();
						if (postcode.length() < PostcodeService.minPostcodeLen) throw new GarbageLineException("Postcode too short:" + postcode);
						TreeMap<String, PostcodeResult> submap = map.get(postcode.length());
						if (submap == null) {
							submap = new TreeMap<String, PostcodeResult>();
							map.put(postcode.length(), submap);
						}
						submap.put(postcode, result);
						codesRead++;
						if (codesRead > 0 && codesRead % 100000 == 0) System.out.println("Read " + codesRead + " codes...");
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
		
		File dir = new File(out);
		dir.mkdirs();
		
		for (Entry<Integer, TreeMap<String, PostcodeResult>> subentry : map.entrySet()) {
			Set<Entry<String, PostcodeResult>> set = subentry.getValue().entrySet();
			System.out.println("Outputing " + subentry.getKey() + " digit codes...");

			Iterator<Entry<String, PostcodeResult>> iterator = set.iterator();
			Entry<String, PostcodeResult> entry = null;
			
			while (iterator.hasNext() || entry != null) {
				TreeMap<String, PostcodeResult> submap = new TreeMap<String, PostcodeResult>();
				if (entry==null) entry = iterator.next();
				// Create new file
				String filename = entry.getKey().substring(0, entry.getKey().length()-blocksize);
				//System.out.println("Creating file: " + filename);
				
				do {
					submap.put(entry.getKey(), entry.getValue());
					//System.out.println("	Putting entry: " + entry.getKey() + entry.getValue().toString());
					
					if (iterator.hasNext()) {
						entry = iterator.next();
					} else {
						entry = null;
					}
				} while (entry != null && entry.getKey().startsWith(filename));
				
				try {
					String outFile = out + File.separatorChar + filename;
					FileUtils.writeObjectToGZip(outFile, submap);
				} catch (IOException e) {
					return;
				}
			}
		}
		System.out.println("Conversion complete.");
	}
}
