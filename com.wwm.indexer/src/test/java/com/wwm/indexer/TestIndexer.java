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
package com.wwm.indexer;


import java.net.MalformedURLException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.wwm.context.JVMAppListener;
import com.wwm.db.WWMDBProtocolHander;
import com.wwm.indexer.exceptions.IndexerException;

import static org.junit.Assert.*;

public class TestIndexer {

    static final String xmlpath = "xml/liftshare";
    private static String storeUrl = "wwmdb://127.0.0.1/LiftShareStore";

    static Indexer indexer;

    @BeforeClass // Configure database
    public static void initStore() throws Exception {

        JVMAppListener.getInstance().preRequest();

        // Delete old one

        //		TODO: Set Up database once

        // Configure new one
        // FIXME: Not sure why this is needed.  SyncedADM is working, but without
        // this line things fail reading back record.  Need to check why.
        //		WhirlwindCommon shouldntNeed = new WhirlwindCommon(xmlpath, storeName, host, port);

        //		if (args.length > 2 ) {
        //			storeName = args[2];
        //		}
        connect();
    }


    static void connect() throws IndexerException, MalformedURLException {
        storeUrl = storeUrl + String.valueOf(System.currentTimeMillis());
        System.out.print("connect\n");
        IndexerFactory.setCurrentStoreUrl(storeUrl);
        // Use store name as username as we use username as the store
        // in web service.
        String storeName = WWMDBProtocolHander.getAsURL(storeUrl).getPath();
//        AtomFactory.setCredentials(storeName, "dummy");
        indexer = IndexerFactory.getIndexer();
    }

    @Test
    public void addTwo() throws IndexerException {
        System.out.print("addTwo\n");
        {
            addRec1();
            Record rec = indexer.retrieveRecord("1");

            assertNotNull(rec);
            assertEquals("1", rec.getPrivateId() );
            assertEquals("T1", rec.getTitle());
            assertEquals(Boolean.TRUE, rec.getAttributes().get("Gender") );
            assertEquals(32f, rec.getAttributes().get("Age") );
        }
        {
            addRec2();
            Record rec = indexer.retrieveRecord("2");

            assertNotNull(rec);
            assertEquals("2", rec.getPrivateId() );
            assertEquals("T2", rec.getTitle());
            assertEquals(Boolean.FALSE, rec.getAttributes().get("Gender") );
            assertEquals(16f, rec.getAttributes().get("Age") );
        }
        System.out.print("addTwo Complete\n");
    }


    private void addRec2() throws IndexerException {
        Rec r = new Rec(2, "T2");
        r.put("Gender", false);
        r.put("Age", 16.0f);

        indexer.addRecord(r);
    }


    private void addRec1() throws IndexerException {
        Rec r = new Rec(1, "T1");
        r.put("Gender", true);
        r.put("Age", 32.0f);

        indexer.addRecord(r);
    }

    @Test
    public void updateTwo() throws IndexerException {
        System.out.print("updateTwo\n");

        addRec1();
        Rec r = new Rec(1, "T3");
        r.put("Gender", true);
        r.put("Age", 48.0f);

        indexer.addRecord(r);
        Record rec = indexer.retrieveRecord("1");

        assertNotNull(rec);
        assertEquals("1", rec.getPrivateId() );
        assertEquals("T3", rec.getTitle());
        assertEquals(Boolean.TRUE, rec.getAttributes().get("Gender") );
        assertEquals(48f, rec.getAttributes().get("Age") );

        addRec2();
        r = new Rec(2, "T4");
        r.put("Gender", false);
        r.put("Age", 64.0f);

        indexer.addRecord(r);
        rec = indexer.retrieveRecord("2");

        assertNotNull(rec);
        assertEquals("2", rec.getPrivateId() );
        assertEquals("T4", rec.getTitle());
        assertEquals(Boolean.FALSE, rec.getAttributes().get("Gender") );
        assertEquals(64f, rec.getAttributes().get("Age") );

        System.out.print("updateTwo Complete\n");
    }

}
