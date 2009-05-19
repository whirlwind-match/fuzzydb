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
package com.wwm.db.internal.comms.messages;

import java.util.ArrayList;

import com.wwm.db.internal.ResultImpl;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.marker.IWhirlwindItem;
import com.wwm.db.query.Result;
import com.wwm.model.attributes.Score;


public class WWSearchOkayRsp extends OkRsp {

	private static final long serialVersionUID = 1L;

	private ArrayList<IWhirlwindItem> results;
	private ArrayList<Score> scores;
	private boolean moreResults;
	
	public WWSearchOkayRsp(int storeId, int cid, ArrayList<IWhirlwindItem> results, ArrayList<Score> scores, boolean moreResults) {
		super(storeId, cid);
		this.results = results;
		this.moreResults = moreResults;
		this.scores = scores;
	}

	public <E extends Object> void getResults(Class<E> clazz, ArrayList<Result<E>> array) {
		int count = 0;
		for (IAttributeContainer i : results) {
			ResultImpl<E> result = new ResultImpl<E>( clazz.cast(i), scores.get(count++) );
			array.add( result );
		}
	}

	public boolean isMoreResults() {
		return moreResults;
	}
}
