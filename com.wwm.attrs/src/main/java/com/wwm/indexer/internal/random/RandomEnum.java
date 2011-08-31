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


import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.model.attributes.EnumAttribute;
import com.wwm.util.MTRandom;


public class RandomEnum implements RandomGenerator {

    EnumDefinition enumdef;
    private int nullPercent;

    public RandomEnum(EnumDefinition enumdef) {
        this.enumdef = enumdef;
    }

    public EnumAttribute next(String attrName) {
        int rand = MTRandom.getInstance().nextInt(100);
        if (rand < nullPercent) {
            return null;
        }

        short randenum = (short) MTRandom.getInstance().nextInt(enumdef.size());
        String str = enumdef.findAsString(randenum);
        return new EnumAttribute(attrName, enumdef.getName(), str);
    }
}
