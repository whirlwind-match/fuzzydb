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
public class RetrieveSingleRsp extends OkRsp {
	//private final CompactedObject object;
	private final Object object;
	
    /** Default ctor for serialization libraries */
	private RetrieveSingleRsp() {
        super(0, 0);
        this.object = null;
    }
	
	public RetrieveSingleRsp(int storeId, int cid, Object object) {
		super(storeId, cid);
		this.object = object;
	}

	public Object getCompactedObject() {
		return object;
	}
}
