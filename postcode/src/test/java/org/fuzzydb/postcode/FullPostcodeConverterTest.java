package org.fuzzydb.postcode;
/******************************************************************************
 * Copyright (c) 2005-2009 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/


import org.fuzzydb.core.Settings;
import org.fuzzydb.postcode.PostcodeConvertor;
import org.fuzzydb.postcode.RandomPostcodeGenerator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

import com.wwm.geo.GeoInformation;

/**
 * These tests assume the postcode data has been built and installed into the correct location.
 * 
 * They test that full postcode resolve sufficiently accurately
 */
@Ignore("Re-instate when have full resolution postcode service available")
public class FullPostcodeConverterTest {
    private PostcodeConvertor convertor;

    @Before
    public void setUp() throws Exception {

        Settings.getInstance().setPostcodeRoot("data");
        convertor = new PostcodeConvertor();
    }

    @Test
    public void testFullAb() {
        GeoInformation r = convertor.lookupFull("ab101af");
        Assert.assertNotNull(r);
        Assert.assertEquals(57.15, r.getLatitude(), 0.05);
        Assert.assertEquals(-2.05, r.getLongitude(), 0.01);
    }

    @Test
    public void testFullCB45RJ() {
        GeoInformation r = convertor.lookupFull("CB4 5RJ");
        Assert.assertNotNull(r);
        Assert.assertEquals(52.30, r.getLatitude(), 0.05);
        Assert.assertEquals(-0.005, r.getLongitude(), 0.005);
    }

    @Test
    public void testFullCB42QW() {
        GeoInformation r = convertor.lookupFull("CB4 2QW");
        Assert.assertNotNull(r);
        Assert.assertEquals(52.30, r.getLatitude(), 0.05);
        Assert.assertEquals(-0.005, r.getLongitude(), 0.005);
    }

    @Test
    public void testFullBL09BX() {
        GeoInformation r = convertor.lookupFull("BL09BX");
        Assert.assertNotNull(r);
		Assert.assertTrue (r.getLatitude() < 52.35);
		Assert.assertTrue (r.getLatitude() > 52.25);
		Assert.assertTrue (r.getLongitude() < -0.00);
		Assert.assertTrue (r.getLatitude() > -0.01);
    }

    @Test
    public void testFullGL170LS() {
        GeoInformation r = convertor.lookupFull("GL170LS");
        Assert.assertNotNull(r);
		Assert.assertTrue (r.getLatitude() < 52.35);
		Assert.assertTrue (r.getLatitude() > 52.25);
		Assert.assertTrue (r.getLongitude() < -0.00);
		Assert.assertTrue (r.getLatitude() > -0.01);
    }

    @Test
    public void testFullW93PJ() {
        GeoInformation r = convertor.lookupFull("W93PJ");
        Assert.assertNotNull(r);
		Assert.assertTrue (r.getLatitude() < 52.35);
		Assert.assertTrue (r.getLatitude() > 52.25);
		Assert.assertTrue (r.getLongitude() < -0.00);
		Assert.assertTrue (r.getLatitude() > -0.01);
    }

    @Test
    public void testFullAbSpaced() {
        GeoInformation r = convertor.lookupFull("AB10 1AF");
        assertAB101AF(r);
    }

    private void assertAB101AF(GeoInformation r) {
        Assert.assertNotNull(r);
        Assert.assertEquals(57.15, r.getLatitude(), 0.01);
        Assert.assertEquals(-2.1, r.getLongitude(), 0.005);
    }

    @Test
    public void testFullAbSpacedCaps() {
        GeoInformation r = convertor.lookupFull("ab 10 1Af  ");
        assertAB101AF(r);
    }

    @Test
    public void testFullInvalid() {
        GeoInformation r = convertor.lookupFull("FOOBAR");
        Assert.assertNull(r);
    }

    @Test
    public void testFullShort() {
        GeoInformation r = convertor.lookupFull("g");
        Assert.assertNull(r);
    }

    @Test
    public void testFullEmpty() {
        GeoInformation r = convertor.lookupFull("");
        Assert.assertNull(r);
    }

    @Test
    public void testFullPerf() {
        RandomPostcodeGenerator gen = new RandomPostcodeGenerator();
        final int count = 1000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            GeoInformation r = convertor.lookupFull(gen.nextFullPostcode());
            Assert.assertNotNull(r);
        }
        long dur = System.currentTimeMillis() - start;
        System.out.println("Full lookup speed: " + ((float)dur/count) + "ms each");
        System.gc();
        System.gc();
        System.out.println("Mem used: " + (float)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024) + "MB");
    }

    // Doesn't make sense as Db connection is in another thread
    //	public void testFullDisconnect(), DbCommandFailedException, DbNetworkErrorException {
    //		client.disconnect();
    //		boolean threw = false;
    //		PostcodeResult r = null;
    //		try {
    //			r = convertor.lookupFull("AB101AF");
    //			try { Thread.sleep(1000); } catch (InterruptedException e) { } 
    //			r = convertor.lookupFull("AB101AF");
    //			try { Thread.sleep(1000); } catch (InterruptedException e) { } 
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
