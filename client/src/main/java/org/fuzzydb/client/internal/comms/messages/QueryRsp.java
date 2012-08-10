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

public class QueryRsp extends OkRsp {
	private static final long serialVersionUID = 1L;

	private ArrayList<Object> results;
	private boolean moreResults;
	
    /** Default ctor for serialization libraries */
    private QueryRsp() {
        super(-1, -1);
    }

	public QueryRsp(int storeId, int cid, ArrayList<Object> results, boolean moreResults) {
		super(storeId, cid);
		this.results = results;
		this.moreResults = moreResults;
	}

	public void getResults(ArrayList<Object> array) {
		array.addAll( results );
	}

	public ArrayList<Object> getResults() {
		return results;
	}

	public boolean isMoreResults() {
		return moreResults;
	}

}
