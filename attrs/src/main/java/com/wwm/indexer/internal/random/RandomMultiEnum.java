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
import com.wwm.attrs.enums.OptionsSource;
import com.wwm.model.attributes.MultiEnumAttribute;
import com.wwm.util.MTRandom;

public class RandomMultiEnum extends AbstractRandomGenerator<MultiEnumAttribute> {

    OptionsSource options;

    public RandomMultiEnum(EnumDefinition enumdef) {
        this.options = enumdef;
    }

    public RandomMultiEnum(OptionsSource optionsSource, float nullProportion) {
    	super(nullProportion);
        this.options = optionsSource;
    }

    protected MultiEnumAttribute randomResult(String attrName) {
        int numvals = MTRandom.getInstance().nextInt(options.size() - 1) + 1;

        TreeSet<String> values = new TreeSet<String>();
        while (numvals > values.size()) {
            short randIndex = (short) MTRandom.getInstance().nextInt(options.size());
            values.add(options.findAsString(randIndex));
        }

        String[] result = new String[numvals];
        result = values.toArray(result); // uses supplied array
        return new MultiEnumAttribute(attrName, options.getName(), result);
    }
}
