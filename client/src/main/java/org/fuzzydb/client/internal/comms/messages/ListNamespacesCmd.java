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

public class ListNamespacesCmd extends TransactionCommand {

	private static final long serialVersionUID = 1L;
	
    /** Default ctor for serialization libraries */
    private ListNamespacesCmd() {
        super(-1, -1, -1);
    }

	public ListNamespacesCmd(int storeId, int cid, int tid) {
		super(storeId, cid, tid);
	}
}
