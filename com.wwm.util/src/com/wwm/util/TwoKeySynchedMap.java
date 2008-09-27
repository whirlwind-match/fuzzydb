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

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A key-value treemap that uses 2 keys. You need both keys to put, but either key will do for get or remove.
 * Synchronized, although be careful to synch on the TwoKeySynchedMap instance if you want to iterate
 * over the values() collection.
 * @author ac
 *
 * @param <K1> First key, must extend <code>Comparable<K1></code>
 * @param <K2> Second key, must extend <code>Comparable<K2></code>
 * @param <V> Value to be stored
 */
public class TwoKeySynchedMap <K1 extends Comparable<K1>, K2 extends Comparable<K2>, V> {

//	private class Tuple <A,B> {
//		public Tuple(A key, B value) {
//			this.key = key;
//			this.value = value;
//		}
//		A key;
//		B value;
//	}
	
	private TreeMap<K1, V> map1 = new TreeMap<K1, V>();
	private TreeMap<K2, HashSet<V>> map2 = new TreeMap<K2, HashSet<V>>();
	private TreeMap<K1, K2> k1tok2 = new TreeMap<K1, K2>();
	private TreeMap<K2, TreeSet<K1>> k2tok1 = new TreeMap<K2, TreeSet<K1>>();
	
	public TwoKeySynchedMap() {
		super();
	}

	public synchronized TreeSet<K1> k2tok1(K2 key2) {
		assert(key2 != null);		
		return k2tok1.get(key2);
	}
	
	public synchronized void clear() {
		map1.clear();
		map2.clear();
		k1tok2.clear();
		k2tok1.clear();
	}
	
	public synchronized boolean containsk1(K1 key1) {
		assert(key1 != null);
		return map1.containsKey(key1);
	}

	public synchronized boolean containsk2(K2 key2) {
		assert(key2 != null);
		return map2.containsKey(key2);
	}
	
	public synchronized V getk1(K1 key1) {
		assert(key1 != null);
		return map1.get(key1);
	}

//	public synchronized V getk2(K2 key2) {
//		assert(key2 != null);
//		return map2.get(key2);
//	}
	
	private void removeK1FromK2ToK1(K2 key2, K1 key1) {
		TreeSet<K1> kset = k2tok1.get(key2);
		kset.remove(key1);
		if (kset.isEmpty()) k2tok1.remove(key2);
	}
	
	private void removeVFromMap2(K2 key2, V value) {
		HashSet<V> vset = map2.get(key2);
		vset.remove(value);
		if (vset.isEmpty()) map2.remove(key2);
	}
	
	public synchronized V removek1(K1 key1) {
		assert(key1 != null);
		K2 key2 = k1tok2.remove(key1);
		
		removeK1FromK2ToK1(key2, key1);
		V value = map1.remove(key1);
		removeVFromMap2(key2, value);
				
		return value;
	}

	public synchronized void removek2(K2 key2) {
		assert(key2 != null);
		TreeSet<K1> a = k2tok1.remove(key2);
		for (K1 key1 : a) {
			k1tok2.remove(key1);
			map1.remove(key1);
		}
		map2.remove(key2);
	}
	
	public synchronized void put(K1 key1, K2 key2, V value) {
		assert(key1 != null);
		assert(key2 != null);
		assert(value != null);
		map1.put(key1, value);
		HashSet<V> a;
		if (!map2.containsKey(key2)) {
			a = new HashSet<V>();
			map2.put(key2, a);
		} else {
			a = map2.get(key2);
		}
		a.add(value);
		k1tok2.put(key1, key2);
		
		TreeSet<K1> ak;
		if (!k2tok1.containsKey(key2)) {
			ak = new TreeSet<K1>();
			k2tok1.put(key2, ak);
		} else {
			ak = k2tok1.get(key2);
		}
		ak.add(key1);
	}
	
	public synchronized Collection<V> values() {
		return map1.values();
	}
}
