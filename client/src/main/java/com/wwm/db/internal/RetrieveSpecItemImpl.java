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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.wwm.db.query.RetrieveSpecItem;

public class RetrieveSpecItemImpl implements RetrieveSpecItem, Serializable {

	private static final long serialVersionUID = 1L;

	private final HashSet<Object> keys = new HashSet<Object>();
	private final String fieldName;

	public RetrieveSpecItemImpl(final String fieldName) {
		super();
		this.fieldName = fieldName;
	}
	
	public void add(Object key) {
		keys.add(key);
	}

	public void addAll(Collection<Object> keys) {
		keys.addAll(keys);
	}

	public String getFieldName() {
		return fieldName;
	}

	public Set<Object> getKeys() {
		return keys;
	}


}
