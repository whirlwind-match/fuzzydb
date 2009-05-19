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
public class AllocNewIdsRsp extends OkRsp {

	private final int slice;
	private final long firstOid;
	private final int tableid;
	private final int count;

	public AllocNewIdsRsp(int storeId, int cid, int slice, int tableId, long firstOid, int count) {
		super(storeId, cid);
		this.slice = slice;
		this.tableid = tableId;
		this.firstOid = firstOid;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public long getFirstOid() {
		return firstOid;
	}

	public int getSlice() {
		return slice;
	}

	public int getTableid() {
		return tableid;
	}

}
