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


public class IntegerRange  {

    private static final long serialVersionUID = 6374805371660899960L;
    
    int minValue;
	int prefValue;
	int maxValue;
	
	public IntegerRange() {}
	public IntegerRange(int minValue, int prefValue, int maxValue) {
        assert(minValue <= prefValue);
        assert(prefValue <= maxValue);
		this.minValue = minValue;
		this.prefValue = prefValue;
		this.maxValue = maxValue;
	}
	public int getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(int maxValue) {
        assert(prefValue <= maxValue);
		this.maxValue = maxValue;
	}
	public int getMinValue() {
		return minValue;
	}
	public void setMinValue(int minValue) {
        assert(minValue <= prefValue);
		this.minValue = minValue;
	}
	public int getPrefValue() {
		return prefValue;
	}
	public void setPrefValue(int prefValue) {
        assert(minValue <= prefValue);
        assert(prefValue <= maxValue);
		this.prefValue = prefValue;
	}
    
    @Override
    public String toString() { 
        return minValue + " < " + prefValue + " < " + maxValue;
    }    

    public Object getObject() {
        return this;
    }
    public Object getClazz() {
        return this.getClass();
    }    
}