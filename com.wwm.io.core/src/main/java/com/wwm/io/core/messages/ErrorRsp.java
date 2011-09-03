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
package com.wwm.io.core.messages;

import com.wwm.db.core.exceptions.ArchException;

@SuppressWarnings("serial")
public class ErrorRsp extends Response {
	private final ArchException e;
	
	public ErrorRsp(int storeId, int cid, ArchException e) {
		super(storeId, cid);
		this.e = e;
	}

	public ArchException getError() {
		return e;
	}
	
	@Override
	public String toString() {
		return e.getClass().getSimpleName() + " : " + e.getMessage();
	}
}
