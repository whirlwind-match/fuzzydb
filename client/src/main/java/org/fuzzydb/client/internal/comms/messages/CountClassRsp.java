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

@SuppressWarnings("serial")
public class CountClassRsp extends OkRsp {
	
	private final long count;
	
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private CountClassRsp() {
        this(0, 0, 0);
    }
	
	public CountClassRsp(int storeId, int cid, long count) {
		super(storeId, cid);
		this.count = count;
	}
	
	public long getCount() {
		return count;
	}
}
