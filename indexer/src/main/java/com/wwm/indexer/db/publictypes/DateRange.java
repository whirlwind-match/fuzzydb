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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateRange {
    
    private static final long serialVersionUID = 6019989083517270116L;

    Date minValue;	// lowest date, i.e. oldest age
	Date prefValue;
	Date maxValue;	// highest date, i.e. youngest age

	public DateRange() {}

	public DateRange(FloatRange frp) {
		minValue = new Date((long) frp.getMinValue());
		prefValue = new Date((long) frp.getPrefValue());
		maxValue = new Date((long) frp.getMaxValue());
	}
	
	public FloatRange getFloatBean() {
		return new FloatRange(minValue.getTime(), prefValue.getTime(), maxValue.getTime() );
	}
	
	public DateRange(Date minValue, Date prefValue, Date maxValue) {
        assert(minValue.equals(prefValue) || minValue.before(prefValue));
        assert(prefValue.equals(maxValue) || prefValue.before(maxValue));
		this.minValue = minValue;
		this.prefValue = prefValue;
		this.maxValue = maxValue;
	}
	
	public DateRange(Integer minAge, Integer prefAge, Integer maxAge) {
        assert(minAge.equals(prefAge) || minAge < prefAge);
        assert(prefAge.equals(maxAge) || prefAge< maxAge);
        
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.YEAR, -minAge);	// 18 years or older must have been born 18 years ago.
        maxValue = gc.getTime();
        
        gc = new GregorianCalendar();
        gc.add(Calendar.YEAR, -(maxAge));	// 80 years or younger must have been born 80 years ago.
        minValue = gc.getTime();
        
        gc = new GregorianCalendar();
        gc.add(Calendar.YEAR, -(prefAge));	// 80 years or younger must have been born 80 years ago.
        prefValue = gc.getTime();
	}

	private Integer dateToAge(Date date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		int year = gc.get(Calendar.YEAR);
		int day = gc.get(Calendar.DAY_OF_YEAR);
		
		GregorianCalendar gcNow = new GregorianCalendar();
		int yearNow = gcNow.get(Calendar.YEAR);
		int dayNow = gcNow.get(Calendar.DAY_OF_YEAR);
		
		int age = yearNow - year;
		
		if (dayNow < day) {
			age--;
		}
		
		return age;
		
	}
	
	public Integer getMaxAge() {
		return dateToAge(minValue);
	}

	public Integer getMinAge() {
		return dateToAge(maxValue);
	}

	public Integer getPrefAge() {
		return dateToAge(prefValue);
	}
	
	public Date getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Date maxValue) {
        assert(prefValue.equals(maxValue) || prefValue.before(maxValue));
		this.maxValue = maxValue;
	}
	public Date getMinValue() {
		return minValue;
	}
	public void setMinValue(Date minValue) {
        assert(minValue.equals(prefValue) || minValue.before(prefValue));
		this.minValue = minValue;
	}
	public Date getPrefValue() {
		return prefValue;
	}
	public void setPrefValue(Date prefValue) {
        assert(minValue.equals(prefValue) || minValue.before(prefValue));
        assert(prefValue.equals(maxValue) || prefValue.before(maxValue));
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

    /**
     * Gets a date range where:
     * max = 'period' 'gregorianUnits' ago (e.g. 1 DAY ago; 3 MONTH ago)
     * pref = max - 1 day
     * min = pref - 1 year
     * 
     * @param gregorianUnits
     * @param period
     * @return
     */
	public static DateRange getDateRange(int gregorianUnits, int period) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(gregorianUnits, -period);
		Date max = gc.getTime();
		gc.add(Calendar.DAY_OF_YEAR, -1);
		Date pref = gc.getTime();
		gc.add(Calendar.YEAR, -1);
		Date min = gc.getTime();
		DateRange dateRange = new DateRange(min, pref, max);
		return dateRange;
	}    
}