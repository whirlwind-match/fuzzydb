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
package com.wwm.indexer.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.fuzzydb.attrs.AttrsFactory;
import org.fuzzydb.attrs.userobjects.StandaloneWWIndexData;
import org.fuzzydb.client.Store;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.core.LogFactory;
import org.fuzzydb.core.query.Result;
import org.fuzzydb.core.query.ResultIterator;
import org.fuzzydb.core.query.ResultSet;
import org.fuzzydb.core.whirlwind.SearchSpec;
import org.slf4j.Logger;

import com.wwm.indexer.Indexer;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.Record;
import com.wwm.indexer.SearchResults;
import com.wwm.indexer.exceptions.AttributeException;
import com.wwm.indexer.exceptions.IndexerException;
import com.wwm.model.attributes.Attribute;

public class IndexerImpl implements Indexer {
	
    protected static Logger log = LogFactory.getLogger(IndexerImpl.class);

    private final Map<Integer, ResultIterator<Result<StandaloneWWIndexData>>> searchSet = Collections.synchronizedMap(new TreeMap<Integer, ResultIterator<Result<StandaloneWWIndexData>>>());
    private int lastSearch = 0;
    private final RecordConverter recordConverter;
    private final SearchParamsConverter searchParamsConverter;

    public IndexerImpl() {
        this.recordConverter = new RecordConverter();
        this.searchParamsConverter = new SearchParamsConverter();
    }

    /* (non-Javadoc)
     * @see com.wwm.indexer.Indexer#addRecord(com.wwm.indexer.Record)
     */
    public void addRecord(Record record) {
        Store currentStore = IndexerFactory.getCurrentStore();
		Transaction tx = currentStore.getAuthStore().begin();
        StandaloneWWIndexData data = tx.retrieve(StandaloneWWIndexData.class, StandaloneWWIndexData.sPrivateId, record.getPrivateId());
        if (data == null) {
            data = new StandaloneWWIndexData(record.getPrivateId());
            recordConverter.convertRecordToInternal(data, record);
            tx.create(data);
            tx.commit();
            log.info("Added record:" + record.getPrivateId() + ", store:" + currentStore.getStoreName());
        } else {
            recordConverter.convertRecordToInternal(data, record);
            tx.update(data);
            tx.commit();
            log.info("Updated record:" + record.getPrivateId() + ", store:" + currentStore.getStoreName());
        }
    }


    public Record retrieveRecord(String privateId) {
        Store currentStore = IndexerFactory.getCurrentStore();
		Transaction tx = currentStore.begin();
        StandaloneWWIndexData data = tx.retrieve(StandaloneWWIndexData.class, StandaloneWWIndexData.sPrivateId, privateId);
        tx.dispose();
        if (data == null) {
            throw new IndexerException("Couldn't find record: privateId=" + privateId);
        }
        RecordImpl rec = new RecordImpl(privateId);
        recordConverter.convertInternalToRecord(rec, data);
        log.info("Retrieved record:" + privateId + ", store:" + currentStore.getStoreName());
        return rec;
    }


    /* (non-Javadoc)
     * @see com.wwm.indexer.Indexer#addRecords(java.util.ArrayList)
     */
    public void addRecords(ArrayList<Record> records) {
        Store currentStore = IndexerFactory.getCurrentStore();
		Transaction tx = currentStore.getAuthStore().begin();
        Collection<Object> createdata = new ArrayList<Object>();
        for (Record record : records) {
            try {
				StandaloneWWIndexData data = new StandaloneWWIndexData(record.getPrivateId());
				recordConverter.convertRecordToInternal(data, record);
				createdata.add(data);
			} catch (AttributeException e) {
				log.warn("Record skipped due to exception: " + e.getMessage());
			}
        }
        tx.create(createdata);
        tx.commit();
        log.info("Added " + records.size() + " records to store:" + currentStore.getStoreName());
    }


    public void deleteRecord(String privateRecordId) {
    	Store currentStore = IndexerFactory.getCurrentStore();
		Transaction tx = currentStore.getAuthStore().begin();
        StandaloneWWIndexData data = tx.retrieve(StandaloneWWIndexData.class, StandaloneWWIndexData.sPrivateId, privateRecordId);
        if (data == null) {
            tx.dispose();
            return;
        }

        tx.delete(data);
        tx.commit();
        log.info("Deleted record:" + privateRecordId + " from store:" + currentStore.getStoreName());
    }


    public void deleteRecord(int recordId) {
        throw new UnsupportedOperationException();
    }

    public void deleteRecords(ArrayList<Integer> recordIds) {
        throw new UnsupportedOperationException();
    }


    public void deleteRecordsByPrivateId(ArrayList<String> privateIds) {
        throw new UnsupportedOperationException();
    }


    public SearchResults searchRecords(Record record, String scorerConfig, int maxResults, int numResults, float minScore) {
        try {
			StandaloneWWIndexData data = new StandaloneWWIndexData(record.getPrivateId());
			recordConverter.convertRecordToInternal(data, record);
			SearchSpec searchSpec = AttrsFactory.createSearchSpec(StandaloneWWIndexData.class);
			searchSpec.setAttributes( data );
			    return doSearch(scorerConfig, maxResults, numResults, searchSpec);
		} catch (AttributeException e) {
			log.warn("Record skipped due to exception: " + e.getMessage());
			return null;
		}
    }
    

    public SearchResults searchRecords(Map<String, Attribute<?>> attributes, String scorerConfig, int maxResults, int numResults, float minScore) {
        SearchSpec searchSpec = AttrsFactory.createSearchSpec(StandaloneWWIndexData.class);
        searchParamsConverter.buildSearchAttributes(searchSpec, attributes);
        return doSearch(scorerConfig, maxResults, numResults, searchSpec);
    }

    
	private SearchResults doSearch(String scorerConfig, int maxResults, int numResults,
			SearchSpec searchSpec) {
		
        lastSearch++;
		searchSpec.setTargetNumResults(maxResults);
		searchSpec.setScorerConfig(scorerConfig);

		Store currentStore = IndexerFactory.getCurrentStore();
		log.info("Searching on store: "+ currentStore.getStoreName() + " using matchStyle: " + scorerConfig );
		Transaction tx = currentStore.begin();
		ResultSet<Result<StandaloneWWIndexData>> query;
		query = tx.query(StandaloneWWIndexData.class, searchSpec);
		searchSet.put(lastSearch, query.iterator());
		return buildResults(lastSearch, numResults);
	}

    
    
    public SearchResults searchNext(int searchId, int numResults) {
        return buildResults(lastSearch, numResults);
    }

    private SearchResults buildResults(int searchId, int numResults) {
        ResultIterator<Result<StandaloneWWIndexData>> query = searchSet.get(searchId);
        if (query == null) {
            throw new IndexerException("buildResults :: Unknown searchId :: " + searchId);
        }

        SearchResultsImpl results = new SearchResultsImpl(searchId);
        int i = 0;
        while (query.hasNext() ) {
            Result<StandaloneWWIndexData> result = query.next();
            StandaloneWWIndexData item = result.getItem();
            SearchResultImpl sr = new SearchResultImpl(result.getScore(), item.getPrivateId());
            recordConverter.convertInternalToRecord(sr, item);
            results.addResult(sr);
            i++;
            if (i >= numResults) {
                break;
            }
        }

        return results;
    }

    public void shutdown() {
        // Do nowt
    }
    
    public long getCount() {
    	Transaction tx = IndexerFactory.getCurrentStore().begin();
		long count = tx.count(StandaloneWWIndexData.class);
		tx.dispose();
		return count;
    }
}
