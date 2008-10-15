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


import junit.framework.Assert;
import junit.framework.TestCase;

import com.wwm.db.dao.Db2ObjectDAO;
import com.wwm.db.dao.SimpleDAO;
import com.wwm.postcode.PostcodeConvertor;
import com.wwm.postcode.PostcodeResult;
import com.wwm.postcode.RandomPostcodeGenerator;
import com.wwm.postcode.PostcodeConvertor.LostDbConnection;

/**
 * These tests assume the postcode data has been built and installed into the correct location.
 */
public class TestPostcodeConvertor extends TestCase {
    //	private Database database;
    private PostcodeConvertor convertor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //		database = new Database();
        //		database.listen();
        SimpleDAO dao = new Db2ObjectDAO("wwmdb:/postcode");

        convertor = new PostcodeConvertor(dao);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //		database.closeDown();
    }

    public void testJibbleSimple() {
        PostcodeResult r = convertor.lookupShort("CB4");
        assertCB4(r);
    }

    public void testJibbleSimpleSpaced() {
        PostcodeResult r = convertor.lookupShort("CB 4");
        assertCB4(r);
    }

    private void assertCB4(PostcodeResult r) {
        Assert.assertNotNull(r);
        Assert.assertTrue (r.getLatitude() < 52.35);
        Assert.assertTrue (r.getLatitude() > 52.2);
        Assert.assertTrue (r.getLongitude() < 0.2);
        Assert.assertTrue (r.getLatitude() > -0.2);
    }

    public void testJibbleSimpleCased() {
        PostcodeResult r = convertor.lookupShort("cb4");
        assertCB4(r);
    }

    public void testJibbleSimpleCasedSpaced() {
        PostcodeResult r = convertor.lookupShort(" c B 4 ");
        assertCB4(r);
    }

    public void testJibbleInvalid() {
        PostcodeResult r = convertor.lookupShort("FOOBAR");
        Assert.assertNull(r);
    }

    public void testJibbleEmpty() {
        PostcodeResult r = convertor.lookupShort("");
        Assert.assertNull(r);
    }

    public void testFullAb() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("ab101af");
        Assert.assertNotNull(r);
        Assert.assertEquals(r.getLatitude(), 57.15, 0.05);
        Assert.assertEquals(r.getLongitude(), -2.05, 0.01);
    }

    public void testFullCB45RJ() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("CB4 5RJ");
        Assert.assertNotNull(r);
        Assert.assertEquals(r.getLatitude(), 52.30, 0.05);
        Assert.assertTrue (r.getLatitude() > 52.25);
        Assert.assertTrue (r.getLongitude() < -0.00);
        Assert.assertTrue (r.getLongitude() > -0.01);
    }

    public void testFullCB42QW() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("CB4 2QW");
        Assert.assertNotNull(r);
        Assert.assertEquals(r.getLatitude(), 52.30, 0.05);
        //		Assert.assertTrue (r.getLongitude() < -0.00);
        //		Assert.assertTrue (r.getLatitude() > -0.01);
    }

    public void testFullBL09BX() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("BL09BX");
        Assert.assertNotNull(r);
        //		Assert.assertTrue (r.getLatitude() < 52.35);
        //		Assert.assertTrue (r.getLatitude() > 52.25);
        //		Assert.assertTrue (r.getLongitude() < -0.00);
        //		Assert.assertTrue (r.getLatitude() > -0.01);
    }

    public void testFullGL170LS() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("GL170LS");
        Assert.assertNotNull(r);
        //		Assert.assertTrue (r.getLatitude() < 52.35);
        //		Assert.assertTrue (r.getLatitude() > 52.25);
        //		Assert.assertTrue (r.getLongitude() < -0.00);
        //		Assert.assertTrue (r.getLatitude() > -0.01);
    }

    public void testFullW93PJ() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("W93PJ");
        Assert.assertNotNull(r);
        //		Assert.assertTrue (r.getLatitude() < 52.35);
        //		Assert.assertTrue (r.getLatitude() > 52.25);
        //		Assert.assertTrue (r.getLongitude() < -0.00);
        //		Assert.assertTrue (r.getLatitude() > -0.01);
    }

    public void testFullAbSpaced() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("AB10 1AF");
        assertAB101AF(r);
    }

    private void assertAB101AF(PostcodeResult r) {
        Assert.assertNotNull(r);
        Assert.assertTrue (r.getLatitude() < 57.16);
        Assert.assertTrue (r.getLatitude() > 57.14);
        Assert.assertTrue (r.getLongitude() < -2.095);
        Assert.assertTrue (r.getLatitude() > -2.15);
    }

    public void testFullAbSpacedCaps() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("ab 10 1Af  ");
        assertAB101AF(r);
    }

    public void testFullInvalid() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("FOOBAR");
        Assert.assertNull(r);
    }

    public void testFullShort() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("g");
        Assert.assertNull(r);
    }

    public void testFullEmpty() throws LostDbConnection {
        PostcodeResult r = convertor.lookupFull("");
        Assert.assertNull(r);
    }

    public void testFullPerf() throws LostDbConnection {
        RandomPostcodeGenerator gen = new RandomPostcodeGenerator();
        final int count = 1000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            PostcodeResult r = convertor.lookupFull(gen.nextFullPostcode());
            Assert.assertNotNull(r);
        }
        long dur = System.currentTimeMillis() - start;
        System.out.println("Full lookup speed: " + ((float)dur/count) + "ms each");
        System.gc();
        System.gc();
        System.out.println("Mem used: " + (float)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024) + "MB");
    }

    // Doesn't make sense as Db connection is in another thread
    //	public void testFullDisconnect() throws LostDbConnection, DbCommandFailedException, DbNetworkErrorException {
    //		client.disconnect();
    //		boolean threw = false;
    //		PostcodeResult r = null;
    //		try {
    //			r = convertor.lookupFull("AB101AF");
    //			try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); } // FIXME: Document this exception
    //			r = convertor.lookupFull("AB101AF");
    //			try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); } // FIXME: Document this exception
    //			r = convertor.lookupFull("AB101AF");
    //		} catch (LostDbConnection e) {
    //			threw = true;
    //		}
    //		Assert.assertTrue(threw);
    //		client = new Client();
    //		client.connect();
    //		SimpleDAO dao = new Db1ObjectDAO(client, "postcode");
    //		convertor.setDao(dao);
    //		r = convertor.lookupFull("AB101AF");
    //		Assert.assertNotNull(r);
    //	}

}
