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
import com.wwm.indexer.Indexer;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.Record;
import com.wwm.indexer.exceptions.IndexerException;

public class DBIndexerTester {

    private Indexer indexer;

    public static void main(String[] args) throws IndexerException, MalformedURLException {
        JVMAppListener.getInstance().preRequest();

        DBIndexerTester t = new DBIndexerTester();

        String xmlPath = args[0];
        String storeUrl = (args.length > 1) ? args[1] : "wwmdb:/IndexerTestStore";

        JVMAppListener.getInstance().preRequest();

        t.connect(storeUrl, xmlPath);
        t.addTwo();
        t.updateTwo();
    }

    void connect(String storeUrl, String xmlpath) throws IndexerException, MalformedURLException {
        System.out.print("connect\n");
        IndexerFactory.setCurrentStoreUrl(storeUrl);
        // Use store name as username as we use username as the store
        // in web service.
//        String storeName = WWMDBProtocolHander.getAsURL(storeUrl).getPath();
//        AtomFactory.setCredentials(storeName, "dummy");
        indexer = IndexerFactory.getIndexer();
    }

    void addTwo() throws IndexerException {
        System.out.print("addTwo\n");
        Rec r = new Rec(1, "T1");
        r.put("Gender", true);
        r.put("Age", 32.0f);
        indexer.addRecord(r);
        Record rec = indexer.retrieveRecord("1");
        assert rec != null;
        assert rec.getPrivateId().equals("1");
        assert rec.getTitle().equals("T1");
        assert rec.getBoolean("Gender") == true;
        assert rec.getFloat("Age") == 16.0f;

        r = new Rec(2, "T2");
        indexer.addRecord(r);
        r.put("Gender", false);
        r.put("Age", 16.0f);

        rec = indexer.retrieveRecord("2");
        assert rec != null;
        assert rec.getPrivateId().equals("2");
        assert rec.getTitle().equals("T2");
        assert rec.getBoolean("Gender") == false;
        assert rec.getFloat("Age") == 32.0f;
        System.out.print("addTwo Complete\n");
    }

    void updateTwo() throws IndexerException {
        System.out.print("updateTwo\n");
        Rec r = new Rec(1, "T3");
        indexer.addRecord(r);
        r.put("Gender", true);
        r.put("Age", 48.0f);
        Record rec = indexer.retrieveRecord("1");
        assert rec != null;
        assert rec.getPrivateId().equals("1");
        assert rec.getTitle().equals("T3");
        assert rec.getBoolean("Gender") == true;
        assert rec.getFloat("Age") == 48.0f;

        r = new Rec(2, "T4");
        indexer.addRecord(r);
        r.put("Gender", false);
        r.put("Age", 64.0f);

        rec = indexer.retrieveRecord("2");
        assert rec != null;
        assert rec.getPrivateId().equals("2");
        assert rec.getTitle().equals("T4");
        assert rec.getBoolean("Gender") == false;
        assert rec.getFloat("Age") == 64.0f;
        System.out.print("updateTwo Complete\n");
    }

}
