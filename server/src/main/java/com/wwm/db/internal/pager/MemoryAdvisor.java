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

/**
 * Monitors memory and helps advise what action to take
 */
class MemoryAdvisor {
	private final long lowWaterMark;
	@SuppressWarnings("unused")
	private final long midWaterMark;
	private final long highWaterMark;
	
	private long max; // max heap size (e.g. -Xmx200M gives us 200M
	private long free; // amount of currently allocated heap (i.e. total) that is free 
	private long total;

	static final int MEG = 1024 * 1024;

	/**
	 * Initialise with given config
	 * @param low - min amount of free memory, in megabytes
	 * @param mid - mid in megabytes
	 * @param high - high in megabytes
	 */
	public MemoryAdvisor(float low, float mid, float high) {
		lowWaterMark = (long) (low * MEG);
		midWaterMark = (long) (mid * MEG);
		highWaterMark = (long) (high * MEG);
	}

	/**
	 * The amount of memory available up to the configured maximum heap size.
	 */
	public long getFreeMem() {
		long usedMem = total - free;
		return max - usedMem;
	}

	public long getTotal() {
		return total;
	}
	
	public void update() {
		max = Runtime.getRuntime().maxMemory();
		free = Runtime.getRuntime().freeMemory();
		total = Runtime.getRuntime().totalMemory();
	}

	public boolean isMemoryLow() {
		return getFreeMem() < lowWaterMark;
	}

	public boolean isAboveHigh() {
		return getFreeMem() > highWaterMark;
	}

	@Override
	public String toString() {
		return "MemoryAdvisor [lowWaterMark=" + lowWaterMark / MEG
				+ ", midWaterMark=" + midWaterMark  / MEG + ", highWaterMark="
				+ highWaterMark  / MEG + ", max=" + max  / MEG + ", free=" + free  / MEG
				+ ", total=" + total  / MEG + "]";
	}
	
	
}