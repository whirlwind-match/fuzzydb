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

import org.fuzzydb.core.LogFactory;
import org.slf4j.Logger;
import org.springframework.core.convert.converter.Converter;

import com.wwm.geo.GeoInformation;
import com.wwm.util.StringUtils;

/**
 * Converts postcodes to geographic information using available PostcodeService
 * <p>
 * Rather specific to UK which is on the list to sort out.
 */
public class PostcodeConvertor implements Converter<String, GeoInformation> {


	private static Logger log = LogFactory.getLogger(PostcodeConvertor.class);
	static private PostcodeService service;
			
	/** Converter for UK part postcodes - e.g. SE1 */
	private final JibbleConvertor jibble;

	
	public PostcodeConvertor() {
		jibble = JibbleConvertor.getInstance();
	}
	
	public static void setService(PostcodeService service) {
		PostcodeConvertor.service = service;
	}
	
	public GeoInformation lookupShort(String prefix) {
		return jibble.lookupShort(prefix);
	}

	public synchronized GeoInformation lookupFull(String postcode) {
		return convert(postcode);
	}

	/** 
	 * Look up a full postcode.<br>
	 * @param postcode A full postcode to lookup, any caps, any spaces
	 * @return A PostcodeResult if the postcode is valid, otherwise null
	 */
	@Override
	public GeoInformation convert(String postcode) {
		if (service != null && postcode.length() > 4) {
			return service.lookupFull(postcode);
		}
		log.debug("Converting short postcode for {}", postcode);
		postcode = StringUtils.stripSpaces(postcode);

		// Trim length if longer than first part
		if (postcode.length() > 4) {
			int trimmedLength = postcode.length() - 3; // strip off tail
			if (trimmedLength <2 || trimmedLength > 4) return null;
			postcode = postcode.substring(0, trimmedLength);
		}
		return jibble.lookupShort(postcode);
	}
}
