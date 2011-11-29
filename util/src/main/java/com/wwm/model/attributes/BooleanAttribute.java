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
package com.wwm.model.attributes;

public class BooleanAttribute extends Attribute<Boolean> {

    private boolean value;

    public BooleanAttribute(String name, boolean value) {
        super(name);
        this.value = value;
    }

    public BooleanAttribute(String name, Boolean value) {
        super(name);
        this.value = value.booleanValue();
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {

        return value ? "true" : "false";
    }

	@Override
	public Boolean getValueAsObject() {
		return Boolean.valueOf(value);
	}
}
