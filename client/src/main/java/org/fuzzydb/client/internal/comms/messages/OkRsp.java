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

import org.fuzzydb.io.core.messages.Response;

@SuppressWarnings("serial")
public class OkRsp extends Response {

    /** Default ctor for serialization libraries */
    private OkRsp() {
        super(0, 0);
    }
    
	public OkRsp(int storeId, int cid) {
		super(storeId, cid);
	}

}
