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
package com.wwm.indexer.db.converters;


import org.fuzzydb.dto.attributes.NonIndexStringAttribute;
import org.fuzzydb.postcode.PostcodeConvertor;
import org.fuzzydb.util.geo.GeoInformation;

import com.wwm.indexer.exceptions.AttributeException;


public class UKPostcodeToPlaceDerivation extends InboundDerivation<String> {

    private static final PostcodeConvertor converter = TempFactory.getPostcodeConverter();

    public UKPostcodeToPlaceDerivation(String derivedAttrName) {
        super(derivedAttrName);
    }

    @Override
    public Class<String> getInboundClass() {
        return String.class;
    }

    @Override
    synchronized public String convertToInternal(int attrid, Object object) throws AttributeException {
        NonIndexStringAttribute attr = (NonIndexStringAttribute) object;
        String postcode = attr.getValue();
        GeoInformation result = converter.lookupShort(postcode);
        if (result == null) {
            result = converter.lookupFull(postcode);
        }

        if (result == null){
            throw new AttributeException("Unable to lookup postcode for: "
                    + attr.getName() + "=" + postcode);
        }

        return result.getTown();
    }

}
