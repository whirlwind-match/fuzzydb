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
package com.wwm.attrs.decorators;


import java.util.Calendar;
import java.util.GregorianCalendar;

import com.wwm.attrs.Decorator;
import com.wwm.attrs.internal.BaseAttribute;
import com.wwm.attrs.simple.FloatValue;




/**
 * @author Neale
 */
public class AgeDecorator extends Decorator {

	private static final long serialVersionUID = 3256440291920984112L;

	/**
	 * Create Only parameter for date is the name.
	 * @param attrName
	 */
	public AgeDecorator( String attrName ) {
		super( attrName );
	}
	
	
	/**
	 * Turn a value we know to be a date into an age based on todays date.
	 */
	@Override
	public String getValueString(BaseAttribute attr) {
        FloatValue val = (FloatValue)attr;
        
        GregorianCalendar today = new GregorianCalendar();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);
        
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis((long) val.getValue());
       
        // Calculate year difference between date we have (DOB)
        // and today then decrement by one if today's month/day
        // is before the month/day of the DOB.
        int age = year - cal.get(Calendar.YEAR);
        if ( ( month < cal.get(Calendar.MONTH) ) || 
             ( month == cal.get(Calendar.MONTH) && day < cal.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return String.valueOf(age);
	}
}
