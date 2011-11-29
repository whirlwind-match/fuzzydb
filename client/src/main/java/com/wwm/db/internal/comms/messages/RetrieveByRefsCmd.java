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

import java.util.Collection;

import com.wwm.db.Ref;
import com.wwm.db.internal.RefImpl;

@SuppressWarnings("serial")
public class RetrieveByRefsCmd<T> extends TransactionCommand {
	private final RefImpl<?>[] refs;

    /** Default ctor for serialization libraries */
    private RetrieveByRefsCmd() {
        super(-1, -1, -1);
        this.refs = null;
    }

	public RetrieveByRefsCmd(int storeId, int cid, int tid, Collection<Ref<T>> refs) {
		super(storeId, cid, tid);
		// Following involves a seemingly unnecessary System.arrayCopy
		// but we'd rather do on client that server.
		this.refs = refs.toArray( new RefImpl[0] );
	}

	@SuppressWarnings("unchecked")
    public RefImpl<T>[] getRefs() {
		return (RefImpl<T>[]) refs;
	}
}
