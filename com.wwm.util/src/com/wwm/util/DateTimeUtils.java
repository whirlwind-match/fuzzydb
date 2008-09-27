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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {

    public static float getDateDiff(Date d1, Date d2, int gregorianCalendarUnits) {
        long millis = d1.getTime()-d2.getTime(); 
        switch(gregorianCalendarUnits) {
        case Calendar.MILLISECOND:
            return millis;
        case Calendar.SECOND:
            return millis/1000;
        case Calendar.MINUTE:
            return millis/1000/60;
        case Calendar.HOUR:
            return millis/1000/60/60;
        case Calendar.DAY_OF_WEEK:
        case Calendar.DAY_OF_MONTH:
        case Calendar.DAY_OF_YEAR:
            return millis/1000/60/60/24;
        case Calendar.WEEK_OF_MONTH:
        case Calendar.WEEK_OF_YEAR:
            return millis/1000/60/60/24/7;
        case Calendar.YEAR:
            return (float) (millis/1000/60/60/24/365.25);
        default:
            return 0.0f;
        }
    }

    public static float getDateDiff(GregorianCalendar d1, GregorianCalendar d2, int gregorianCalendarUnits) {
        return getDateDiff(d1.getTime(), d2.getTime(), gregorianCalendarUnits);
    }
    
    public static long getDateUnit(Date date, int gregorianCalendarUnits) {
    	GregorianCalendar cal = new GregorianCalendar();
    	cal.setTime(date);
    	return cal.get(gregorianCalendarUnits);
    }
}
