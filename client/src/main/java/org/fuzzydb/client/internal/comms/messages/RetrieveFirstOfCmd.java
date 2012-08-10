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

public class RetrieveFirstOfCmd extends TransactionCommand {

	private static final long serialVersionUID = 1L;

	private final String namespace;

	private final String forClass;
	
	/** Default ctor for serialization libraries */
    private RetrieveFirstOfCmd() {
       super(0, 0, 0);
       this.namespace = null;
       this.forClass = null;
    }

	public RetrieveFirstOfCmd(int storeId, String namespace, int cid, int tid, Class<?> forClass) {
		super(storeId, cid, tid);
		this.namespace = namespace;
		this.forClass = forClass.getCanonicalName();
	}

	public String getNamespace() {
		return namespace;
	}

	public Class<?> getForClass() {
		try {
            return Class.forName(forClass);
        }
        catch (ClassNotFoundException e) {
            throw new ArchException("Class " + forClass + " not not on (probably server) classpath", e);
        }
	}
}
