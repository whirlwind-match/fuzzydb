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
package com.wwm.atom.elements;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.abdera.model.Entry;
import org.fuzzydb.dto.attributes.Score;




/**
 * A class used to add fuzzy elements to an Atom Entry (i.e. Document/Record).
 * This turns a normal Atom entry into one annotated with Fuzzy record properties.
 */
public class EntryDecorator {

    protected Entry entry;

    public EntryDecorator(Entry entry) {
        super();
        this.entry = entry;
    }

    public void addSimpleAttribute(String name, String value) {
    	if (value == null) return; // allow null for "don't care" / "not specified"
        SimpleAttributeElement element = entry.addExtension(AbderaElementFactory.wwmSimpleAttribute);
        element.setName(name);
        element.setValue(value);
    }

    public void addBoolean(String name, String value) {
    	if (value == null) return; // allow null for "don't care" / "not specified"
        BooleanElement element = entry.addExtension(AbderaElementFactory.wwmBoolean);
        element.setName(name);
        element.setValue(value);
    }

    public void addEnum(String name, String enumName, String value) {
		if (value == null) {
			throw new Error("Null not allowed for an enum. enumAttr=" + name );
		}

        EnumElement element = entry.addExtension(AbderaElementFactory.wwmEnum);
        element.setName(name);
        element.setEnumName(enumName);
        element.setValue(value);
    }

    public void addEnumList(String name, String enumName, String ... values) {
        MultiEnumElement element = entry.addExtension(AbderaElementFactory.wwmEnumList);
        element.setName(name);
        element.setEnumName(enumName);
        for (String value : values) {
            element.addValue(value);
        }
    }

    public void addString(String name, String value) {
    	if (value == null) return; // allow null for "don't care" / "not specified"

        StringElement element = entry.addExtension(AbderaElementFactory.wwmString);
        element.setName(name);
        element.setValue(value);
    }

    public void addAgeRange(String name, float min, float pref, float max) {
        AgeRangeElement element = entry.addExtension(AbderaElementFactory.wwmAgeRange);
        element.setName(name);
        element.setMin(min);
        element.setPref(pref);
        element.setMax(max);
    }

    public void addFloatRange(String name, float min, float pref, float max) {
        AgeRangeElement element = entry.addExtension(AbderaElementFactory.wwmAgeRange); // FIXME: Implement wwmFloatRange
        element.setName(name);
        element.setMin(min);
        element.setPref(pref);
        element.setMax(max);
    }

    public void addDate(String name, int year, int month) {
        DateElement element = entry.addExtension(AbderaElementFactory.wwmDate);
        element.setName(name);
        element.setYear(year);
        element.setMonth(month);
    }

    public void addDate(String name, int year, int month, int day) {
        DateElement element = entry.addExtension(AbderaElementFactory.wwmDate);
        element.setName(name);
        element.setYear(year);
        element.setMonth(month);
        element.setDay(day);
    }

    public void addDate(String name, Date date) {
    	if (date == null) return; // allow null for "don't care" / "not specified"

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        addDate(name, year, month, day);
    }

    public void addFloat(String name, Float value) {
    	if (value == null) return; // allow null for "don't care" / "not specified"
    	FloatElement element = entry.addExtension(AbderaElementFactory.wwmFloat);
        element.setName(name);
        element.setValue(value.floatValue());
    }

    public void addLocation(String name, String postcode) {
    	if (postcode == null) return; // allow null for "don't care" / "not specified"
        LocationElement element = entry.addExtension(AbderaElementFactory.wwmLocation);
        element.setName(name);
        element.setPostcode(postcode);
    }

    public void setMetadata(String contentType, String privateId) {
        FuzzyRecordProperties element = entry.addExtension(AbderaElementFactory.wwmRecordProperties);
        element.setValue(contentType);
        element.setPrivateId(privateId);
    }

    public void setScores(Score score) {
        FuzzyScoreElement element = entry.addExtension(AbderaElementFactory.wwmScores);
        element.setScore(score.total());
    }



}