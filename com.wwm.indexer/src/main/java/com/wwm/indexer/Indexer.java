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
package com.wwm.indexer;

import java.util.ArrayList;
import java.util.Map;

import com.wwm.context.IShutdown;
import com.wwm.indexer.exceptions.IndexerException;
import com.wwm.model.attributes.Attribute;

public interface Indexer extends IShutdown {

    /**
     * This method will add a new record to the index, or if the RecordId
     * corresponds to an existing record that record is updated.
     * 
     * @param record
     *            Data to store in the index.
     * @throws IndexerException
     */
    void addRecord(Record record);

    /**
     * This method will delete the specified record. If it does not exist, this
     * method silently has no effect.
     * 
     * @param recordId
     *            A unique integer identifying this record
     * @throws IndexerException
     */
    void deleteRecord(int recordId);

    void deleteRecord(String privateRecordId);

    /**
     * This method performs a search using a {@link Record} as the 
     * 'prototype' to match against. It returns a collection of Records.
     * 
     * @param record
     *            A record
     * @param scorerConfig
     *            A string specifying which search configuration to use
     * @param maxResults
     *            An integer specifying the maximum number of results to be
     *            returned
     * @param numresults
     *            An integer specifying the number number of results to be
     *            returned
     * @param minScore TODO
     * @return A collection of records
     * @throws IndexerException
     */
    public SearchResults searchRecords(Record record, String scorerConfig, int maxResults, int numResults, float minScore);

    /**
     * This method performs a search using a map of search parameters.
     * It returns a collection of records as added by the
     * AddRecord and AddRecords methods
     * 
     * @param attributes
     *            A collection of search attributes
     * @param scorerConfig
     *            A string specifying which search configuration to use
     * @param maxResults
     *            An integer specifying the maximum number of results to be
     *            returned
     * @param numresults
     *            An integer specifying the number number of results to be
     *            returned
     * @param minScore TODO
     * @return A collection of records
     * @throws IndexerException
     */
    SearchResults searchRecords(Map<String, Attribute> attributes, String scorerConfig, int maxResults, int numresults, float minScore);

    /**
     * Gets the next page of results from the current search.
     * 
     * @param searchId
     *            An integer specifying the search to get more results for
     * @param numresults
     *            An integer specifying the number of results to be returned
     * @return
     * @throws IndexerException
     */
    SearchResults searchNext(int searchId, int numresults);

    /**
     * This method is similar to AddRecord but takes a collection of Records.
     * Adding multiple records is more time efficient than adding a single
     * record.
     * 
     * @param records
     *            A collection of record data
     * @throws IndexerException
     */
    void addRecords(ArrayList<Record> records);

    /**
     * This method is similar to DeleteRecords but takes a collection of
     * RecordIds.
     * 
     * @param recordIds
     *            A collection of record Id's
     * @throws IndexerException
     */
    void deleteRecords(ArrayList<Integer> recordIds);

    void deleteRecordsByPrivateId(ArrayList<String> privateIds);

    /**
     * This method is retrieves a record
     * 
     * @param recordId
     *            A unique string identifying this record
     * @throws IndexerException
     */
    Record retrieveRecord(String recordId);

    /**
     * Get count of how many records we've inserted
     * @throws IndexerException 
     */
	long getCount();


}
