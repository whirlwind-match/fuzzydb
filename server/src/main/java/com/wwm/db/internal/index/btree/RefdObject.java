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
package com.wwm.db.internal.index.btree;

import java.io.Serializable;

import org.fuzzydb.client.internal.RefImpl;


public class RefdObject implements Serializable {

	private static final long serialVersionUID = 1L;

	public final RefImpl ref;
	public final Object object;
	
	public RefdObject(RefImpl ref, Object object) {
		this.ref = ref;
		this.object = object;
	}
}
