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
package com.wwm.atom.impl;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;

import com.thoughtworks.xstream.XStream;
import com.wwm.atom.server.BadRequestException;
import com.wwm.atom.server.NotFoundException;
import com.wwm.attrs.ManualIndexStrategy;
import com.wwm.attrs.WWConfigHelper;
import com.wwm.db.Store;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.internal.XStreamHelper;

public class IndexConfigEntryHandler implements TypeHandler {

    public void createEntry(RequestContext request, Document<Entry> doc) throws Exception {

        XStream xs = XStreamHelper.getIndexConfigXStream();
        xs.setClassLoader( this.getClass().getClassLoader() ); // We need it to use our classLoader, as it's own bundle won't help it :)

        String content = doc.getRoot().getContent();
        ManualIndexStrategy strategy = (ManualIndexStrategy) xs.fromXML(content);

        Store store = IndexerFactory.getCurrentStore();
        WWConfigHelper.updateIndexConfig( store, strategy );

        Entry entry = doc.getRoot();
        entry.addLink(IndexerFactory.baseFeedUrl + "/" + entry.getId().toString(), "edit");

    }

    public void deleteEntry(RequestContext context, String privateRecordId) throws NotFoundException {

    }

    public Entry getEntry(RequestContext request, String entryId) throws NotFoundException {
        return null;
    }

    public Feed getFeed(RequestContext request) throws BadRequestException {
        return null;
    }
}

