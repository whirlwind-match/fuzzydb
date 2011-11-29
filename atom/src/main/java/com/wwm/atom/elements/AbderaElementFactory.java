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

import javax.xml.namespace.QName;

import org.apache.abdera.util.AbstractExtensionFactory;


public class AbderaElementFactory extends AbstractExtensionFactory {

	public static final String NS = "http://schema.whirlwindmatch.com/data/1.0";

	/** The contentType/profileType of this record */
	public static final QName wwmRecordProperties = new QName(NS, "record-properties", "wwm");
	public static final QName wwmScores = new QName(NS, "scores", "wwm");
	
	public static final QName wwmSimpleAttribute = new QName(NS, "attr", "wwm");

//	public static final QName wwmBoolean = new QName(NS, "attr", "wwm");
//	public static final QName wwmEnum = new QName(NS, "attr", "wwm");
//	public static final QName wwmLocation = new QName(NS, "attr", "wwm");
//	public static final QName wwmString = new QName(NS, "attr", "wwm");

	// NOTE: The following were the original tags, but simplified as all text value
	public static final QName wwmBoolean = new QName(NS, "boolean", "wwm");
	public static final QName wwmEnum = new QName(NS, "enum", "wwm");
	public static final QName wwmLocation = new QName(NS, "location", "wwm");
	public static final QName wwmString = new QName(NS, "string", "wwm");

	public static final QName wwmDate = new QName(NS, "date", "wwm");
	public static final QName wwmAgeRange = new QName(NS, "agerange", "wwm");
	public static final QName wwmEnumList = new QName(NS, "enumlist", "wwm");
	public static final QName wwmFloat = new QName(NS, "float", "wwm");
	public static final QName wwmFloatRange = new QName(NS, "floatrange", "wwm");
	public static final QName wwmValue = new QName(NS, "value", "wwm");

	public AbderaElementFactory() {
		super(NS);
		addImpl(wwmRecordProperties, FuzzyRecordProperties.class);
		addImpl(wwmScores, FuzzyScoreElement.class);

		addImpl(wwmSimpleAttribute, SimpleAttributeElement.class);

//		addImpl(wwmBoolean, SimpleAttributeElement.class);
//		addImpl(wwmEnum, SimpleAttributeElement.class);
//		addImpl(wwmLocation, SimpleAttributeElement.class);
//		addImpl(wwmString, SimpleAttributeElement.class);

		addImpl(wwmBoolean, BooleanElement.class);
		addImpl(wwmEnum, EnumElement.class);
		addImpl(wwmLocation, LocationElement.class);
		addImpl(wwmString, StringElement.class);

		addImpl(wwmDate, DateElement.class);
		addImpl(wwmAgeRange, AgeRangeElement.class);
		addImpl(wwmEnumList, MultiEnumElement.class);
		addImpl(wwmFloat, FloatElement.class); 
		addImpl(wwmFloatRange, FloatRangeElement.class);
	}

}
