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

public class EnumAttribute extends EnumeratedAttribute<String> {

	private static final long serialVersionUID = 1L;

    private String value;
    private String enumName;

    public EnumAttribute(String name, String enumName, String value) {
        super(name);
        assert enumName != null;
        assert value != null;
        this.enumName = enumName;
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
	public String getEnumName() {
        return enumName;
    }

    @Override
	public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    @Override
    public String toString() {
        return value;
    }
    
    @Override
    public String getValueAsObject() {
    	return value;
    }
}
