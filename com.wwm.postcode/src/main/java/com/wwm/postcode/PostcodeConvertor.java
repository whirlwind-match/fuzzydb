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

import org.slf4j.Logger;
import org.springframework.core.convert.converter.Converter;

import com.wwm.db.core.LogFactory;
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

	/**Look up a full postcode.<br><br>WARNING - EXECUTING THIS FUNCTION COSTS 1p!!<br><br>
	 * @param postcode A full postcode to lookup, any caps, any spaces
	 * @return A PostcodeResult if the postcode is valid, otherwise null
	 */
	public synchronized GeoInformation lookupFull(String postcode) {
		// If no service for full postcode, then try short somehow
		if (service != null){
			return service.lookupFull(postcode);
		}
		log.debug("No PostcodeService present, falling back to jibble for {}", postcode);
		postcode = StringUtils.stripSpaces(postcode);

		int trimmedLength = postcode.length() - 3; // strip off tail
		if (trimmedLength <2 || trimmedLength > 4) return null;
		
		postcode = postcode.substring(0, trimmedLength);
		return jibble.lookupShort(postcode);
	}

	@Override
	public GeoInformation convert(String postcode) {
        PostcodeResult result = jibble.lookupShort(postcode);
        if (result == null) {
            result = service.lookupFull(postcode);
        }
        return result;

	}
}
