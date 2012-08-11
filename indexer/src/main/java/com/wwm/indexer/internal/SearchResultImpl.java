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

import org.fuzzydb.dto.attributes.Score;

import com.wwm.indexer.Record;
import com.wwm.indexer.SearchResult;

public class SearchResultImpl extends RecordImpl implements SearchResult {

    private Score score;

    public SearchResultImpl(Score score, String recordId) {
        super(recordId);
        this.score = score;
    }

    public Score getScore() {
        return score;
    }

    public Record getRecord() {
        return this;
    }

    public float getForwardScore() {
        // TODO Auto-generated method stub
        return 0;
    }

    public float getForwardScore(String name) {
        // TODO Auto-generated method stub
        return 0;
    }

    public float getReverseScore() {
        // TODO Auto-generated method stub
        return 0;
    }

    public float getReverseScore(String name) {
        // TODO Auto-generated method stub
        return 0;
    }

    public float getScore(String name) {
        // TODO Auto-generated method stub
        return 0;
    }
}
