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
public class CountClassCmd extends TransactionCommand {
	
	private final Class<?> clazz;
	private final String namespace;
	
	public CountClassCmd(int storeId, int cid, int tid, String namespace, Class<?> clazz) {
		super(storeId, cid, tid);
		this.clazz = clazz;
		this.namespace = namespace;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getNamespace() {
		return namespace;
	}
}
