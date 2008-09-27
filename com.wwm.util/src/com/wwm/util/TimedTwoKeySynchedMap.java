/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


public class TimedTwoKeySynchedMap <K1 extends Comparable<K1>, K2 extends Comparable<K2>, V> {

	private TwoKeySynchedMap<K1, K2, V> map = new TwoKeySynchedMap<K1, K2, V>();
	private TreeMap<K1, Date> accessed = new TreeMap<K1, Date>();
	private TreeMap<K1, Date> created = new TreeMap<K1, Date>();
	
	public synchronized Collection<K1> findInactive(Date time) {
		return findOlder(time, accessed);
	}

	public synchronized Collection<K1> findExpired(Date time) {
		return findOlder(time, created);
	}
	
	private Collection<K1> findOlder(Date time, TreeMap<K1, Date> map) {
		ArrayList<K1> al = null;
		for (Map.Entry<K1, Date> entry : map.entrySet()) {
			if (entry.getValue().before(time)) {
				if (al == null) {
					al = new ArrayList<K1>();
				}
				al.add(entry.getKey());
			}
		}
		return al;
	}
	
	private void update(K1 key1) {
		accessed.put(key1, new Date());
	}

	private Date remove(K1 key1) {
		accessed.remove(key1);
		return created.remove(key1);
	}
	
	public synchronized void clear() {
		accessed.clear();
		created.clear();
		map.clear();
	}

	public synchronized boolean containsk1(K1 key1) {
		update(key1);
		return map.containsk1(key1);
	}

//	public synchronized boolean containsk2(K2 key2) {
//		update(map.k2tok1(key2));
//		return map.containsk2(key2);
//	}

	public synchronized V getk1(K1 key1) {
		update(key1);
		return map.getk1(key1);
	}

//	public synchronized V getk2(K2 key2) {
//		update(map.k2tok1(key2));
//		return map.getk2(key2);
//	}

	public synchronized void put(K1 key1, K2 key2, V value) {
		created.put(key1, new Date());
		update(key1);
		map.put(key1, key2, value);
	}

	public synchronized V removek1(K1 key1) {
		if (remove(key1) == null) return null;
		return map.removek1(key1);
	}

	public synchronized void removek2(K2 key2) {
		TreeSet<K1> ak = map.k2tok1(key2);
		if (ak == null) return;
		for (K1 k1 : ak) {
			remove(k1);
		}
		map.removek2(key2);
	}

	public TimedTwoKeySynchedMap() {
		super();
	}

}
