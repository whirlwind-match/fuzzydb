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
package com.wwm.attrs;

import java.util.Calendar;
import java.util.GregorianCalendar;


import org.junit.Assert;
import org.junit.Test;

import com.wwm.attrs.simple.FloatConstraint;
import com.wwm.attrs.simple.FloatHave;

/**
 * @author Neale
 */
public class DobRangeValueTest {

    private int id = 1; 

    FloatHave dob;
    FloatHave splitDob;
    
    FloatConstraint low; 
    FloatConstraint high;
    

    /**
     * Constructor for DobRangeValueTest.
     * @param testMethod
     */
    public DobRangeValueTest() {

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1970, Calendar.OCTOBER, 22);
        dob = new FloatHave(id, cal.getTimeInMillis() ); 

        cal.set(1980, Calendar.JANUARY, 1);
        splitDob = new FloatHave( id, cal.getTimeInMillis() );

        low = new FloatConstraint( id, Float.MIN_VALUE, splitDob.getValue() );
        high = new FloatConstraint( id, splitDob.getValue(), Float.MAX_VALUE );
        
    }
    
    @Test
    public void testQualify() {
        Assert.assertTrue(low.consistent(dob) );
        Assert.assertFalse( high.consistent(dob) );
    }
    
    @Test
    public void testSplitPoint() {
    	// The split point should go into one or the other container - but not both
    	Assert.assertTrue(low.consistent(splitDob) ^ high.consistent(splitDob));
    }


}
