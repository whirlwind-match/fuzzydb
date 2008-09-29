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
package com.whirlwindmatch.api;



import java.text.DateFormat;
import java.util.Date;

import com.wwm.atom.elements.EntryDecorator;

public class TestAtomDating extends BaseAtomTest {

	@Override
	protected void addAttrs(EntryDecorator builder) {
		// Item and Id = for now create Id as time value which should be unique enough
		builder.setMetadata("Dating", privateId);

		// <wwm:String name="FirstName" value="Neale"/>
		builder.addSimpleAttribute("ScreenName", "JUnitTest");
		builder.addSimpleAttribute("SummaryTag", "Item posted at " + DateFormat.getDateInstance().format(new Date()));
		builder.addSimpleAttribute("Description", "This is an item inserted via web services.");

		// <wwm:AgeRange name="wantAge" low="28" {pref="34"} high="38"/>
		builder.addAgeRange("wantAge", 28f, 34f, 38f);
		builder.addBoolean("Gender", "Male");

		// <wwm:Date name="DoB" month="Oct" year="1970" {day="22"} />
		builder.addDate("Dob", 1970, 10);
		builder.addDate("FullDob", 1970, 11, 15);

		// <wwm:Enum name="Smoking" value="GivingUp"/>
		builder.addEnum("Smoke", "SmokingHabits", "GivingUp");
		builder.addEnumList("LeisureActivities", "Activities", "Skiing", "Cycling", "Walking");
		/* FIXME: Add the following
		<wwm:FloatRange name="wantQuality" low="0.5" high="0.99"/>
		 */
	}
}
