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
import org.fuzzydb.io.core.messages.Loggable;


@SuppressWarnings("serial")
public class BeginAndCommitCmd extends BeginTransactionCmd implements Loggable {
    
    /** Default ctor for serialization libraries */
    private BeginAndCommitCmd() {
        super(0, 0, 0, null);
    }

	public BeginAndCommitCmd(int storeId, int cid, int tid, Command payload) {
		super(storeId, cid, tid, payload);
	}

}
