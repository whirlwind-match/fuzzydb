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

import org.fuzzydb.io.core.messages.Command;

@SuppressWarnings("serial")
public class TransactionCommand extends Command {
	final int tid;
	
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private TransactionCommand() {
        super(-1, -1);
        this.tid = 0;
    }

	protected TransactionCommand(int storeId, int cid, int tid) {
		super(storeId, cid);
		this.tid = tid;
	}

	public int getTid() {
		return tid;
	}
}
