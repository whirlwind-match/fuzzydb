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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.wwm.db.query.RetrieveSpec;
import com.wwm.db.query.RetrieveSpecItem;

public class RetrieveSpecImpl implements RetrieveSpec, Serializable {

	private static final long serialVersionUID = 1L;

	private final HashMap<Class<? extends Object>, HashMap<String, RetrieveSpecItem>> map = new HashMap<Class<? extends Object>, HashMap<String, RetrieveSpecItem>>();
	
	public void add(Class<? extends Object> clazz, String fieldName, Object key) {
		HashMap<String, RetrieveSpecItem> submap = map.get(clazz);
		if (submap == null) {
			submap = new HashMap<String, RetrieveSpecItem>();
			map.put(clazz, submap);
		}
		
		RetrieveSpecItem item = submap.get(fieldName);
		if (item == null) {
			item = new RetrieveSpecItemImpl(fieldName);
			submap.put(fieldName, item);
		}

		item.add(key);
	}

	public void addAll(Class<? extends Object> clazz, String fieldName, Collection<Object> keys) {
		HashMap<String, RetrieveSpecItem> submap = map.get(clazz);
		if (submap == null) {
			submap = new HashMap<String, RetrieveSpecItem>();
			map.put(clazz, submap);
		}
		
		RetrieveSpecItem item = submap.get(fieldName);
		if (item == null) {
			item = new RetrieveSpecItemImpl(fieldName);
			submap.put(fieldName, item);
		}
		
		item.addAll(keys);
	}

	public Set<Class<? extends Object>> getClasses() {
		return map.keySet();
	}

	public Map<String, RetrieveSpecItem> getSpecs(Class<? extends Object> clazz) {
		return map.get(clazz);
	}

}
