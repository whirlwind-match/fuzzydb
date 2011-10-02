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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import org.slf4j.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.db.core.Settings;
import com.wwm.util.FileUtils;
import com.wwm.util.MTRandom;

public class RandomPostcodeGenerator {
	private static Logger log = LogFactory.getLogger(RandomPostcodeGenerator.class);

	private Random random;
	byte[] fullData;
	byte[] shortData;
	
	/**
	 * Create a new postcode generator with a default source of randomness
	 */
	public RandomPostcodeGenerator() {
		random = new MTRandom();
		load();
	}
	
	/**
	 * Create a new postcode generator with the specified source of randomness
	 */
	public RandomPostcodeGenerator(Random r) {
		assert(r != null);
		random = r;
		load();
	}
	
	public String nextFullPostcode() {
		if (fullData==null || fullData.length < 7) {
			throw new RuntimeException("Random Postcodes data did not load");
		}
		int numCodes = fullData.length / 7;
		int index = 7 * random.nextInt(numCodes);
		
		String result;
		
		try {
			result = new String(fullData, index, 7, "UTF8");
		} catch (UnsupportedEncodingException e) {
			log.error("Internal error: ", e);
			throw new RuntimeException(e);
		}
		return result.trim();
	}

	public String nextShortPostcode() {
		if (shortData==null || shortData.length < 4) {
			throw new RuntimeException("Random Postcodes short data did not load");
		}
		int numCodes = shortData.length / 4;
		int index = 4 * random.nextInt(numCodes);
		
		String result;
		
		try {
			result = new String(shortData, index, 4, "UTF8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Internal error: " + e);
			throw new RuntimeException(e);
		}
		return result.trim();
	}
	
	private void load() {
		// Load Jibble data
		JibbleConvertor j = JibbleConvertor.getInstance();
		shortData = j.getPrefixData();
		
		// Load full data
		try {
			String root = Settings.getInstance().getPostcodeRoot();
			String fileName = root + File.separatorChar + RandomPostcodeImporter.randomCodesFile;
			fullData = (byte[]) FileUtils.readObjectFromGZip(fileName);
			if (fullData.length % 7 != 0) {
				log.error("Error reading from " + fileName + ": Postcodes array is not a multiple of 7!");
				throw new RuntimeException("Error reading from " + fileName + ": Postcodes array is not a multiple of 7!");
			}
		} catch (Exception e) {
			// ignore exception
			log.debug("Aborted loading random postcode data file due to: ", e.getMessage());
		}
		
	}
}
