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
package com.wwm.indexer.db.publictypes;

import java.util.ArrayList;

public class EnumMulti {

    private String[] values;

	public EnumMulti() {}
    public EnumMulti(String[] values) {
        setValues(values);
    }
    public EnumMulti(ArrayList<String> values) {
        setValues(values);
    }
    
    public String[] getValues() {
        return values;
    }

    public ArrayList<String> getValuesArrayList() {
        ArrayList<String> result = new ArrayList<String>();
        for (String value: values) {
            result.add(value);
        }
        return result;
    }
    
    public void setValues(String[] values) {
        this.values = values;
    }
    
    public void setValues(ArrayList<String> values) {
        if(values == null) {
            this.values = new String[0];
            return;
        }
        
        this.values = new String[values.size()];
        values.toArray(this.values);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean prefix = false;
        for (String value: values ) {
            if (prefix == false) { prefix = true; }
            else {sb.append(", ");}
            sb.append(value);
        }
        return sb.toString();
    }

    public Object getObject() {
        return this;
    }
    public Object getClazz() {
        return this.getClass();
    }

    @Override
    public boolean equals(Object obj) {
    	EnumMulti bean = (EnumMulti) obj;
    	if (bean.getValues().length != values.length) return false;
    	for (int i = 0; i < values.length; i++) {
			if (!values[i].equals(bean.getValues()[i])) return false;
		}
    	return true;
    }
    
}