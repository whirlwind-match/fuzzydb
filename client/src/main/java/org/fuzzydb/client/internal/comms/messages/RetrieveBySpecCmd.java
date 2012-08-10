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

import com.wwm.db.query.RetrieveSpec;

public class RetrieveBySpecCmd extends TransactionCommand {

	private static final long serialVersionUID = 1L;

	private final RetrieveSpec spec;
	private final String namespace;

    /** Default ctor for serialization libraries */
    private RetrieveBySpecCmd() {
        super(-1, -1, -1);
        this.spec = null;
        this.namespace = null;
    }

	public RetrieveBySpecCmd(int storeId, String namespace, int cid, int tid, RetrieveSpec spec) {
		super(storeId, cid, tid);
		this.spec = spec;
		this.namespace = namespace;
	}

	public RetrieveSpec getSpec() {
		return spec;
	}

	public String getNamespace() {
		return namespace;
	}
}
