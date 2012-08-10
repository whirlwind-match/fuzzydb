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

import org.slf4j.Logger;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.fuzzydb.attrs.WWConfigHelper;

import com.wwm.abdera.util.server.BadRequestException;
import com.wwm.abdera.util.server.NotFoundException;
import com.wwm.db.core.LogFactory;
import com.wwm.indexer.IndexerFactory;

public class ScoreConfigEntryHandler implements TypeHandler {

	protected static Logger log = LogFactory.getLogger(ScoreConfigEntryHandler.class);

	public void createEntry(RequestContext request, Document<Entry> doc) throws Exception {

		String content = doc.getRoot().getContent();
		WWConfigHelper.updateScorerConfig( IndexerFactory.getCurrentStore(), content );

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

