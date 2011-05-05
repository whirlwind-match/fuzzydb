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

import org.slf4j.Logger;

import com.wwm.attrs.AttrsFactory;
import com.wwm.attrs.userobjects.StandaloneWWIndexData;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.core.LogFactory;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultIterator;
import com.wwm.db.query.ResultSet;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.indexer.Indexer;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.Record;
import com.wwm.indexer.SearchResults;
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
    public void addRecord(Record record) throws IndexerException {
        try {
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
        } catch (ArchException e) {
            throw new Error(e);
        }
    }


    public Record retrieveRecord(String privateId) throws IndexerException {
        try {
            Store currentStore = IndexerFactory.getCurrentStore();
			Transaction tx = currentStore.getStore().begin();
            StandaloneWWIndexData data = tx.retrieve(StandaloneWWIndexData.class, StandaloneWWIndexData.sPrivateId, privateId);
            tx.dispose();
            if (data == null) {
                throw new IndexerException("Couldn't find record: privateId=" + privateId);
            }
            RecordImpl rec = new RecordImpl(privateId);
            recordConverter.convertInternalToRecord(rec, data);
            log.info("Retrieved record:" + privateId + ", store:" + currentStore.getStoreName());
            return rec;
        } catch (ArchException e) {
            throw new IndexerException(e);
        }
    }


    /* (non-Javadoc)
     * @see com.wwm.indexer.Indexer#addRecords(java.util.ArrayList)
     */
    public void addRecords(ArrayList<Record> records) throws IndexerException {
        try {
            Store currentStore = IndexerFactory.getCurrentStore();
			Transaction tx = currentStore.getAuthStore().begin();
            Collection<Object> createdata = new ArrayList<Object>();
            for (Record record : records) {
                StandaloneWWIndexData data = new StandaloneWWIndexData(record.getPrivateId());
                recordConverter.convertRecordToInternal(data, record);
                createdata.add(data);
            }
            tx.create(createdata);
            tx.commit();
            log.info("Added " + records.size() + " records to store:" + currentStore.getStoreName());
        } catch (ArchException e) {
            e.printStackTrace();
        }
    }


    public void deleteRecord(String privateRecordId) throws IndexerException {
        try {
        	Store currentStore = IndexerFactory.getCurrentStore();
			Transaction tx = currentStore.getStore().getAuthStore().begin();
            StandaloneWWIndexData data = tx.retrieve(StandaloneWWIndexData.class, StandaloneWWIndexData.sPrivateId, privateRecordId);
            if (data == null) {
                tx.dispose();
                return;
            }

            tx.delete(data);
            tx.commit();
            log.info("Deleted record:" + privateRecordId + " from store:" + currentStore.getStoreName());
        } catch (ArchException e) {
            e.printStackTrace();
        }
    }


    /* (non-Javadoc)
     * @see com.wwm.indexer.Indexer#deleteRecord(int)
     */
    public void deleteRecord(int recordId) throws IndexerException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.wwm.indexer.Indexer#deleteRecords(java.util.ArrayList)
     */
    public void deleteRecords(ArrayList<Integer> recordIds) throws IndexerException {
        throw new UnsupportedOperationException();
    }


    public void deleteRecordsByPrivateId(ArrayList<String> privateIds)
    throws IndexerException {
        throw new UnsupportedOperationException();
    }


    public SearchResults searchRecords(Record record, String scorerConfig, int maxResults, int numResults, float minScore) throws IndexerException {
        StandaloneWWIndexData data = new StandaloneWWIndexData(record.getPrivateId());
        recordConverter.convertRecordToInternal(data, record);
        SearchSpec searchSpec = AttrsFactory.createSearchSpec(StandaloneWWIndexData.class);
        searchSpec.setAttributes( data );
            return doSearch(scorerConfig, maxResults, numResults, searchSpec);
    }
    

    public SearchResults searchRecords(Map<String, Attribute> attributes, String scorerConfig, int maxResults, int numResults, float minScore) throws IndexerException {
        SearchSpec searchSpec = AttrsFactory.createSearchSpec(StandaloneWWIndexData.class);
        searchParamsConverter.buildSearchAttributes(searchSpec, attributes);
        return doSearch(scorerConfig, maxResults, numResults, searchSpec);
    }

    
	private SearchResults doSearch(String scorerConfig, int maxResults, int numResults,
			SearchSpec searchSpec) throws IndexerException {
		
        lastSearch++;
		searchSpec.setTargetNumResults(maxResults);
		searchSpec.setScorerConfig(scorerConfig);

		Store currentStore = IndexerFactory.getCurrentStore();
		log.info("Searching on store: "+ currentStore.getStoreName() + " using matchStyle: " + scorerConfig );
		Transaction tx = currentStore.getStore().begin();
		ResultSet<Result<StandaloneWWIndexData>> query;
		try {
			query = tx.query(StandaloneWWIndexData.class, searchSpec);
		} catch (ArchException e) {
			throw new IndexerException("Unexpected Db exception:" + e.getMessage(), e);
		} catch (RuntimeException e) {
			throw new IndexerException("Runtime exception:" + e.getMessage(), e);
		}
		searchSet.put(lastSearch, query.iterator());
		return buildResults(lastSearch, numResults);
	}

    
    
    /* (non-Javadoc)
     * @see com.wwm.indexer.Indexer#searchnext(int)
     */
    public SearchResults searchNext(int searchId, int numResults) throws IndexerException {
        return buildResults(lastSearch, numResults);
    }

    private SearchResults buildResults(int searchId, int numResults) throws IndexerException {
        ResultIterator<Result<StandaloneWWIndexData>> query = searchSet.get(searchId);
        if (query == null) {
            throw new IndexerException("buildResults :: Unknown searchId :: " + searchId);
        }

        SearchResultsImpl results = new SearchResultsImpl(searchId);
        int i = 0;
        while (query.hasNext() ) {
            Result<StandaloneWWIndexData> result = query.next();
            StandaloneWWIndexData item = result.getItem();
            if (result == null) {
                break;
            }
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
    
    public long getCount() throws IndexerException {
    	Transaction tx = IndexerFactory.getCurrentStore().begin();
		long count = 0;
		try {
			count = tx.count(StandaloneWWIndexData.class);
		} catch (ArchException e) {
			throw new IndexerException(e);
		}
		return count;
    }
}
