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


import com.wwm.model.attributes.FloatAttribute;
import com.wwm.util.MTRandom;

public class RandomFloat extends AbstractRandomGenerator<FloatAttribute> {

    private float min;
    private float max;
    
    public RandomFloat(float min, float max, float nullProportion) {
        super(nullProportion);
    	this.min = min;
        this.max = max;
    }

    protected FloatAttribute randomResult(String attrName) {

        float f = min + ((max - min) * MTRandom.getInstance().nextFloat());
        return new FloatAttribute(attrName, f);
    }
}
