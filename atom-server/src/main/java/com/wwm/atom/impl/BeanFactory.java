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
package com.wwm.atom.impl;

import java.util.GregorianCalendar;

import com.wwm.atom.elements.AgeRangeElement;
import com.wwm.atom.elements.AttributeElement;
import com.wwm.atom.elements.BooleanElement;
import com.wwm.atom.elements.DateElement;
import com.wwm.atom.elements.EnumElement;
import com.wwm.atom.elements.FloatElement;
import com.wwm.atom.elements.FloatRangeElement;
import com.wwm.atom.elements.LocationElement;
import com.wwm.atom.elements.MultiEnumElement;
import com.wwm.atom.elements.SimpleAttributeElement;
import com.wwm.atom.elements.StringElement;
import com.wwm.indexer.db.publictypes.EnumMulti;
import com.wwm.indexer.db.publictypes.FloatRange;

// TODO: Change so that it works for types in StandaloneIndex

public class BeanFactory {

	static final BeanFactory instance = new BeanFactory();
	
	public static BeanFactory getInstance() {
		return instance;
	}
	

	public Object getObject(AttributeElement attr) {

		if (attr instanceof AgeRangeElement){
			AgeRangeElement f = (AgeRangeElement) attr;
			return new FloatRange( f.getMin(), f.getPref(), f.getMax() );
		} else if (attr instanceof BooleanElement){
			BooleanElement b = (BooleanElement) attr;
			return b.getValue();
		} else if (attr instanceof DateElement) {
			DateElement d = (DateElement) attr;
			int year = d.getYear();
			int month = d.getMonth();
			int day = d.getDay() == -1 ? 15 : d.getDay();
			
			GregorianCalendar date = new GregorianCalendar();
			if (year != -1 && month != -1) {
	            date.set(year, month, day);
	        }
	        return date;
		} else if (attr instanceof EnumElement) {
			EnumElement ee = (EnumElement) attr;
			return ee.getValue();
		} else if (attr instanceof FloatElement) {
			FloatElement f = (FloatElement) attr;
			return f.getValue();
		} else if (attr instanceof FloatRangeElement) {
			FloatRangeElement fr = (FloatRangeElement) attr;
			return new FloatRange( fr.getMin(), fr.getPref(), fr.getMax() );
		} else if (attr instanceof LocationElement) {
			LocationElement loc = (LocationElement) attr;
			return loc.getPostcode(); // return it as a string
		} else if (attr instanceof MultiEnumElement) {
			MultiEnumElement mee = (MultiEnumElement) attr;
	    	return new EnumMulti( mee.getValues() );
		} else if (attr instanceof SimpleAttributeElement) {
			SimpleAttributeElement sa = (SimpleAttributeElement) attr;
			return sa.getValue();
		} else if (attr instanceof StringElement) {
			StringElement se = (StringElement) attr;
			return se.getValue();
		}
		
		throw new Error("oops!");
	}


}
