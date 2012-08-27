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
package org.fuzzydb.server.internal.pager;

/**
 * Records the cost of a purging a given page of a table, such that a pager can
 * prioritise purging of pages from memory.  Lower cost pages are pages that
 * are access less than higher cost pages and therefore better candidates
 * for purging.
 */
class PageOutCandidate implements Comparable<PageOutCandidate> {
	final PersistentPagedObject pageTable;

	final long pageId;

	final float cost;

	PageOutCandidate(float cost, PersistentPagedObject pageTable, long pageId) {
		this.cost = cost;
		this.pageTable = pageTable;
		this.pageId = pageId;
	}

	public int compareTo(PageOutCandidate o) {
		float dif = cost - o.cost;
		if (dif < 0)
			return -1;
		if (dif > 0)
			return 1;
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PageOutCandidate) {
			return this.compareTo((PageOutCandidate) obj) == 0;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException("Do not use with hashcode based collection");
	}
}