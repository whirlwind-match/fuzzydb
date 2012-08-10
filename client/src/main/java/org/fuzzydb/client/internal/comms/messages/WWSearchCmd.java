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

import com.wwm.db.whirlwind.SearchSpec;

public class WWSearchCmd extends TransactionCommand {

	private static final long serialVersionUID = 1L;

	private String namespace;

	private final SearchSpec search;

	private int queryId;

	private boolean wantNominee;

	private int fetchSize;

    /** Default ctor for serialization libraries */
    private WWSearchCmd() {
        super(0, 0, 0);
        this.search = null;
    }

	public WWSearchCmd(int storeId, int cid, int tid, String namespace, int queryId, boolean wantNominee, int fetchSize, SearchSpec search) {
		super(storeId, cid, tid);
		this.namespace = namespace;
		this.queryId = queryId;
		this.wantNominee = wantNominee;
		this.fetchSize = fetchSize;
		this.search = search;
	}

	public String getNamespace() {
		return namespace;
	}
	
	public int getQueryId() {
		return queryId;
	}
	
	public boolean getWantNominee() {
		return wantNominee;
	}
	
	public int getFetchSize() {
		return fetchSize;
	}
	
	public SearchSpec getSearchSpec() {
		return search;
	}
}
