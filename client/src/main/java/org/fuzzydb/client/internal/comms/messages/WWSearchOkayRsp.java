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
package org.fuzzydb.client.internal.comms.messages;

import java.util.ArrayList;

import org.fuzzydb.client.internal.MetaCache;
import org.fuzzydb.client.internal.MetaObject;
import org.fuzzydb.client.internal.ResultImpl;
import org.fuzzydb.client.marker.IWhirlwindItem;
import org.fuzzydb.core.query.Result;
import org.fuzzydb.dto.attributes.Score;



public class WWSearchOkayRsp extends OkRsp {

	private static final long serialVersionUID = 1L;

	private ArrayList<MetaObject<? extends IWhirlwindItem>> results;
	private ArrayList<Score> scores;
	private boolean moreResults;
	
    /** Default ctor for serialization libraries */
    private WWSearchOkayRsp() {
        super(0, 0);
    }

	public WWSearchOkayRsp(int storeId, int cid, ArrayList<MetaObject<? extends IWhirlwindItem>> results, ArrayList<Score> scores, boolean moreResults) {
		super(storeId, cid);
		this.results = results;
		this.moreResults = moreResults;
		this.scores = scores;
	}

	public <E extends Object> void getResults(ArrayList<Result<E>> array, MetaCache metaCache) {
		int count = 0;
		for (MetaObject<? extends IWhirlwindItem> i : results) {
			metaCache.addToMetaCache(i);
			@SuppressWarnings("unchecked")
            ResultImpl<E> result = new ResultImpl<E>( (E) i.getObject(), scores.get(count++) );
			array.add( result );
		}
	}

	public boolean isMoreResults() {
		return moreResults;
	}
}
