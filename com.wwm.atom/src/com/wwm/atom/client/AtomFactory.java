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
package com.wwm.atom.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.util.BaseRequestEntity;

import sun.misc.BASE64Encoder;

import com.wwm.atom.elements.AbderaElementFactory;
import com.wwm.atom.server.BadRequestException;
import com.wwm.atom.util.AtomUtils;

/**
 * Factory for getting the stuff the end user needs.
 * 
 * FIXME: Make into a proxy for Session or Request stuff... and put things like Username and Password in the correct
 * context.
 * 
 * FIXME: This should probably eliminate the need for the end user to use Abdera APIs at all.
 * Maybe.. maybe not.
 * 
 * What it should DEFINITELY do, is to provide pre-configured instances (e.g. add our own element factory)
 */
public class AtomFactory {

    private static final String feedUriBase = "http://testmachine.local:9090/fuzz/feed"; 


    static private String username;
    static private String password = "not used";

    static private final Abdera abdera = new Abdera();
    static private AbderaClient client = new AbderaClient(abdera);

    static public Entry createBaseEntry(String author, String title, String linkUrl) {
        Factory factory = getFactory();

        Entry entry = factory.newEntry();

        String id = FOMHelper.generateUuid();
        entry.setId(id);
        entry.setUpdated(new Date());
        entry.setPublished(new Date());
        entry.addAuthor(author);
        entry.setTitle(title);
        entry.addLink(linkUrl);
        //	    entry.addLink("","self");

        // Can have content, as below or not to be a valid entry.
        //		entry.setContentAsXhtml(
        //		  "<p>This can go in an atom feed, I think... Will have to try to a blogger account...</p>");

        Document<Entry> doc = entry.getDocument();

        // We don't (currently) support SLUG
        String slug = "Suggested-reference-which-we-will-probably-ignore";
        doc.setSlug(slug);

        return entry;
    }

    public static Factory getFactory() {
        Factory factory = abdera.getFactory();
        factory.registerExtension( new AbderaElementFactory() );
        return factory;
    }

    public static AbderaClient getClient() {
        return client;
    }

    public static Abdera getAbdera() {
        return abdera;
    }


    /**
     * temporary: need to support using our ContextUtils stuff.
     */
    static public void setCredentials(String userName, String password){
        username = userName;
        AtomFactory.password = password;
    }


    static public RequestOptions getOptions() throws UnsupportedEncodingException {
        RequestOptions options = AtomFactory.getClient().getDefaultRequestOptions();
        String usernamePassword = username + ":" + password;
        String auth = new BASE64Encoder().encode(usernamePassword.getBytes("utf-8"));
        options.setAuthorization("Basic " + auth);
        //      options.setContentType("application/atom+xml;type=entry");
        return options;
    }

    public static String getFeedUriBase() {
        return feedUriBase;
    }

    static public IRI create(Entry entry) throws UnsupportedEncodingException, IOException, BadRequestException {

        RequestOptions options = getOptions();
        BaseRequestEntity bre = new BaseRequestEntity(entry, false);
        ClientResponse response = getClient().post(AtomFactory.getFeedUriBase(), bre, options);

        try {
			System.out.println(response.getStatusText());
			// Look at what we got back from the POST
			try {
				AtomUtils.logHeaders(response); // should get postcode issue
				if ("BadRequestException".equals(response.getHeader("Fuzz-ExceptionClass")) ) {
					throw new BadRequestException( response.getHeader("Fuzz-Exception"));
				}
				AtomUtils.prettyPrint(response);
			} catch (ParseException e) {
				// Ignore this as we can't test content length as it's not set.  We have to try parse and ignore it
			}
			IRI location = response.getLocation();
			assert location != null && location.toString().length() > 0;
			return location;
		} finally {
			response.release();
		}
    }



}
