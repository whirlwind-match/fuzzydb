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

@SuppressWarnings("serial")
public class CreateStoreRsp extends OkRsp {
	private final int createStoreId;
	private final String createStoreName;
	
    /** Default ctor for serialization libraries */
    private CreateStoreRsp() {
       super(-1, -1);
       this.createStoreId = 0;
       this.createStoreName = null; 
    }

	public CreateStoreRsp(int cid, String storeName, int storeId) {
		super(0, cid);
		this.createStoreId = storeId;
		this.createStoreName = storeName; 
	}

	public int getNewStoreId() {
		return createStoreId;
	}

	public String getNewStoreName() {
		return createStoreName;
	}
}
