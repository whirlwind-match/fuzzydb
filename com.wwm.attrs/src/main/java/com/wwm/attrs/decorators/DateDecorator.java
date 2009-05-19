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
import com.wwm.attrs.simple.FloatHave;




/**
 * @author Neale
 */
public class DateDecorator extends Decorator {

	private static final long serialVersionUID = 3256440291920984112L;

	/**
	 * Create Only parameter for date is the name.
	 * @param attrName
	 */
	public DateDecorator( String attrName ) {
		super( attrName );
	}
	
	
	/**
	 * Turn a value we know to be a date into dd/mm/yyyy string, or use MIN/MAX if
	 * the float value is -/+Float.MAX_VALUE
	 */
	@Override
	public String getValueString(BaseAttribute attr) {
        FloatHave val = (FloatHave)attr;
        if (val.getValue() == -Float.MAX_VALUE) {
            return "MIN";
        }
        else if (val.getValue() == Float.MAX_VALUE) {
            return "MAX";
        }
        
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis((long) val.getValue());

        return cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) 
        + "/" + cal.get(Calendar.YEAR) + " (" + val.getValue() + ")";
	}
}
