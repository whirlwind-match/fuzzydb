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

import com.wwm.db.internal.RetrieveSpecResultImpl;

public class RetrieveBySpecRsp extends OkRsp {

	private static final long serialVersionUID = 1L;

	private final RetrieveSpecResultImpl result;

	public RetrieveBySpecRsp(int storeId, int cid, RetrieveSpecResultImpl result) {
		super(storeId, cid);
		this.result = result;
	}

	public RetrieveSpecResultImpl getResult() {
		return result;
	}
	
}
