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
package com.wwm.db.internal.pager;


class PageScore implements Comparable<PageScore> {
	final PersistentPagedObject pageTable;

	final long pageId;

	final float score;

	PageScore(float score, PersistentPagedObject pageTable, long pageId) {
		this.score = score;
		this.pageTable = pageTable;
		this.pageId = pageId;
	}

	public int compareTo(PageScore o) {
		float dif = score - o.score;
		if (dif < 0)
			return -1;
		if (dif > 0)
			return 1;
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PageScore) {
			return this.compareTo((PageScore) obj) == 0;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// this ensures that we don't break hashCode rules where equal objects should have equal hashCodes
		assert false : "hashCode not designed"; 
		return 0; // any arbitrary constant will do 
	}
}