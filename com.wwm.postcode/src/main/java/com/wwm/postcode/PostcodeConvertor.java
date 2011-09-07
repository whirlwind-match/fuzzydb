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
import com.wwm.util.StringUtils;

/**
 * Rather piggy-in-the-middle implementation which sits bridging static instances and OSGi service objects.
 * 
 * FIXME: This is dangerous as it assumes that only one instance of PostcodeConverter is created, and
 * therefore uses the DAO object supplied for that.
 *
 */
public class PostcodeConvertor implements Converter<String, PostcodeResult>{


	private static Logger log = LogFactory.getLogger(PostcodeConvertor.class);
	static private PostcodeService service;
			
	private final JibbleConvertor jibble;

	public static class LostDbConnection extends Exception {
		private static final long serialVersionUID = 5523931674418224181L;
	}
	
	public PostcodeConvertor() {
		jibble = new JibbleConvertor(log);
	}
	
	public static void setService(PostcodeService service) {
		PostcodeConvertor.service = service;
	}
	
	public PostcodeResult lookupShort(String prefix) {
		return jibble.lookupShort(prefix);
	}

	/**Look up a full postcode.<br><br>WARNING - EXECUTING THIS FUNCTION COSTS 1p!!<br><br>
	 * @param postcode A full postcode to lookup, any caps, any spaces
	 * @return A PostcodeResult if the postcode is valid, otherwise null
	 * @throws LostDbConnection
	 */
	public synchronized PostcodeResult lookupFull(String postcode) {
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
	public PostcodeResult convert(String postcode) {
        PostcodeResult result = jibble.lookupShort(postcode);
        if (result == null) {
            result = service.lookupFull(postcode);
        }
        return result;

	}
}
