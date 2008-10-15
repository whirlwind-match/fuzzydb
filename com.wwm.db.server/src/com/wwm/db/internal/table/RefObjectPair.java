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
package com.wwm.db.internal.table;

import com.wwm.db.internal.RefImpl;

/**
 * @param <RT> Type of object that RefImpl refers to... which may be within T rather than T itself 
 * (e.g. T = VersionedObject<RT>
 * @param <T> e.g. VersionedObject<RT>
 */
public class RefObjectPair<RT, T> { 
	public RefImpl<RT> ref;
	public T obj;

	public RefObjectPair(RefImpl<RT> ref, T object) {
		this.ref = ref;
		this.obj = object;
	}
}