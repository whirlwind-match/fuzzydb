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
package com.wwm.indexer.internal.random;


import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.location.EcefVector;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.geo.GeoInformation;
import com.wwm.indexer.db.converters.TempFactory;
import com.wwm.indexer.exceptions.AttributeException;
import com.wwm.model.attributes.LocationAttribute;


public class PostcodeConverter {

    private static final Converter<String, GeoInformation> converter = TempFactory.getPostcodeConverter();

    public Class<EcefVector> getIAttributeClass() {
        return EcefVector.class;
    }

    public Class<LocationAttribute> getObjectClass() {
        return LocationAttribute.class;
    }

    public EcefVector convert(String name, IAttribute attribute) {
        return (EcefVector) attribute;
    }

    synchronized public EcefVector convertToInternal(int attrid, Object object) throws AttributeException {
        // FIXME: a bodge to work with either
        String name, postcode;
        if (object instanceof LocationAttribute){
            LocationAttribute attr = (LocationAttribute) object;
            postcode = attr.getPostcode();
            name = attr.getName();
        } else {
            postcode = (String)object;
            name = String.valueOf(attrid);
        }
        GeoInformation result = converter.convert(postcode);

        if (result == null){
            throw new AttributeException("Unable to lookup postcode for: " + name + "=" + postcode);
        }

        return EcefVector.fromDegs(attrid, result.getLatitude(), result.getLongitude());
    }
}
