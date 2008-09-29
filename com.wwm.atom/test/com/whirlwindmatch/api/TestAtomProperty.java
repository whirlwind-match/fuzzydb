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

import java.util.Date;
import java.util.Map;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.junit.Before;

import com.wwm.atom.client.Config;
import com.wwm.atom.elements.EntryDecorator;
import com.wwm.util.CsvReader;

public class TestAtomProperty extends BaseAtomTest {

    private String csvPath = "/com/whirlwindmatch/api/propertyTestData.csv";


    protected String privateId = "10_50-" + String.valueOf(new Date().getTime()); // generate our own id
    
    private CsvReader reader; 

    protected int count = 1;

    @Before
    public void setupReader() throws Exception {
        reader = new CsvReader(csvPath, true, true);
        reader.setColumn("title", String.class);
        reader.setColumn("area", String.class);
        reader.setColumn("price", Float.class);
        reader.setColumn("bedrooms", Float.class);
        reader.setColumn("receptionRooms", Float.class);
        reader.setColumn("bathrooms", Float.class);
        reader.setColumn("floors", Float.class);
        reader.setColumn("floorArea", Float.class);
        reader.setColumn("landArea", Float.class);
        reader.setColumn("pool", String.class);
        reader.setColumn("parkingSpaces", Float.class);
        reader.setColumn("propertySuitability", String.class);
        reader.setColumn("propertyStatus", String.class);
        reader.setColumn("propertyType", String.class);
    }
    

    @Override
    protected void addAttrs(EntryDecorator builder) throws Exception {
        Map<String, Object> map = reader.readLine();

        // Item and Id = for now create Id as time value which should be unique enough
        builder.setMetadata("property", privateId);

        // NOT NEEDED BUT USEFUL IN UI
        builder.addSimpleAttribute("Ref", "Property Test #" + (count++));
        builder.addSimpleAttribute("Description", (String) map.get("title"));

        // Rob's attrs
        builder.addEnum("area", "areas", (String)map.get("area"));
        builder.addEnum("propertySuitability", "suitabilities", (String)map.get("propertySuitability"));
        builder.addEnum("propertyStatus", "statuses", (String)map.get("propertyStatus"));
        builder.addEnum("propertyType", "types", (String)map.get("propertyType"));
        builder.addFloat("price", (Float) map.get("price"));
        builder.addFloat("bedrooms", (Float) map.get("bedrooms"));
        builder.addFloat("floorArea", (Float) map.get("floorArea"));
        builder.addBoolean("pool", (String) map.get("pool"));
    }
    
    @Override
    protected void addUpdateAttrs(EntryDecorator builder) {
        // Item and Id = for now create Id as time value which should be unique enough
        builder.setMetadata("property", privateId);
        builder.addEnum("Island", "Islands", "Cuba");
    }
    
	@Override
	protected String getFeedQueryString() {
		return "?matchStyle=propertyDefault&area=Antigua&price=250000&numResults=100";
//		return "?matchStyle=propertyDefault&area=Antigua&price=250000&laundryArea=false&numResults=100";
	}

    
    @Override
	protected void configureScorers(){
        IRI scorerLocation = Config.writeScorerConfig( "Scorer: PropertyDefault", "/com/whirlwindmatch/api/propertyScorerConfig.xml" );

        assert scorerLocation != null;
    }
    
    
	@Override
	protected void validateTestGetFeedResults(Document<Feed> docGot) {
		// nowt for now
	}
}
