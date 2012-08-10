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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.util.BaseRequestEntity;
import org.fuzzydb.core.LogFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import com.wwm.abdera.util.AtomUtils;
import com.wwm.abdera.util.server.BadRequestException;
import com.wwm.atom.client.AtomFactory;
import com.wwm.atom.client.Config;
import com.wwm.atom.elements.AbderaElementFactory;
import com.wwm.atom.elements.EntryDecorator;
import com.wwm.atom.impl.HttpServer;
import com.wwm.atom.impl.HttpServerFactory;
import com.wwm.util.NanoTimer;

public abstract class BaseAtomTest {
	
	static private final Logger log = LogFactory.getLogger(BaseAtomTest.class);

    protected AbderaClient client = AtomFactory.getClient();

    protected String privateId = "10_50-" + String.valueOf(new Date().getTime()); // generate our own id

    static private HttpServer server = HttpServerFactory.getInstance();
    
    
    @BeforeClass
    static public void startServer() throws Exception {
    	server.start();
    }
    
    @AfterClass
    static public void stopServer() throws Exception {
    	server.stop();
    }
    
    
    // Static so we don't have huge db, as there is an instance of this test class per test method.
    //	static protected RandomPostcodeGenerator gen = new RandomPostcodeGenerator();

    @Test
    public void testCreateGetUpdateDelete() throws Exception {

        IRI location;
        {
            Entry entry = makeCreateEntry();
            location = AtomFactory.create(entry);
            assertNotNull(location);
            System.out.println( "\n** Created entry. Location = " + location + "\n" );
        }

        // This is NOT expected to be the case: the server gets the ultimate say on
        // location (which in our case will be urlBase/ourInternalId - which might be the ref that client suggests in ID
        //		assertEquals(uriBase + "/" + id, location.toString());

        //==== GET the entry ====
        getCheckAndPrint(location);

        // UDPATE
        Entry entry = makeUpdateEntry();
        update(entry, location);

        //==== GET the entry ====
        getCheckAndPrint(location);

        // DELETE
        delete(location);


        //		// TODO: Test we got back what we want
        //		{
        //			ClientResponse response = client.get(feedUriBase + "/foobar");
        //			assertEquals(404, response.getStatus());
        //		}
    }


    @Test
    public void testCreateMany() throws Exception {
        NanoTimer t = new NanoTimer();
        @SuppressWarnings("unused")
        IRI location;
        int max = 600;
        int i = 0;
        try {
            for (i = 0; i < max; i++) {
                Entry entry = getNextEntry();
                try {
                	location = AtomFactory.create(entry);
                } catch (BadRequestException e) {
                	// rethrow the exception if it isn't our expected failed postcode lookup
                	if (!e.getMessage().contains("Unable to lookup postcode")){
                		throw e;
                	}
                }
            }
        } catch (EOFException e) {
            // finished file
        }
        float time = t.getMillis();
        System.out.println("======= Create " + i + " took: " + time / 1000f + " seconds");
        assertTrue((time / 1000f) + " < " + i + " seconds", time < i * 1000 );
    }

    /**
     * Get next entry for testCreateMany.
     * This can be overridden to input test data from CSV file
     */
	protected Entry getNextEntry() throws Exception {
		Entry entry = makeCreateEntry();
		return entry;
	}

    // POST /atom/feed -> createEntry
    // PUT /atom/feed/id -> updateEntry
    // POST /atom/feed/id -> postEntry
    //

    @Test
    public void testGetFeed() throws Exception {

        // Ensure we have at least 1 result
        @SuppressWarnings("unused")
        IRI location;
        {
            Entry entry = makeCreateEntry();
            location = AtomFactory.create(entry);
        }

        ClientResponse response;
        response = client.get(AtomFactory.getFeedUriBase() + getFeedQueryString(), AtomFactory.getOptions() );
        if (log.isTraceEnabled()){
	        AtomUtils.logHeaders(response);
	        AtomUtils.prettyPrint(response);
        }
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());


