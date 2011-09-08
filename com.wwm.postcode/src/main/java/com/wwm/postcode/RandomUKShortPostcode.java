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

import java.io.UnsupportedEncodingException;
import java.util.Random;
import org.slf4j.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.model.attributes.NonIndexStringAttribute;
import com.wwm.model.attributes.RandomGenerator;
import com.wwm.util.MTRandom;

public class RandomUKShortPostcode implements RandomGenerator<NonIndexStringAttribute> {
	private static Logger log = LogFactory.getLogger(RandomUKShortPostcode.class);

	private Random random;

	private JibbleConvertor jibble;
	
	/**
	 * Create a new postcode generator with a default source of randomness
	 */
	public RandomUKShortPostcode() {
		random = new MTRandom();
		jibble = JibbleConvertor.getInstance();
	}
	
	@Override
	public NonIndexStringAttribute next(String attrName) {
		return new NonIndexStringAttribute(attrName, nextShortPostcode());
	}
	
	public String nextShortPostcode() {
		byte[] shortData = jibble.getPrefixData();
		if (shortData==null || shortData.length < 4) {
			throw new RuntimeException("Random Postcodes short data did not load");
		}
		int numCodes = shortData.length / 4;
		int index = 4 * random.nextInt(numCodes);
		
		String result;
		
		try {
			result = new String(shortData, index, 4, "UTF8");
		} catch (UnsupportedEncodingException e) {
			log.error("Internal error: ", e);
			throw new RuntimeException(e);
		}
		return result.trim();
	}
}
