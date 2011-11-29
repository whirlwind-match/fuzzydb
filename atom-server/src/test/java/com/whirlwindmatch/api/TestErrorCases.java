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

import org.apache.abdera.model.Entry;

import com.wwm.atom.elements.EntryDecorator;

public class TestErrorCases extends BaseAtomTest {

    protected String privateId = "10_50-" + String.valueOf(new Date().getTime()); // generate our own id

    
    @Override
    protected Entry getNextEntry() throws Exception {
        Entry entry = makeCreateEntry();
    	return entry;
    }
    
    private static final float defaultRange = 50f;  

    @Override
    protected void addAttrs(EntryDecorator builder) throws Exception {
        
    	builder.addFloat("LocationRange", defaultRange);


        // Item and Id = for now create Id as time value which should be unique enough
        builder.setMetadata("errorContent", privateId);

        // NOT NEEDED BUT USEFUL IN UI
        builder.addSimpleAttribute("ScreenName", "Error Test");
        builder.addSimpleAttribute("SummaryTag", "Item posted at " + DateFormat.getDateInstance().format(new Date()));

    }
    
	@Override
	protected void addUpdateAttrs(EntryDecorator builder) {
        builder.setMetadata("errorContent", privateId);
		// <wwm:Location name="location" {ukPostcode="CB4"} {lat="53.1" lon="0.1"} />
		builder.addSimpleAttribute("PostCode", "CB4 2QW"); // fixme: was random!
	}

    
	@Override
	protected String getFeedQueryString() {
		// Query using stuff we've not defined
		return "?PostCode=SW17+7TG&matchStyle=shopDefault&numResults=100";
	}

    @Override
	protected void configureScorers(){
    	// Deliberately don't config scorers
//        IRI scorerLocation = Config.writeScorerConfig( "Scorer: Distance <5 miles", "/blahConfig.xml" );
//        assert scorerLocation != null;
    }

}
