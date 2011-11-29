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


import com.wwm.attrs.location.EcefVector;
import com.wwm.indexer.exceptions.AttributeException;
import com.wwm.model.attributes.Point3DAttribute;
import com.wwm.model.attributes.RandomGenerator;
import com.wwm.postcode.RandomPostcodeGenerator;
import com.wwm.util.MTRandom;

public class RandomLocation implements RandomGenerator<Point3DAttribute> {

    private int nullProportion;
    private RandomPostcodeGenerator gen;
    private PostcodeConverter converter;


    public Point3DAttribute next(String attrName) {
        int rand = MTRandom.getInstance().nextInt(100);
        if (rand < nullProportion) {
            return null;
        }

        // Use 0 as this will be replaced
        try {
            EcefVector vec = getConverter().convertToInternal(0, getGen().nextFullPostcode());
            return new Point3DAttribute(attrName, vec);
        } catch (AttributeException e) {
            throw new Error(e); // shouldn't get invalid postcodes.
        }
    }

    public synchronized PostcodeConverter getConverter() {
        if (converter == null) {
            converter = new PostcodeConverter();
        }
        return converter;
    }

    public synchronized RandomPostcodeGenerator getGen() {
        if (gen == null) {
            gen = new RandomPostcodeGenerator(MTRandom.getInstance());
        }
        return gen;
    }
}
