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

import com.wwm.io.packet.messages.Command;
import com.wwm.io.packet.messages.Loggable;

@SuppressWarnings("serial")
public class AllocNewIdsCmd extends Command implements Loggable {

	private final Class<?> clazz;
	private final String namespace;
	private final int count;
	
	public AllocNewIdsCmd(int storeId, int cid, String namespace, Class<?> clazz, int count) {
		super(storeId, cid);
		this.namespace = namespace;
		this.clazz = clazz;
		this.count = count;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getNamespace() {
		return namespace;
	}

	public int getCount() {
		return count;
	}
}
