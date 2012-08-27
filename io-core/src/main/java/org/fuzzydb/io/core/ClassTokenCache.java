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
package org.fuzzydb.io.core;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectIntHashMap;

import java.io.ObjectStreamClass;
import java.io.Serializable;



public class ClassTokenCache implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static class ClassCacheEntry implements Serializable {
		private static final long serialVersionUID = 1L;
		private final Class<?> clazz;
		private final long svUID;
		private final int storeId;
		private ObjectStreamClass cachedOsc = null;
		@Override
		public int hashCode() {
			return clazz.hashCode() + (int)svUID + storeId;
		}
		
		@Override
		public boolean equals(Object o) {
			ClassCacheEntry rhs = (ClassCacheEntry)o;
			return this.clazz.equals(rhs.clazz) && this.svUID == rhs.svUID && this.storeId == rhs.storeId;
		}

		public ObjectStreamClass getOsc(ClassLoaderInterface cli) throws ClassNotFoundException {
			if (cachedOsc != null) return cachedOsc;
			Class<?> c = cli.getClass(storeId, clazz.getName());
			ObjectStreamClass osc = ObjectStreamClass.lookup(c);
			
			if (osc.getSerialVersionUID() != svUID) {
				throw new ClassNotFoundException();	
			}
			cachedOsc = osc;
			return osc;
		}
		
		public ClassCacheEntry(Class<?> clazz, long serialVersionUID, int storeId) {
			super();
			this.clazz = clazz;
			this.svUID = serialVersionUID;
			this.storeId = storeId;
		}
	}
	
	
	private final TObjectIntHashMap<ClassCacheEntry> oscCacheToToken = new TObjectIntHashMap<ClassCacheEntry>();
	private final TIntObjectHashMap<ClassCacheEntry> oscCacheToOSC = new TIntObjectHashMap<ClassCacheEntry>();
	private int lastOscToken = 0;
	private final boolean autoAdd;

	public ClassTokenCache(boolean autoAdd) {
		this.autoAdd = autoAdd;
	}
	
	public int getOSCToken(int storeId, Class<?> clazz, long serialVersionUID) {
		ClassCacheEntry temp = new ClassCacheEntry(clazz, serialVersionUID, storeId);
		if (oscCacheToToken.containsKey(temp)) {
			int rval = oscCacheToToken.get(temp);
			return rval;
		}
		
			if (autoAdd) {
				return addOSCTokenPriv(storeId, clazz, serialVersionUID);
			} else {
				return -1;
			}
	}
	
	public int addOSCToken(int storeId, Class<?> clazz, long serialVersionUID) {
		assert(!autoAdd);
		return addOSCTokenPriv(storeId, clazz, serialVersionUID);
	}

	private int addOSCTokenPriv(int storeId, Class<?> clazz, long serialVersionUID) {
		ClassCacheEntry temp = new ClassCacheEntry(clazz, serialVersionUID, storeId);
		assert(!oscCacheToToken.containsKey(temp));
		lastOscToken++;
		oscCacheToToken.put(temp, lastOscToken);
		oscCacheToOSC.put(lastOscToken, temp);
		return lastOscToken;
	}
	
	public ClassCacheEntry lookupOSCToken(int token) {
		return oscCacheToOSC.get(token);
	}
	
}
