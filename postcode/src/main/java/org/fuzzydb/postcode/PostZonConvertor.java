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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.TreeMap;

import org.fuzzydb.core.Settings;
import org.fuzzydb.util.StringUtils;
import org.fuzzydb.util.geo.GeoInformation;
import org.slf4j.Logger;
import java.util.zip.GZIPInputStream;

import com.wwm.postcode.PostcodeResult;

/**
 * Old version with high memory footprint
 * @deprecated Now use efficient version supplied by PostcodeService.
 */
@Deprecated
public class PostZonConvertor {
	private final Logger log;

	private static final String fullFile = Settings.getInstance().getPostcodeRoot() + File.separatorChar + PostZonImporter.postzonDataDir;

	// Future use
	//	class Cluster {
	//		final static float offsetMultiplier = 0.002f;
	//		float latOrigin;
	//		float lonOrigin;
	//		String[] town;
	//		char[] code;
	//		byte latOffset[];
	//		byte lonOffset[];
	//		byte townOffset[];
	//
	//		void test() {
	//			synchronized (code) {}
	//		}
	//	}


	//HashMap<String, byte[]> longCache = new HashMap<String, byte[]>();

	public PostZonConvertor(Logger log) {
		this.log = log;
		log.info("Starting up PostcodeConvertor");
	}


	/**Look up a full postcode.<br><br>WARNING - EXECUTING THIS FUNCTION COSTS 1p!!<br><br>
	 * @param postcode A full postcode to lookup, any caps, any spaces
	 * @return A PostcodeResult if the postcode is valid, otherwise null
	 * @throws LostDbConnection
	 */
	@SuppressWarnings("unchecked")
	public synchronized GeoInformation lookupFull(String postcode) {
		postcode = StringUtils.stripSpaces(postcode.toUpperCase());
		if (postcode.length() < PostcodeService.minPostcodeLen) {
			return null;
		}

		String filename = postcode.substring(0, postcode.length()-PostZonImporter.blocksize);
		InputStream is;

		byte[] cached = null; //longCache.get(filename);
		if (cached == null) {
			try {
				File f = new File(fullFile + File.separator + filename);
				cached = new byte[(int) f.length()];
				FileInputStream fis = new FileInputStream(f);
				fis.read(cached);
				//longCache.put(filename, cached);
			} catch (FileNotFoundException e) {
				log.warn("Failed to load Full data from " + fullFile + " for prefix: " + filename + ", bad code or missing file?");
				return null;
			} catch (IOException e) {
				log.error("Error reading full data: " + e);
				return null;
			}
		}

		is = new ByteArrayInputStream(cached);

		try {

			GZIPInputStream gzis = new GZIPInputStream(is);
			ObjectInputStream ois = new ObjectInputStream(gzis);
			TreeMap<String, PostcodeResult> subMap = (TreeMap<String, PostcodeResult>)ois.readObject();
			GeoInformation pr = subMap.get(postcode);
			return pr;
		} catch (IOException e) {
			log.error("Error reading full data: " + e);
			return null;
		} catch (ClassNotFoundException e) {
			log.error("PostcodeConvertor internal error, Error reading full data: " + e);
			return null;
		}
	}

}
