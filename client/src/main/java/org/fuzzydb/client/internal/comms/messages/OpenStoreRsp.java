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

@SuppressWarnings("serial")
public class OpenStoreRsp extends OkRsp {
	private final int openedStoreId;
	private final String openedStoreName;
	
    /** Default ctor for serialization libraries */
    private OpenStoreRsp() {
        super(-1, -1);
        this.openedStoreId = 0;
        this.openedStoreName = null;
    }

	public OpenStoreRsp(int cid, String storeName, int storeId) {
		super(0, cid);
		this.openedStoreId = storeId;
		this.openedStoreName = storeName; 
	}

	public int getOpenedStoreId() {
		return openedStoreId;
	}

	public String getOpenedStoreName() {
		return openedStoreName;
	}
}
