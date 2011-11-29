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

import org.springframework.util.Assert;

import com.wwm.db.Ref;

public final class RefImpl<T> implements Ref<T>, Serializable, Comparable<RefImpl<T>> {
	
	private static final long serialVersionUID = 1L;
	protected final long oid;  // could go past 32 bit, as we could be using single slice to log web page hits... 10 million hits per day only lasts just over a year 
    protected final int table;
	protected final int slice;
	
	public RefImpl(int slice, int table, long oid) {
		this.slice = slice;
        this.table = table;
        this.oid = oid;
	}
	
	public RefImpl(RefImpl<T> ref) {
		slice = ref.slice;
		table = ref.table;
		oid = ref.oid;
	}

	public RefImpl(Ref<T> ref) {
		RefImpl<T> ri = (RefImpl<T>)ref;
		slice = ri.slice;
		table = ri.table;
		oid = ri.oid;
	}
	
	public long getOid() {
		return oid;
	}

	public int getSlice() {
		return slice;
	}

	public int getTable() {
		return table;
	}

	@Override
	public int hashCode() {
		return (int)(oid * table * slice);
	}
	
	@SuppressWarnings("unchecked") // For RefImpl<T> cast. We know there's only one Impl
	@Override
	public boolean equals(Object o) {
		RefImpl<T> rhs = (RefImpl<T>) o;
		return oid == rhs.oid && table == rhs.table && slice == rhs.slice;
	}
	
	public int compareTo(RefImpl<T> rhs) {
		// different instances with same value must be equal in maps
		if (this.oid != rhs.oid) {
			return this.oid < rhs.oid ? -1 : 1;
		}
		if (this.table != rhs.table) {
			return this.table < rhs.table ? -1 : 1;
		}
		if (this.slice != rhs.slice) {
			return this.slice < rhs.slice ? -1 : 1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return table + ":" + oid;  // slice + ":" + table + ":" + oid;   
	}
	
	/**
	 * For conversion to String
	 */
	public String asString() {
		return slice + "_" + table + "_" + oid;
	}

	/**
	 * For conversion from String
	 */
	static public <E> Ref<E> valueOf(String refAsString) {
		String[] parts = refAsString.split("_");
		Assert.state(parts.length == 3, "Illegal ref " + refAsString);
		
		int p1 = Integer.valueOf(parts[0]);
		int p2 = Integer.valueOf(parts[1]);
		int p3 = Integer.valueOf(parts[2]);
		
		return new RefImpl<E>(p1, p2, p3);
	}
}