        Document<Feed> docGot = response.getDocument();
        validateTestGetFeedResults(docGot);
        response.release();
    }


	protected void validateTestGetFeedResults(Document<Feed> docGot) {
		assertAtLeastOneResultWithDistance(docGot);
	}


	protected String getFeedQueryString() {
		return "?PostCode=CB2+3LL&matchStyle=shopDefault&numResults=100";
	}


    private void assertAtLeastOneResultWithDistance(Document<Feed> docGot) {
        List<Entry> entries = docGot.getRoot().getEntries();
        assertTrue("Should return a result", entries.size() > 0);
        Entry first = entries.get(0);
        assertHasDistance(first);
    }


    @Test
    public void testGetFeedInvalidPostcode() throws Exception {

        // Ensure we have at least 1 result
        @SuppressWarnings("unused")
        IRI location;
        {
            Entry entry = makeCreateEntry();
            location = AtomFactory.create(entry);
        }

        ClientResponse response;
        response = client.get(AtomFactory.getFeedUriBase() + "?PostCode=CB99+3LL&matchStyle=shopDefault&numResults=100", AtomFactory.getOptions() );
        AtomUtils.logHeaders(response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertNotNull(response.getHeader("Fuzz-Exception"));
        response.release();
    }


    //	private String getRandomFullPostcode() {
    //		return gen.nextFullPostcode();
    //	}

    @Test
    public void testGetFeedWithRange() throws Exception {

        // Ensure we have at least 1 result
        @SuppressWarnings("unused")
        IRI location;
        {
            Entry entry = makeCreateEntry();
            location = AtomFactory.create(entry);
        }



        ClientResponse response;
        response = client.get(AtomFactory.getFeedUriBase() + "?PostCode=CB2+3LL&matchStyle=shopDefault&LocationRange=30&numResults=100", AtomFactory.getOptions() );
        if (log.isTraceEnabled()){
        	AtomUtils.logHeaders(response);
        	AtomUtils.prettyPrint(response);
        }
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        Document<Feed> docGot = response.getDocument();
        validateTestGetFeedResults(docGot);
        response.release();

    }

    @Test
    public void testGetInvalid() throws IOException {
        ClientResponse response;
        response = client.get(AtomFactory.getFeedUriBase() + "/not-known-abcxyz", AtomFactory.getOptions() );

        AtomUtils.logHeaders(response);

        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertNotNull(response.getHeader("Fuzz-Error"));
        response.release();
    }


    private void delete(IRI location)throws UnsupportedEncodingException {
        ClientResponse response = client.delete(location.toString(), AtomFactory.getOptions());
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        response.release();
    }


    private void getCheckAndPrint(IRI location) throws IOException {
        ClientResponse response;
        response = client.get(location.toString(), AtomFactory.getOptions() );
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        Document<Entry> docGot = response.getDocument();
        AtomUtils.prettyPrint(docGot);
        //		printSummary(abdera, docGot);

        @SuppressWarnings("unused")
        Entry entryGot = docGot.getRoot();
        response.release();
    }


    private Entry makeUpdateEntry() throws IOException {
        Entry entry = AtomFactory.createBaseEntry("Neale", "Data record", "http://localhost/digitalburn/tools/cms/site/company/10/");
        {
            EntryDecorator builder = new EntryDecorator( entry );
            addUpdateAttrs(builder);
            AtomUtils.prettyPrint( entry.getDocument() );
        }
        return entry;
    }


    protected void addUpdateAttrs(EntryDecorator builder) {
        builder.setMetadata("mmmm", privateId);
		builder.addSimpleAttribute("Xxx", "It got updated");
	}


    private void update(Entry entry, IRI location)throws UnsupportedEncodingException {

        BaseRequestEntity bre = new BaseRequestEntity(entry, false);
        ClientResponse response = client.put(location.toString(), bre, AtomFactory.getOptions() );
        String header = response.getHeader("Content-Length");
        assertNotNull(header);
        long length = response.getContentLength();
        assertEquals(0, length);
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        response.release();
    }

    /**
     * Add attributes that are specific to the service being tested (e.g. those for
     * Wedge, or GiveTake or Dating.
     * @param builder
     * @throws Exception 
     */
    abstract protected void addAttrs(EntryDecorator builder) throws Exception;


    protected Entry makeCreateEntry() throws Exception {
        // update private ID which is shared across some tests
        privateId = String.valueOf(new Date().getTime());
        Entry entry = AtomFactory.createBaseEntry("Neale", "Data record", "http://localhost/digitalburn/tools/cms/site/company/10/");
        {
            EntryDecorator builder = new EntryDecorator( entry );
            addAttrs(builder);
        }
        return entry;
    }


    protected void assertHasDistance(Entry first) {
        List<Element> extensions = first.getExtensions(AbderaElementFactory.NS);
        boolean found = false;
        for (Element element : extensions) {
            if (element.getAttributeValue("name") != null
                    && element.getAttributeValue("name").equals("Distance")) {
                return;
            }
        }
        assertTrue("Must be an element named Distance", found);
    }



    @Before
    public void setUp() throws Exception {
        //		client.setMaxConnectionsPerHost(20);  // HACK to get around problem where HTTPCLient is not releasing/reusing connections...
        // set up to generate objects to recognise our data
        // client.setProxy("localhost", 1080); // for when TPTP HTTP Recorder works !  FIXME: This should be injected into Settings

        Config.setClassForLoadingResources(getClass()); // should be same as above!
        setCredentials();
        configureScorers();
    }

    protected void configureScorers(){
        IRI scorerLocation = Config.writeScorerConfig( "Scorer: Distance <5 miles", "classpath:/shopScorerConfig.xml" );

        assert scorerLocation != null;
    }

    protected void setCredentials(){
    	// Username is used to select the Store, so vary it according to the test.
    	String userName = this.getClass().getSimpleName();
    	AtomFactory.setCredentials(userName, "don't care");
    }
    

    
}
