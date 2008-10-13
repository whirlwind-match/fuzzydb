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
package com.wwm.db.internal;

import java.io.Serializable;

public class CompactedObject<T extends Object> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final RefImpl<T> ref;
	private final int version;
	private final byte[] data;
	
	public CompactedObject(RefImpl<T> ref, int version, byte[] data) {
		this.ref = ref;
		this.version = version;
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public RefImpl<T> getRef() {
		return ref;
	}

	public int getVersion() {
		return version;
	}
}
