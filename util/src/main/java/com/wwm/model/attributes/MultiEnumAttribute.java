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

public class MultiEnumAttribute extends EnumeratedAttribute<String[]> {

	private static final long serialVersionUID = 1L;

    private String[] values;
    private String enumName;

    public MultiEnumAttribute(String name, String enumName, String[] values) {
        super(name);
        this.enumName = enumName;
        this.values = values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String[] getValues() {
        return values;
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
        StringBuilder str = new StringBuilder();
        boolean first = true;
        for (String s : values) {
            if (first){
                str.append(s);
                first = false;
            } else {
                str.append(',').append(s);
            }
        }
        return str.toString();
    }
    
    @Override
    public String[] getValueAsObject() {
    	return values;
    }
}
