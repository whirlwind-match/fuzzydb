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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;

import com.wwm.abdera.util.server.BadRequestException;
import com.wwm.abdera.util.server.BaseCollectionAdapter;
import com.wwm.abdera.util.server.NotFoundException;
import com.wwm.atom.elements.AbderaElementFactory;
import com.wwm.db.core.LogFactory;
import com.wwm.indexer.Indexer;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.Record;
import com.wwm.indexer.exceptions.IndexerException;
import com.wwm.util.StringUtils;


/**
 * This provider redirects content of different types to registered type handlers.
 * Support is needed for:
 * - Index entries
 * - Scorer configurations
 * - Enum definitions
 * - Index strategies (these could initially be done based on first scorer config, and rebuild index on change)
 * (NOTE: Index strategy support is somewhat low priority, as it's just a perf boost for bigger sites.. some sensible
 *  defaults can be assumed).
 * Future:
 * - Newsletter templates
 * - Newsletter schedules
 * - Scorers (if we chose to manage them separately from the full configuration)
 */
public class CollectionAdapterImpl extends BaseCollectionAdapter {

    protected static Logger log = LogFactory.getLogger(CollectionAdapterImpl.class);

    // Type Handlers could be in own class
    static Map<String,TypeHandler> typeHandlers = new HashMap<String, TypeHandler>();
    static {
        IndexEntryHandler indexHandler = new IndexEntryHandler();
        typeHandlers.put( "IndexEntry", indexHandler );
        typeHandlers.put( "ScoreConfiguration", new ScoreConfigEntryHandler());
        typeHandlers.put( "IndexConfiguration", new IndexConfigEntryHandler());
        typeHandlers.put( null, indexHandler ); // null allowed in HashMap (but not TreeMap)
    }

    static TypeHandler getTypeHandler(Document<Entry> doc) throws BadRequestException {
        // Use categories to differentiate between config and entries etc.
        // See http://www.ibm.com/developerworks/xml/library/x-tipatom4.html#N100A1
        List<Category> categories = doc.getRoot().getCategories();
        String type = (categories.size() == 0) ? null : categories.get(0).getTerm();
        TypeHandler th = typeHandlers.get(type);
        if (th == null) {
            throw new BadRequestException("Illegal category:" + type);
        }
        return th;
    }

    static TypeHandler getTypeHandler(String id) throws BadRequestException {
        // TODO: This expects and ID to be prefixed with the handler string
        // e.g. IndexEntry:112233, or Scorer:334455
        String type = StringUtils.subStringBefore(id, ":");
        TypeHandler th = typeHandlers.get(type);
        if (th == null) {
            throw new BadRequestException("Illegal category:" + type);
        }
        return th;
    }

    
    

    @Override
    protected void registerExtensionsInternal(Factory factory) {
        factory.registerExtension( new AbderaElementFactory() );  // FIXME: check. might find we're repeating this on same instance
    }


    @Override
    protected void createEntryInternal(RequestContext request, Document<Entry> doc, Entry entry) throws Exception {

        TypeHandler th = getTypeHandler(doc);
        th.createEntry(request, doc);
    }



    @Override
    protected void deleteEntryInternal(RequestContext request, String privateRecordId) throws NotFoundException, BadRequestException {

        // FIXME: There are interesting implications here for index configuration items, but should be able to
        // provide a unique id that gives us access to update and delete those items.
        TypeHandler th = getTypeHandler(privateRecordId);
        th.deleteEntry(request, privateRecordId);
    }


    /**
     * Gets an Entry for
     * @param request
     */
    @Override
    protected Entry getEntryInternal(RequestContext request, String entryId) throws NotFoundException, BadRequestException {

        TypeHandler th = getTypeHandler(entryId);
        Entry entry = th.getEntry(request, entryId);
        return entry;
    }


    @Override
    protected Feed getFeedInternal(RequestContext request) throws BadRequestException {

        // Must specify ?category=Scorer for scorers
        TypeHandler th = typeHandlers.get( request.getParameter("category"));

        return th.getFeed(request);
    }


    @Override
    protected void updateEntryInternal(RequestContext request, Document<? extends Entry> updatedDoc) throws IOException {
        Entry entry = updatedDoc.getRoot();

        // FIXME: Handle conflict (I think this is probably dealt with by AppLayer API if we're lucky
        // TODO: Improve AppLayer API to have unique entry id avail (e.g. DbVersion)
        //				if (!entry.getId().equals(orig_entry.getId()))
        //					return new EmptyResponseContext(HttpServletResponse.SC_CONFLICT);

        entry.setUpdated(new Date());
        //				entry.getIdElement().setValue(factory.newUuidUri());
        //FIXME: Should this be addEditLinkToEntry(entry);
        entry.addLink("fuzz/feed/" + entry.getId().toString(),"edit");


        Record record = FuzzyRecordBuilder.getRecord(updatedDoc);
        try {
            updateProfile(request, record);
        } catch (IndexerException e) {
            log.info(e.getMessage(), e);
            throw new Error(e);
        }
    }



    private void updateProfile(RequestContext request, Record fuzzyRecord) throws IndexerException {

        Indexer indexer = IndexerFactory.getIndexer();

        // Update existing profile.
        log.info("Updating existing record");
        indexer.addRecord(fuzzyRecord); // does an update if it already exists
        log.info("Record updated successfully."); // success .. no exception
    }



    @Override
    protected void decorateExceptionResponse(EmptyResponseContext rc, Exception e) {
        String message = e.getMessage();
        // assert(message != null); // Exception must have a message as we're using it as a header which must be non-null

        log.info(message); // ,e );

        rc.addHeader( "Fuzz-Exception", message );
        rc.addHeader( "Fuzz-ExceptionClass", e.getClass().getSimpleName() );
    }


    @Override
    protected void decorateErrorResponse(EmptyResponseContext rc, String error) {
        log.info(error);

        rc.addHeader( "Fuzz-Error", error );
    }


	@Override
	protected Collection<String> getCategoriesInternal() {
		return typeHandlers.keySet();
	}

	@Override
	protected String getFeedUri() {
		return "fuzz/feed";
	}
    
}
