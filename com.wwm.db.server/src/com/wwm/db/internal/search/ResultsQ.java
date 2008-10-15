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

import com.wwm.attrs.Score;
import com.wwm.attrs.Scorer;

/**
 * Ordered queue of index items not yet reported in search results. Ordered by priority
 * ResultQ automatically discards lowest scoring items in whenever the scoreThreshold and
 * targetNumResults conditions are met.
 */
public class ResultsQ extends Q<NextItem> {

    private int resultsQAdded = 0; // track number of results added to resutltsQ so that we can start discarding end of resultsQ
    private Score currentScoreThreshold; // Scores of this value and below are not added
    private final int targetNumResults; // The number of results we're expecting to look at (any beyond this may not include all profiles)


    public ResultsQ(final int maxNonMatches, final float scoreThreshold, int targetNumResults) {
        this.targetNumResults = targetNumResults;

        this.currentScoreThreshold = new Score() {
            private static final long serialVersionUID = 1L;

            @Override
            public void add(Scorer s, float score, Direction d) {
            	throw new UnsupportedOperationException(); // this should not be modified (a constant)
            }

            @Override
            protected void update() {
                this.linear = scoreThreshold;
                this.nonMatches = maxNonMatches;
            }
        };
    }

    /**
     * Add the item to the queue, but keep track of how many items we have in the results,
     * and if we've hit our target number of results, then always remove the surplus after
     * each add, and update the targetScore.
     * @param newItem
     */
    @Override
    final public void add(NextItem newItem) {
        super.add(newItem);
        resultsQAdded++;  // Note: this is not the same as resultsQ.size() as it's getting stuff removed.

        // If we've got relevant number of results, then keep track of
        // worst result to compare against.
        if ( resultsQAdded > targetNumResults) {
            NextItem worstItem = this.worst();
            assert( currentScoreThreshold.compareTo( worstItem.getScore() ) <= 0  ); // should never have things in resultsQ that are below the threshold
            currentScoreThreshold = worstItem.getScore();  // Update so that we discard any Items or Nodes that are worst than best results so far.
            this.remove( worstItem ); // Get rid of surplus one
        }
    }

    public Score getCurrentScoreThreshold() {
        return currentScoreThreshold;
    }

    /**
     * @link association NextItem
     * @directed true
     * @supplierCardinality 0..*
     */


}
