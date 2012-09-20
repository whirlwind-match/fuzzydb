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
package org.fuzzydb.dto.attributes;

import org.bson.types.ObjectId;

public class ObjectIdAttribute extends Attribute<ObjectId> {

	private static final long serialVersionUID = 1L;

	private ObjectId value;

    public ObjectIdAttribute(String name, ObjectId value) {
        super(name);
        this.value = value;
    }

    public ObjectIdAttribute(String name, String value) {
        super(name);
        this.value = new ObjectId(value);
    }

    public void setValue(ObjectId value) {
        this.value = value;
    }

    public ObjectId getValue() {
        return value;
    }

    @Override
    public String toString() {

        return value.toString();
    }

	@Override
	public ObjectId getValueAsObject() {
		return value;
	}
}
