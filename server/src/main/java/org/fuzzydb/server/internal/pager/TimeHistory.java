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
 * Provides a filter for averaging together multiple time periods to produce a mean time.
 */
public class TimeHistory {

	public static final int defaultTimeHistory = 100;

	private final int timeHistory; // number of samples to keep in the rolling

	// histroy buffer

	private float avgTime;

	private int count;

	/**
	 * Create a new filter with the filter period set to the default number of samples.
	 */
	public TimeHistory() {
		timeHistory = defaultTimeHistory;
	}

	/**
	 * Create a new filter with the filter period set to the specified number of samples. The filter period must be
	 * greater than some minimum value.
	 * 
	 * @param timeHistory
	 */
	public TimeHistory(int timeHistory) {
		assert (timeHistory >= 10);
		this.timeHistory = timeHistory;
	}

	synchronized void time(float millisecs) {
		if (count < timeHistory) {
			count++;
		}
		avgTime -= (avgTime / count);
		avgTime += (millisecs / count);
	}

	synchronized float getAvgTime() {
		return avgTime;
	}
}
