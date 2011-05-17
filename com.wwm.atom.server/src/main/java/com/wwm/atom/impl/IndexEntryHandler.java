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

import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;

import com.wwm.abdera.util.server.BadRequestException;
import com.wwm.abdera.util.server.NotFoundException;
import com.wwm.db.core.LogFactory;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.Record;
import com.wwm.indexer.SearchResult;
import com.wwm.indexer.SearchResults;
import com.wwm.indexer.exceptions.AttributeException;
import com.wwm.indexer.exceptions.IndexerException;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.Score;

public class IndexEntryHandler implements TypeHandler {

    protected static Logger log = LogFactory.getLogger(ProviderImpl.class);


    public void createEntry(RequestContext request, Document<Entry> doc) throws Exception {

        Entry entry = doc.getRoot();
        try {
            // Handle an index entry
            entry.setUpdated(new Date());
            Record record = FuzzyRecordBuilder.getRecord(doc);
            createProfile(request, record);
            /* Update atom:id to the privateRecordId supplied by the user,
             * and add a link so that user can access it via this id. */
            entry.getIdElement().setValue( record.getPrivateId() );
            entry.addLink(IndexerFactory.baseFeedUrl + "/" + entry.getId().toString(), "edit");
        } catch (AttributeException e) {
            throw new BadRequestException("Invalid Attribute Data:" + e.getMessage(), e);

        } catch (IndexerException e) {
            log.error("Indexer Exception", e);
            //			log.info("CREATE: Did UPDATE instead because record already existed: " + record.getPrivateRecordId() );
            // If profile exists, then try updating instead
            //			updateProfile(request, webServiceId, record);
            throw new Error(e);
        }
    }

    public void deleteEntry(RequestContext context, String privateRecordId) throws NotFoundException {
        try {
            IndexerFactory.getIndexer().deleteRecord(privateRecordId);
        } catch (IndexerException e) {
            log.error("Indexer Exception", e);
            throw new NotFoundException(); // Think this is okay
        }
    }

    public Entry getEntry(RequestContext request, String entryId) throws NotFoundException {
        Record record;

        // Get 'Profile' and then convert to a Document<Entry> to output
        try {
            record = IndexerFactory.getIndexer().retrieveRecord(entryId);
        } catch (IndexerException e) {
            throw new NotFoundException( e.getMessage() );
        }

        // base document
        Abdera abdera = request.getAbdera();
        Factory factory = abdera.getFactory();

        Entry entry = createEntryFromProfile(entryId, record, factory);

        Document<Entry> doc = factory.newDocument();
        doc.setRoot(entry);
        return entry;
    }




    /**
     * Create profile. Throws exception if already exists
     * 
     * == FOR BOTH Create and update, need to establish how Web stuff avoids
     * using more attrs than are allowed.
     */
    private void createProfile(RequestContext request, Record fuzzyRecord) throws IndexerException {

        // Create new profile.
        log.info("Creating new record:" + fuzzyRecord.getPrivateId() );
        //	    String contentType = fuzzyRecord.getContentType();
        IndexerFactory.getIndexer().addRecord(fuzzyRecord);

        // TODO: add unique id to Record ??
    }

    private Entry createEntryFromProfile(String entryId, Record record,
            Factory factory) {
        Entry entry = factory.newEntry();
        // Update required Atom elements
        entry.setId(entryId);
        entry.setUpdated(record.getUpdatedDate()); // NOTE: Using lastLogin as no "modified"

        entry.setTitle(record.getTitle() );

        entry.addLink(IndexerFactory.baseFeedUrl + "/" + record.getPrivateId() );
        entry.addLink(IndexerFactory.baseFeedUrl + "/" + record.getPrivateId(), "edit");

        // Translate our record into Atom XML
        FuzzyRecordBuilder builder = new FuzzyRecordBuilder(entry);
        builder.setMetadata("(no contentType)", entryId);
        builder.add( record );

        return entry;
    }


    public Feed getFeed(RequestContext request) throws BadRequestException {
        Abdera abdera = request.getAbdera();
        Factory factory = abdera.getFactory();
        Feed feed = factory.newFeed();
        try {
            feed.setId("tag:ws.whirlwindmatch.com,2007:feed");
            feed.setTitle("Fuzzy Match Feed");
            feed.setUpdated(new Date());
            feed.addLink("");
            feed.addLink("", "self");
            feed.addAuthor("Fuzz-Bot");
        } catch (Exception e) {
            throw new Error(e);
        }

        log.info("Feed query: " + request.getUri().getQuery() );
        
        // CONVERT REQUEST parameters to a fuzzy record that can be used for query
        Record query = getQuery( request );

        //		CustomAttribsSearch search = new CustomAttribsSearch("OtherProfileForm", resultsPerPage , query);
        //		search.doSearch();
        //		search.setSearchType(matchStyle);
        //		search.setFilterOwnProfiles(false);
        Map<String, Attribute> search = query.getAttributes();

//        int pageNo = 0;
        int resultsPerPage = 10; // FIXME: Rob needs some paging support

        // FIXME: Note numResults and matchStyle are "special" cases and need filtering at
        // com.wwm.atom.impl.FuzzyRecordBuilder.getRecord(RequestContext)
        
        String rpp = request.getParameter("numResults");
        if (rpp != null) {
            try {
                resultsPerPage = Integer.valueOf(rpp);
            } catch (NumberFormatException e) {
                log.info("Error in request URL: numResults=" + rpp);
                throw new BadRequestException("Error in request URL. Expected integer for numResults=" + rpp);
            }
        }

        // SUBMIT QUERY TO QueryMgr
        String matchStyle = request.getParameter("matchStyle");
        if (matchStyle == null) {
            matchStyle = "defaultMatchStyle";
        }
        SearchResults results = null;
        try {
            //			results = search.getResults(pageNo );
            results = IndexerFactory.getIndexer().searchRecords(
                    search, matchStyle, Integer.MAX_VALUE,
                    resultsPerPage, 0.01f);
        } catch (IndexerException e) {
            throw new BadRequestException("Invalid Attribute Data:" + e.getMessage(), e);
        } catch (Exception e) {
            throw new BadRequestException("Search failed (possibly undefined search config)", e);
        }

        // ITERATE OVER RESULTS
        for ( SearchResult result : results.getResults()) {
            String entryId = result.getPrivateId();
            Entry entry = createEntryFromProfile(entryId, result, factory);
            addScoresToEntry(entry, result);
            expandScorerAttributes(entry, result);
            feed.addEntry(entry);
        }
        return feed;
    }

    /**
     * Expand the general purpose annotations that are generated by different scorers,
     * such as Distance being generated by VectorDistanceScorer
     */
    private void expandScorerAttributes(Entry entry, SearchResult result) {
        FuzzyRecordBuilder builder = new FuzzyRecordBuilder(entry);
        // NOTE: Hard coded for now
        Score score = result.getScore();
        for (String name: score.getScorerAttrNames()){
            if (name.equals("Distance")) {
                builder.addFloat("Distance", score.getScorerAttributeAsFloat("Distance"));
            }
        }
    }

    private void addScoresToEntry(Entry entry, SearchResult result){
        FuzzyRecordBuilder builder = new FuzzyRecordBuilder(entry);
        builder.setScores( result.getScore() );
        // TODO: allow more detailed score to be returned...
    }



    // impl moved can inline
    private Record getQuery(RequestContext request) {
        Record record = FuzzyRecordBuilder.getRecord(request);
        return record;
    }



}
