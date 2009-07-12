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
package com.wwm.indexer.test;

import java.net.MalformedURLException;

import com.wwm.context.JVMAppListener;
import com.wwm.db.WWMDBProtocolHander;
import com.wwm.indexer.Indexer;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.Record;
import com.wwm.indexer.SearchResult;
import com.wwm.indexer.SearchResults;
import com.wwm.indexer.exceptions.IndexerException;

public class DBIndexerBuildAndQuery {

    Indexer indexer;

    public static void main(String[] args) throws IndexerException, MalformedURLException {
        JVMAppListener.getInstance().preRequest();

        DBIndexerBuildAndQuery t = new DBIndexerBuildAndQuery();

        String xmlPath = args[0];
        String storeUrl = "wwmdb://127.0.0.1/StoreStore";
        if (args.length > 1) {
            storeUrl = args[1];
        }

        JVMAppListener.getInstance().preRequest();

        t.connect(storeUrl, xmlPath);
        t.addData(1000);
        System.out.print("Query Count: false == " + t.boolCountQuery(false) + "\n");
        System.out.print("Query Count: true == " + t.boolCountQuery(true) + "\n");
        System.out.print("Query Count: null == " + t.boolCountQuery(null) + "\n");

        t.floatQuery(60f);
    }

    void connect(String storeUrl, String xmlpath) throws IndexerException, MalformedURLException {
        System.out.print("connect\n");
        IndexerFactory.setCurrentStoreUrl(storeUrl);
        // Use store name as username as we use username as the store
        // in web service.
        String storeName = WWMDBProtocolHander.getAsURL(storeUrl).getPath();
//        AtomFactory.setCredentials(storeName, "dummy");
        indexer = IndexerFactory.getIndexer();
    }

    void addData(int number) throws IndexerException {
        for (int i = 0; i < number; i++) {
            Record r = new Rec(i, "blah");
            boolean gender = (i % 2) == 1;
            r.put("Gender", gender ); // alternate true/false
            r.put("wantGender", !gender); // alternate true/false
            r.put("Age", i); 		// Clearly we're indexing fossils ;)
            indexer.addRecord(r);
        }
    }

    int boolCountQuery(Boolean gender) throws IndexerException {
        Record search = new Rec(-1, ""); // Not actually a rec

        if (gender != null) {
        	search.put("Gender", gender);
        	search.put("wantGender", !gender);
        }
        SearchResults results = indexer.searchRecords(search.getAttributes(), "LiftShare", 2000, 1000, 0.01f);

        //		for (SearchResult result : results.getResults()) {
        //			System.out.println("Record :: " + result.getRecordId());
        //		}

        return results.getResults().size();
    }


    int floatQuery(float age) throws IndexerException {
        Record search = new Rec(-1, ""); // Not actually a rec
        search.put("Age", age);
        SearchResults results = indexer.searchRecords(search.getAttributes(), "SimilarAge", 2000, 1000, 0.01f);

        for (SearchResult result : results.getResults()) {
            System.out.println("Record " + result.getPrivateId() + " =>  Scored " + result.getScore());
        }

        return results.getResults().size();
    }




}
