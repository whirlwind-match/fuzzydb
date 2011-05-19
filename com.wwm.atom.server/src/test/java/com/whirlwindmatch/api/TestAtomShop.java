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
import java.util.Map;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.junit.Before;

import com.wwm.atom.client.Config;
import com.wwm.atom.elements.EntryDecorator;
import com.wwm.util.CsvReader;

public class TestAtomShop extends BaseAtomTest {


	private static final float defaultOfferValue = 0f;  // 0.8f

	private final String scorerConfigLocation = "classpath:/shopScorerConfig.xml";
    private final String csvPath = "/shopPostcodes.csv";


    protected String privateId = "10_50-" + String.valueOf(new Date().getTime()); // generate our own id
    private CsvReader reader;

    protected int count = 1;

    @Before
    public void setupReader() throws Exception {
        reader = new CsvReader(csvPath, true, true);
        reader.setColumn("Postcode", String.class);
    }
    
    
    @Override
    protected Entry getNextEntry() throws Exception {
        Entry entry = makeCreateEntry();
    	return entry;
    }
    
    private static final float defaultGeographicSearchDistance = 50f;  

    @Override
    protected void addAttrs(EntryDecorator builder) throws Exception {
        Map<String, Object> map = reader.readLine();
        String postcode = (String)map.get("Postcode");
        // <wwm:Location name="location" {ukPostcode="CB4"} {lat="53.1" lon="0.1"} />
        if (postcode != null) {
        	builder.addSimpleAttribute("PostCode", postcode ); // getRandomFullPostcode());
        	builder.addFloat("LocationRange", defaultGeographicSearchDistance);
        }


        // Item and Id = for now create Id as time value which should be unique enough
        builder.setMetadata("trader", privateId);

        // NOT NEEDED BUT USEFUL IN UI
        builder.addSimpleAttribute("ScreenName", "Wedge Test #" + (count++));
        builder.addSimpleAttribute("SummaryTag", "Item posted at " + DateFormat.getDateInstance().format(new Date()));

        // Rob's attrs
        builder.addFloat("OfferValue", defaultOfferValue);
        // builder.addLocationPreference("LocationRange", 50f, "PostCode");
    }
    
	@Override
	protected void addUpdateAttrs(EntryDecorator builder) {
        builder.setMetadata("trader_modified", privateId);
		// <wwm:Location name="location" {ukPostcode="CB4"} {lat="53.1" lon="0.1"} />
		builder.addSimpleAttribute("PostCode", "CB4 2QW"); // fixme: was random!
	}

    
	@Override
	protected String getFeedQueryString() {
		return "?PostCode=SW17+7TG&matchStyle=shopDefault&numResults=100";
	}

    @Override
	protected void configureScorers(){
        IRI scorerLocation = Config.writeScorerConfig( "Scorer: Distance <5 miles", scorerConfigLocation );

        assert scorerLocation != null;
    }

}
