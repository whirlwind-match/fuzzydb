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

@SuppressWarnings("serial")
public class ListStoresRsp extends OkRsp {
	private final Collection<String> storeNames;
	
    /** Default ctor for serialization libraries */
    private ListStoresRsp() {
        super(-1, -1);
        this.storeNames = null;
    }

	public ListStoresRsp(int cid, Collection<String> storeNames) {
		super(0, cid);
		this.storeNames = storeNames;
	}

	public Collection<String> getStoreNames() {
		return storeNames;
	}
}
