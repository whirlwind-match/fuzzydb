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

import org.fuzzydb.core.exceptions.ArchException;
import org.fuzzydb.io.core.messages.Command;
import org.fuzzydb.io.core.messages.Loggable;


@SuppressWarnings("serial")
public class AllocNewIdsCmd extends Command implements Loggable {

	private final String clazz;
	private final String namespace;
	private final int count;
	
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private AllocNewIdsCmd() {
       super(0, 0);
       this.namespace = null;
       this.clazz = null;
       this.count = 0;
    }
	
	public AllocNewIdsCmd(int storeId, int cid, String namespace, Class<?> clazz, int count) {
		super(storeId, cid);
		this.namespace = namespace;
		this.clazz = clazz.getCanonicalName();
		this.count = count;
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

	public int getCount() {
		return count;
	}
}
