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
package com.wwm.abdera.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterOptions;
import org.apache.abdera.xpath.XPath;


/**
 * Some random utility functions for dumping Atom data to stdout etc.
 */
public class AtomUtils {

    static public void printSummary(Abdera abdera, Document<? extends Entry> doc) throws IOException {
        XPath xpath = abdera.getXPath();

        System.out.println(xpath.valueOf("/a:feed/a:id", doc));
        System.out.println(xpath.valueOf("/a:feed/@xml:base", doc));
        System.out.println(xpath.valueOf("/a:feed/@xml:lang", doc));
        System.out.println(xpath.valueOf("/a:feed/a:title", doc));
        System.out.println(xpath.valueOf("/a:feed/a:title/@type", doc));
        System.out.println(xpath.valueOf("/a:feed/a:updated", doc));
        System.out.println(xpath.valueOf("/a:feed/a:link[not(@rel)]/@href", doc));
        System.out.println(xpath.valueOf("/a:feed/a:link[@rel='self']/@href", doc));

        System.out.println(xpath.valueOf("count(//a:entry)", doc) + " entry");
        System.out.println(xpath.valueOf("name(//a:entry/ancestor::*)", doc));

        Map<String, String> namespaces = xpath.getDefaultNamespaces();
        namespaces.putAll(doc.getRoot().getNamespaces());

        AtomUtils.printExtensions( abdera, doc );

        //		Content content = doc.getRoot().getContentElement();
        //		namespaces = content.getNamespaces();
        //		namespaces.put("x", "http://www.w3.org/1999/xhtml");
        //		Div div = (Div) xpath.selectSingleNode("//x:div", content, namespaces);
        //		System.out.println(xpath.valueOf("namespace-uri()", div));
        //		System.out.println(xpath.valueOf("x:p", div, namespaces));
    }

    static public void printExtensions(Abdera abdera, Document<? extends Entry> doc) throws IOException {
        Entry root = doc.getRoot();
        List<Element> extensions = root.getExtensions();
        System.out.println(extensions);

        //		TODO: Check extensions in output.. (was: Build extension factory impl and register it
        //			See http://incubator.apache.org/abdera/docs/developers.html


        @SuppressWarnings("unused")
        XPath xpath = abdera.getXPath();
        for (Element element : extensions) {
            @SuppressWarnings("unused")
            Map<String, String> namespaces = element.getNamespaces();
            //			System.out.println(xpath.valueOf("//wwm:attribute/@name", element, namespaces));
            //			System.out.println(xpath.valueOf("//wwm:attribute/@value", element, namespaces));

            //			MultiEnumElement attr = (MultiEnumElement) element;
            System.out.println( element.toString() );
        }
    }

    
    static public void prettyPrint(Document<? extends Element> doc) {
        //=== Write it prettily! ===
        Abdera abdera = Abdera.getInstance();
        try {
            Writer writer = abdera.getWriterFactory().getWriter("prettyxml");
            WriterOptions writeOptions = writer.getDefaultWriterOptions();
            writeOptions.setCharset("UTF-8");
            writer.writeTo(doc, System.out, writeOptions);
        } catch (Exception e) { e.printStackTrace(); } // FIXME: Document this exception
    }

    static public void logHeaders(ClientResponse response) {
    	System.out.println("-- Response headers --");
        for (String key: response.getHeaderNames()) {
            System.out.println(key + " : " + response.getHeader(key).toString());
        }
    }

    /**
     * Print only if content was received.
     */
	public static void prettyPrint(ClientResponse response) {

		if (response.getContentLength() > 0){
			Document<Entry> docGot;
			docGot = response.getDocument();
			prettyPrint(docGot);
		}
	}
}
