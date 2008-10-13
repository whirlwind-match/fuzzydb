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

@Deprecated // This isn't used?
@SuppressWarnings("serial")
public class NamespaceCommand extends TransactionCommand {
	
	private final String namespace;
	
	public NamespaceCommand(int storeId, int cid, int tid, String namespace) {
		super(storeId, cid, tid);
		this.namespace = namespace;
	}
	
	public String getNamespace() {
		return namespace;
	}	
}
