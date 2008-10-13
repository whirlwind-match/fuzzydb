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
package com.wwm.db.whirlwind.internal;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeMap;



/**
 * FIXME: Add JMX support so we can see size and benefit
 * FIXME: Make available as a singleton, rather than needing to pass it around.
 * FIXME: Resolve two diff AttributeCache impls into one.
 * 
 * Cache for allowing objects that are the same to be 'merged' after de-serialisation such that only 
 * one instance remains in memory.  This naturally assumes that the objects who's instances are being 
 * unified are immutable.
 */
public class AttributeCache {
	
	static private AttributeCache instance = new AttributeCache();

	static private class CacheEntry implements Comparable<CacheEntry>, Serializable {

		private static final long serialVersionUID = 3257002172427547448L;
		IAttribute value;
		
		public CacheEntry(IAttribute value) {
			this.value = value;
		}
		
		public int compareTo(CacheEntry rhs) {
			// Order first by attribute Id
			if (value.getAttrId() < rhs.getValue().getAttrId()) return -1;
			if (value.getAttrId() > rhs.getValue().getAttrId()) return 1;
			
			// then by class name
			int strcmp = value.getClass().getName().compareTo(rhs.getValue().getClass().getName());
			if (strcmp != 0) return strcmp;
			
			// then by value
			return value.compareAttribute(rhs.getValue());
		}

		public IAttribute getValue() {
			return value;
		}
		
	}
	
	/**
	 * Cache of strings (which are immutable) to be used to avoid duplicates.
	 * TODO: Consider using Soft or Weak refs.  Need to monitor size
	 * NOTE: using Map rather than set as set doesn't give us access to the object instance in the Set.
	 */
	private HashMap<String,String> stringCache = new HashMap<String,String>();
	
	private TreeMap<CacheEntry, IAttribute> cache = new TreeMap<CacheEntry, IAttribute>();
	private int successes = 0;
	
	public AttributeCache() {
		super();
	}

	static public AttributeCache getInstance(){
		return instance;
	}
	
	public IAttribute switchTo(IAttribute candidate) {
		CacheEntry newEntry = new CacheEntry(candidate);
		IAttribute cached = cache.get(newEntry);
		if (cached != null)
		{
			successes++;
			return cached;
		}
		cache.put(newEntry, candidate);
		return candidate;
	}
	
	public int getSuccesses() {
		return successes;
	}

	public int getSize() {
		return cache.size();
	}

	private int[] strSuccessesByKey = new int[256];
	private int[] strFailuresByKey = new int[256];
	
	/**
	 * Iterate over the map, and ensure that if the same string is in the cache,
	 * that the entry refers to the cached entry, rather than a new instance.
	 * @param stringMap
	 */
	public void mergeStrings(TIntObjectHashMap<String> stringMap) {

		if (stringMap == null) return;
		// FIXME: Investigate using String.intern()  what's it's overhead if we
		// do it on all strings.
		
		// FIXME: Keep track of which integer keys (they should be 0-255) merge well and which don't,
		// by tracking successes
		for (TIntObjectIterator<String> it = stringMap.iterator(); it.hasNext();) {
			it.advance();
			int key = it.key();
			assert key < 256;

			// Eliminate poor merging strings
			// FIXME: should also remove existing by having sep cache per key, which
			// we can just remove once can see it's a problem.  For now, we
			// just stop after it seems that we've got no dupls
			if (strFailuresByKey[key] > 5000 && strSuccessesByKey[key] < strFailuresByKey[key]){
				continue; // if after 5000 failures, we don't have more successes
			}
			
			
			String possDupl = it.value();
			String cachedString = stringCache.get(possDupl);
			if (cachedString != null) {
				it.setValue(cachedString); // If have one cached, use it
				strSuccessesByKey[key]++;
			} else {
				stringCache.put(possDupl, possDupl);
				strFailuresByKey[key]++;
			}
		}
	}
}
