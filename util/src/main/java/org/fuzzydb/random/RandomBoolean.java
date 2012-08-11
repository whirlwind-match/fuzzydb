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
package org.fuzzydb.random;


import org.fuzzydb.dto.attributes.BooleanAttribute;
import org.fuzzydb.dto.attributes.RandomGenerator;
import org.fuzzydb.util.MTRandom;


public class RandomBoolean implements RandomGenerator<BooleanAttribute> {

    private int truePercent;
    private int falsePercent;

    public RandomBoolean(int truePercent, int falsePercent) {
        this.truePercent = truePercent;
        this.falsePercent = falsePercent;
    }

    public BooleanAttribute next(String attrName) {
        int rand = MTRandom.getInstance().nextInt(100);

        if (rand < truePercent) {
            return new BooleanAttribute(attrName, true);
        } else if (rand < truePercent + falsePercent) {
            return new BooleanAttribute(attrName, false);
        }

        return null;
    }
}
