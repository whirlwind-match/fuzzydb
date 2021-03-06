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


import java.util.UUID;

import org.fuzzydb.dto.attributes.RandomGenerator;
import org.fuzzydb.dto.attributes.UuidAttribute;


public class RandomUuid implements RandomGenerator<UuidAttribute> {

    @Override
	public UuidAttribute next(String attrName) {
        return new UuidAttribute(attrName, UUID.randomUUID());
    }
}
