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

import com.wwm.io.core.messages.Command;

public class OpenStoreCmd extends Command {

	private static final long serialVersionUID = 1L;

	private String openStoreName;

    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private OpenStoreCmd() {
        super(-1, -1);
    }
	
	public OpenStoreCmd(int cid, String storeName) {
		super(0, cid);
		this.openStoreName = storeName;
	}

	public String getStoreName() {
		return openStoreName;
	}
}
