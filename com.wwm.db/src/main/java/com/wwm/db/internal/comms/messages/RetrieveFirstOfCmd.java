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

public class RetrieveFirstOfCmd extends TransactionCommand {

	private static final long serialVersionUID = 1L;

	private final String namespace;

	private final Class<?> forClass;
	
	public RetrieveFirstOfCmd(int storeId, String namespace, int cid, int tid, Class<?> forClass) {
		super(storeId, cid, tid);
		this.namespace = namespace;
		this.forClass = forClass;
	}

	public String getNamespace() {
		return namespace;
	}

	public Class<?> getForClass() {
		return forClass;
	}
}
