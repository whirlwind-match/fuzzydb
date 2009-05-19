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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class NewObjectIds {
	

	/**
	 * Constrained map implementation to give us what we cannot express in one line (I think),
	 * thus allowing stronger typing in the subsequent code
	 * @param <OC>
	 */
	private class ClassToRefQueueMap<OC> extends HashMap<Class<OC>, Queue<RefImpl<OC>>> {
		private static final long serialVersionUID = 1L;
	}
	
	private final Map<String, ClassToRefQueueMap<?>> cache;
	
	public NewObjectIds() {
		cache = new HashMap<String, ClassToRefQueueMap<?>>();
	}
	
	@SuppressWarnings("unchecked") // cast (ClassToRefQueueMap<C>)
	public synchronized <C> void addRefs(String namespace, Class<C> clazz, int slice, int tableId, long firstOid, int count) {
		long oid = firstOid;
		ClassToRefQueueMap<C> part = (ClassToRefQueueMap<C>) cache.get(namespace);
		if (part == null) {
			part = new ClassToRefQueueMap<C>();
			cache.put(namespace, part);
		}
		Queue<RefImpl<C>> queue = part.get(clazz);
		if (queue == null) {
			queue = new LinkedList<RefImpl<C>>();
			part.put(clazz, queue);
		}
		assert(queue.size() == 0); // If we're adding some new refs, we should have run out
		for (int i = 0; i < count; i++) {
			RefImpl<C> ref = new RefImpl<C>(slice, tableId, oid++);
			queue.add(ref);
		}
	}
	
	/**
	 * Get the next reference to an object of the supplied class in the specified namespace
	 * @param <C>
	 * @param namespace
	 * @param clazz
	 * @return RefImpl<C>
	 */
	@SuppressWarnings("unchecked") // cast (ClassToRefQueueMap<C>)
	public synchronized <C> RefImpl<C> getNextRef(String namespace, Class<C> clazz) {
		ClassToRefQueueMap<C> part = (ClassToRefQueueMap<C>) cache.get(namespace);
		if (part == null) return null;
		Queue<RefImpl<C>> queue = part.get(clazz);
		if (queue == null) return null;	// returns null if class is unknown
		return queue.poll();	// returns null if queue empty
	}

}
