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


public class FloatRange {

    private static final long serialVersionUID = -2322052199850432105L;
    
    float minValue;
	float prefValue;
	float maxValue;
	
	public FloatRange() {}
	public FloatRange(float minValue, float prefValue, float maxValue) {
        assert(minValue <= prefValue);
        assert(prefValue <= maxValue);
		this.minValue = minValue;
		this.prefValue = prefValue;
		this.maxValue = maxValue;
	}
	public float getMaxValue() {
		return maxValue;
	}

	public int getMaxValueAsInt(){
		return Math.round( maxValue );
	}
	
	public void setMaxValue(float maxValue) {
        assert(prefValue <= maxValue);
		this.maxValue = maxValue;
	}

	public float getMinValue() {
		return minValue;
	}
	
	public int getMinValueAsInt(){
		return Math.round( minValue );
	}
	
	public void setMinValue(float minValue) {
        assert(minValue <= prefValue);
		this.minValue = minValue;
	}

	public float getPrefValue() {
		return prefValue;
	}

	public int getPrefValueAsInt(){
		return Math.round( prefValue );
	}
	
	public void setPrefValue(float prefValue) {
        assert(minValue <= prefValue);
        assert(prefValue <= maxValue);
		this.prefValue = prefValue;
	}

	public void setValues(float minValue, float prefValue, float maxValue) {
        assert(minValue <= prefValue);
        assert(prefValue <= maxValue);
		this.minValue = minValue;
		this.prefValue = prefValue;
		this.maxValue = maxValue;
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