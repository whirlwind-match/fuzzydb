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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;

/**
 * Implements a weak map to keep metadata in existence while object instances remain live.
 */
public class MetaMap extends ReferenceQueue<Object> {

	private HashMap<MetaMapKey, MetaObject<?>> map = new HashMap<MetaMapKey, MetaObject<?>>();
	
	synchronized public <E> void add(MetaObject<E> mo) {
		flush();
		if (mo == null) return;
		MetaMapKey mmk = new MetaMapKey(mo.getObject(), this);
		MetaObject<E> entry = new MetaObject<E>(mo.getRef(), mo.getVersion(), null);
		map.put(mmk, entry);
	}
	
	/**
	 * Get metadata for this object, returning a new MetaObject built with this object, as it may be a clone, passing
	 * equals() and hashcode() tests.
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked") // cast is safe as we know what's happening internally
	synchronized public <E> MetaObject<E> find(E o) {
		flush();
		MetaMapKey mmk = new MetaMapKey(o, this);
		MetaObject<E> entry = (MetaObject<E>) map.get(mmk);
		if (entry == null) return null;
		return new MetaObject<E>(entry.getRef(), entry.getVersion(), o);
	}
	
	/**
	 * Remove keys where the referred object is only weakly reachable.
	 * NOTE: Proof that this runs can seen by running WhirlwindDemo2 against a server instance.
	 */
	private void flush() {
		Reference<? extends Object> r;
		while ((r = poll()) != null) {
			MetaObject<?> mo = map.remove(r);
			assert(mo != null);
		}
	}

	public int size() {
		flush();
		return map.size();
	}
}
