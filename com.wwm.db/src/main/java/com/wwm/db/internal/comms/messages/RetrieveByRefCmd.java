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

import com.wwm.db.Ref;

@SuppressWarnings("serial")
public class RetrieveByRefCmd extends TransactionCommand {

	private final Ref ref;

    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private RetrieveByRefCmd() {
        this(0, 0, 0, null);
    }

	public RetrieveByRefCmd(int storeId, int cid, int tid, Ref ref) {
		super(storeId, cid, tid);
		this.ref = ref;
	}

	public Ref getRef() {
		return ref;
	}

}
