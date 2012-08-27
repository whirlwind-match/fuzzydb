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
package org.fuzzydb.server.internal.search;

import java.util.TreeSet;

/**
 * Abstract base for queues
 */
public abstract class Q<P extends Priority> {
	private TreeSet<P> q = new TreeSet<P>();
	
	/**
	 * Add item to the Q according to the priority
	 * @param priorityItem
	 */
	public void add(P priorityItem) {
		q.add(priorityItem);
	}

	public P best() {
		return q.last();	// gets highest priority
	}
	
	public P worst() {
		return q.first();
	}
	
	/**
	 * Get the highest priority, and remove from Q
	 * @return P
	 */
	public P pop() {
		P priorityItem = best();
		remove(priorityItem);
		return priorityItem;
	}
	
	public boolean isEmpty() {
		return q.isEmpty();
	}
	
	public boolean remove( P priorityItem ) {
		return q.remove( priorityItem );
	}

	public int size() {
		return q.size();
	}
	
	@Override
	public String toString() {
		return q.toString();
	}

}
