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

import com.wwm.io.core.messages.Command;

@SuppressWarnings("serial")
public class BeginTransactionCmd extends Command {

	private final Command payload;
	private final int tid;

	public BeginTransactionCmd(int storeId, int cid, int tid, Command payload) {
		super(storeId, cid);
		this.payload = payload;
		this.tid = tid;
	}

	public Command getPayload() {
		return payload;
	}

	public int getTid() {
		return tid;
	}
}
