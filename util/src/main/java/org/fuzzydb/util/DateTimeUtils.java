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
package org.fuzzydb.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {

    private static final double SECOND = 1000;
    private static final double MINUTE = SECOND * 60;
    private static final double HOUR   = MINUTE * 60;
    private static final double DAY    = HOUR * 24;
    private static final double WEEK   = DAY * 7;
    private static final double YEAR   = DAY * 365.25;
    
    public static float getDateDiff(Date d1, Date d2, int gregorianCalendarUnits) {
        return getMillisDiff(d1.getTime(), d2.getTime(), gregorianCalendarUnits);
    }
    
    public static float getMillisDiff(long lhs, long rhs, int gregorianCalendarUnits) {
        long millis = lhs - rhs; 
        switch(gregorianCalendarUnits) {
        case Calendar.MILLISECOND:
            return millis;
        case Calendar.SECOND:
            return (float) (millis/SECOND);
        case Calendar.MINUTE:
            return (float) (millis/MINUTE);
        case Calendar.HOUR:
            return (float) (millis/HOUR);
        case Calendar.DAY_OF_WEEK:
        case Calendar.DAY_OF_MONTH:
        case Calendar.DAY_OF_YEAR:
            return (float) (millis/DAY);
        case Calendar.WEEK_OF_MONTH:
        case Calendar.WEEK_OF_YEAR:
            return (float) (millis/WEEK);
        case Calendar.YEAR:
            return (float) (millis/YEAR);
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
