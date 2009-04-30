/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.util;

import java.text.DecimalFormat;

/**
 * @author Neale
 */
public class Stopwatch {

    private long start; // start time in millis
    private long duration = 0;
    
    
    public void start(){
        start = System.currentTimeMillis();
    }
    
    public void stop(){
        duration += (System.currentTimeMillis() - start);
    }
    
    public void reset(){
        duration = 0;
    }
    
    
    @Override
	public String toString(){
        final DecimalFormat format = new DecimalFormat("0.000");
        String str = format.format( duration / 1000.0f);
        return str + " secs";
    }
    
    
    public float getValue() {
    	return duration;
    }
    
}
