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

import com.wwm.db.core.exceptions.ArchException;

@SuppressWarnings("serial")
public class CountClassCmd extends TransactionCommand {
	
	private final String clazz;
	private final String namespace;
	
    /** Default ctor for serialization libraries */
    private CountClassCmd() {
        super(0, 0, 0);
        this.clazz = null;
        this.namespace = null;
    }

	public CountClassCmd(int storeId, int cid, int tid, String namespace, Class<?> clazz) {
		super(storeId, cid, tid);
		this.clazz = clazz.getCanonicalName();
		this.namespace = namespace;
	}

	public Class<?> getClazz() {
        try {
            return Class.forName(clazz);
        }
        catch (ClassNotFoundException e) {
            throw new ArchException("Class " + clazz + " not not on (probably server) classpath", e);
        }
	}

	public String getNamespace() {
		return namespace;
	}
}
