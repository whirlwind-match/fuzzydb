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
import java.util.Map;

import org.fuzzydb.client.internal.MetaObject;
import org.fuzzydb.client.internal.RefImpl;
import org.fuzzydb.io.core.messages.Loggable;


@SuppressWarnings("serial")
public class CommitCmd extends TransactionCommand implements Loggable {

	private final Map<String, ArrayList<MetaObject<?>>> created;
	private final ArrayList<MetaObject<?>> updated;
	private final ArrayList<RefImpl<?>> deleted;

    /** Default ctor for serialization libraries */
	private CommitCmd() {
        super(0, 0, 0);
        this.created = null;
        this.updated = null;
        this.deleted = null;
    }
	
	public CommitCmd(int storeId, int cid, int tid, Map<String, ArrayList<MetaObject<?>>> created, 
			ArrayList<MetaObject<?>> updated, ArrayList<RefImpl<?>> deleted) {
		super(storeId, cid, tid);
		this.created = created;
		this.updated = updated;
		this.deleted = deleted;
	}

	public Map<String, ArrayList<MetaObject<?>>> getCreated() {
		return created;
	}

	public ArrayList<RefImpl<?>> getDeleted() {
		return deleted;
	}

	public ArrayList<MetaObject<?>> getUpdated() {
		return updated;
	}

}
