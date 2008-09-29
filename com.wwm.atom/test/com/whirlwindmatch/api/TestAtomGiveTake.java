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

public class TestAtomGiveTake extends BaseAtomTest {



	@Override
	protected void addAttrs(EntryDecorator builder) {
		// Item and Id = for now create Id as time value which should be unique enough
		builder.setMetadata("gtItem", privateId);

		// <wwm:String name="FirstName" value="Neale"/>
		builder.addSimpleAttribute("ScreenName", "JUnitTest");
		builder.addSimpleAttribute("SummaryTag", "Item posted at " + DateFormat.getDateInstance().format(new Date()));
		builder.addSimpleAttribute("fmCategory", "ElectricalApplicances");
		builder.addSimpleAttribute("Description", "This is an item inserted via web services.");
	}



}
