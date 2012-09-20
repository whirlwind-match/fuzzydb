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


import org.bson.types.ObjectId;
import org.fuzzydb.dto.attributes.ObjectIdAttribute;
import org.fuzzydb.dto.attributes.RandomGenerator;


public class RandomObjectId implements RandomGenerator<ObjectIdAttribute> {

    @Override
	public ObjectIdAttribute next(String attrName) {
        return new ObjectIdAttribute(attrName, ObjectId.get());
    }
}
