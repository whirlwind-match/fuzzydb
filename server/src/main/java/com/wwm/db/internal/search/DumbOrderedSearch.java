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
package com.wwm.db.internal.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.fuzzydb.attrs.IScoreConfiguration;
import org.fuzzydb.attrs.Score;
import org.fuzzydb.attrs.search.SearchSpecImpl;
import org.fuzzydb.client.internal.MetaObject;
import org.fuzzydb.client.marker.IWhirlwindItem;
import org.slf4j.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.db.core.WorkManager;
import com.wwm.db.internal.table.UserTable;
import com.wwm.util.NanoTimer;



/**
 * Implementation of an in-progress search, from which we want the results in order of score.
 * This is implemented without an index, by iterating over every instance of the
 * required class.
 * This search is configured with a hard limit, INDEX_ABORT_LIMIT, at which it will
 * stop scoring results.  This is to ensure that things stay usable in the event
 * of a large database being built but without a real index.
 */

public class DumbOrderedSearch<T extends IWhirlwindItem> implements Search {

    private static Logger log = LogFactory.getLogger(DumbOrderedSearch.class);

    private final SearchSpecImpl spec;
    private AtomicInteger nextSeq = new AtomicInteger();
    private final ResultsQ resultsQ;
    private final UserTable<T> table;

    private final boolean nominee;

    private final IScoreConfiguration config;

    /**
     * Maximum number of items to index "the dumb way"
     */
    static private final int INDEX_ABORT_LIMIT = 10000;

    private static int searchCount = 0;
    private static float searchTime = 0.0f;
    private static long searchStartTime = 0;
    private static int totalResults = 0;

    /**
     * Begin a new, really really inefficient search
     */
    public DumbOrderedSearch(SearchSpecImpl spec, IScoreConfiguration config, boolean nominee, UserTable<T> table) {
        super();
        this.spec = spec;
        this.table = table;
        this.nominee = nominee;
        this.config = config;

        if ( log.isDebugEnabled() ){
        	log.debug( "New Search: threshold = " + spec.getScoreThreshold()
        			+ ", targetNumResults = " + spec.getTargetNumResults()
        			+ ", searchType = " + spec.getScorerConfig() );
        }

        NanoTimer timer = new NanoTimer();
        resultsQ = new ResultsQ(spec.getMaxNonMatches(), spec.getScoreThreshold(), spec.getTargetNumResults());

        fillResultsQ();

        logResults(timer);
    }

    /**
     * Naively score everything in the table with a cheeky limit :)
     */
    private void fillResultsQ() {
        LinkedList<Future<NextItem>> results = new LinkedList<Future<NextItem>>();
        
        int indexed = 0;
        for (final MetaObject<T> mo : table) {

            Callable<NextItem> task = new Callable<NextItem>() {
                public NextItem call() throws Exception {
                    IWhirlwindItem dbItem = mo.getObject();
                    Score itemScore = config.scoreAllItemToItem(spec.getAttributeMap(), dbItem.getAttributeMap(), spec.getSearchMode());
                    if (itemScore.compareTo(resultsQ.getCurrentScoreThreshold()) > 0 ) { // By default, zero, so we add all non-zero scores
                        NextItem newItem = new NextItem(itemScore, nextSeq.getAndIncrement(), mo, null);
                        return newItem;
                    }
                    return null;
                }
            };
            results.add(WorkManager.getInstance().submit(task));

            if (indexed++ > INDEX_ABORT_LIMIT) {
                log.warn("Aborted dumb search after " + INDEX_ABORT_LIMIT +" items.  Perhaps you want the accelerator pack ;)");
                break; // FIXME: Need to inform user/upper layers that limit was reached
            }
        }
        
        
        
        for (Future<NextItem> future : results) {
            NextItem result;
            try {
                result = future.get();
                if (result != null) {
                    resultsQ.add(result);
                }
            }
            catch (InterruptedException e) {
                WorkManager.handleException(e);
            }
            catch (ExecutionException e) {
                WorkManager.handleException(e);
            }
        }
    }


    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.search.Search#getSpec()
     */
    public SearchSpecImpl getSpec() {
        return spec;
    }

    /** used internally by search code
     * @return next unique sequence number
     */
    public int getNextSeq() {
        return nextSeq.getAndIncrement();
    }

    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.search.Search#getNextResults(int)
     */
    public ArrayList<NextItem> getNextResults(int limit)
    {
        ArrayList<NextItem> results = new ArrayList<NextItem>();
        while (results.size() < limit) {

            // If the result q is empty, return what we've got as that's the end.
            if  (resultsQ.isEmpty()) {
            	logResults(results);
                return results;
            }
            results.add( resultsQ.pop() );
        }
    	logResults(results);
        return results;
    }

    private void logResults(NanoTimer timer) {

        float t = timer.getMillis();

        if (searchCount == 0) {
            searchStartTime = System.currentTimeMillis();
        }

        // Log some info about the work done
        if ( log.isDebugEnabled() ) {
            log.debug( "Queued " + resultsQ.size() +
            		" results.  Time (ms): " + timer.getMillis()
            );
        }

        searchTime += t;
        searchCount++;


        if (searchCount == 100) {
            float avTime = searchTime / searchCount;
            float avElapsed = (float)(System.currentTimeMillis() - searchStartTime) / searchCount;
            float avResults = (float)(totalResults) / searchCount;
            log.info("====================== SEARCH STATS =============================");
            log.info("Elapsed time per search: " + avElapsed + "ms (i.e. actual rate: " + 1000 / avElapsed + " searches per sec)");
            log.info("Mean time doing search: " + avTime + "ms (i.e. potential rate: " + 1000 / avTime + " searches per sec)");
            log.info("Mean results per search: " + avResults + " (=> SearchTime per result =" + avTime / avResults + "ms)");
            log.info("Non-search time (elapsed - search): " + (avElapsed - avTime) + "ms");
            searchTime = 0.0f;
            searchCount = 0;
            totalResults = 0;
        }
    }

    private void logResults(ArrayList<NextItem> results) {

        // Log some info about the work done
        if ( log.isDebugEnabled() ) {
            log.debug( "# results: " + results.size()
            );
        }

        totalResults += results.size();
    }


    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.search.Search#isMoreResults()
     */
    public boolean isMoreResults() {
        if (resultsQ.isEmpty()) {
            return false;
        }
        return true;
    }


    /* (non-Javadoc)
     * @see org.fuzzydb.attrs.search.Search#isNominee()
     */
    public boolean isNominee() {
        return nominee;
    }
}
