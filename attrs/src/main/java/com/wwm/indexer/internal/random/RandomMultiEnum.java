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

import java.util.TreeSet;

import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.model.attributes.MultiEnumAttribute;
import com.wwm.util.MTRandom;

public class RandomMultiEnum extends AbstractRandomGenerator<MultiEnumAttribute> {

    EnumDefinition enumdef;

    public RandomMultiEnum(EnumDefinition enumdef) {
        this.enumdef = enumdef;
    }

    public RandomMultiEnum(EnumDefinition enumdef, float nullProportion) {
    	super(nullProportion);
        this.enumdef = enumdef;
    }

    protected MultiEnumAttribute randomResult(String attrName) {
        int numvals = MTRandom.getInstance().nextInt(enumdef.size() - 1) + 1;

        TreeSet<String> values = new TreeSet<String>();
        while (numvals > values.size()) {
            short randenum = (short) MTRandom.getInstance().nextInt(enumdef.size());
            values.add(enumdef.findAsString(randenum));
        }

        String[] result = new String[numvals];
        result = values.toArray(result); // uses supplied array
        return new MultiEnumAttribute(attrName, enumdef.getName(), result);
    }
}
