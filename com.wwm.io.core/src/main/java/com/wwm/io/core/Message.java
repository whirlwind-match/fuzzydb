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
package com.wwm.io.core;

import java.io.Serializable;

public abstract class Message implements Serializable {

	protected int storeId;
	protected int cid;
	
	public Message() {
		super();
	}

	public Message(int storeId, int cid) {
		this.storeId = storeId;
		this.cid = cid;
	}
	

	public int getStoreId() {
		return storeId;
	}

	public int getCommandId() {
		return cid;
	}
}
