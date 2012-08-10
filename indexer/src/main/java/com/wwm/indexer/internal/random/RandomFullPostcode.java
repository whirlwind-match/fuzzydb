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


import org.fuzzydb.postcode.RandomPostcodeGenerator;

import com.wwm.model.attributes.NonIndexStringAttribute;
import com.wwm.model.attributes.RandomGenerator;
import com.wwm.util.MTRandom;

public class RandomFullPostcode implements RandomGenerator {

    RandomPostcodeGenerator gen;

    public NonIndexStringAttribute next(String attrName) {
        return new NonIndexStringAttribute(attrName, getGen().nextFullPostcode());
    }

    public synchronized RandomPostcodeGenerator getGen() {
        if (gen == null) {
            gen = new RandomPostcodeGenerator(MTRandom.getInstance());
        }
        return gen;
    }
}
