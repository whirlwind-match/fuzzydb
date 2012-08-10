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
package org.fuzzydb.client.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.fuzzydb.core.query.RetrieveSpecResult;


public class RetrieveSpecResultImpl implements RetrieveSpecResult, Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<Class<?>, Map<Object, MetaObject<?>>> map = new HashMap<Class<?>, Map<Object, MetaObject<?>>>();
	
	public <E> void put(Class<E> clazz, Object key, MetaObject<E> value) {
		Map<Object, MetaObject<?>> submap = map.get(clazz);
		if (submap == null) {
			submap = new HashMap<Object, MetaObject<?>>();
			map.put(clazz, submap);
		}
		submap.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <E> E get(Class<E> clazz, Object key) {
		Map<Object, MetaObject<?>> submap = map.get(clazz);
		if (submap == null) return null;
		
		MetaObject<E> mo = (MetaObject<E>) submap.get(key);
		
		if (mo == null) {
			return null;
		}
		return mo.getObject();
	}
	
	public void addAllToMetaCache(StoreImpl store) {
		for (Map<Object, MetaObject<?>> maps : map.values()) {
			for (MetaObject<?> mo : maps.values()) {
				if (mo != null) {
					store.addToMetaCache(mo);
				}
			}
		}
	}

}
