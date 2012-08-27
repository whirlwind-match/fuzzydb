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

import java.util.LinkedList;

/**
 * Monitors the mean access frequency of a resource over a specified time window. The window is divided into quanta for
 * efficiency, with the loss of some precision.
 */
public class AccessHistory {

	/**
	 * A count of the number of access events for a given time quanta
	 */
	private static class AccessInfo {

		/**
		 * The quanta over which this data was collected
		 */
		private final long time;

		/**
		 * The number of times the resource was accessed in this quanta
		 */
		private int count;

		AccessInfo(long time, int count) {
			this.time = time;
			this.count = count;
		}

	}

	/**
	 * The duration in millis to record an access history for.
	 */
	public final static long defaultHistoryMillis = 1000 * 6 * 60;

	public final static long defaultQuantaMillis = defaultHistoryMillis / 100;

	private final long historyMillis;

	private final long quantaMillis;

	private long totalCount = 0;

	private final long created;

	private final LinkedList<AccessInfo> accessHistory = new LinkedList<AccessInfo>();

	/**
	 * Constructs a history with the default time window.
	 */
	public AccessHistory() {
		this.historyMillis = defaultHistoryMillis;
		this.quantaMillis = defaultQuantaMillis;
		created = System.currentTimeMillis();
	}

	/**
	 * Constructs a history with the specified time window and quanta.
	 * 
	 * @param historyMillis
	 *            The window to record over
	 * @param quantaMillis
	 *            The time quanta t work to. Bigger quantas are more efficient. There should probably be 100 quantas in
	 *            a history.
	 */
	public AccessHistory(long historyMillis, long quantaMillis) {
		assert (historyMillis >= 1000);
		assert (historyMillis / 100 >= quantaMillis);
		assert (quantaMillis > 0);
		this.historyMillis = historyMillis;
		this.quantaMillis = quantaMillis;
		created = System.currentTimeMillis();
	}

	/**
	 * Constructs a history with the specified time window and default quanta.
	 * 
	 * @param historyMillis
	 *            The window to record over
	 */
	public AccessHistory(long historyMillis) {
		assert (historyMillis >= 1000);
		this.historyMillis = historyMillis;
		this.quantaMillis = historyMillis / 100;
		created = System.currentTimeMillis();
	}

	/**
	 * Logs a resource access event. Call this each time the monitored resource is accessed.
	 */
	public synchronized void accessed() {
		long now = getQTime();
		if (accessHistory.isEmpty() || accessHistory.getLast().time != now) {
			accessHistory.add(new AccessInfo(now, 1));
		} else {
			assert(accessHistory.getLast().time == now);
			accessHistory.getLast().count++;
		}
		totalCount++;
		flushOld(System.currentTimeMillis());
	}

	/**
	 * Flush out old events that go beyond the end of the time window.
	 */
//	private void flushOld() {
//		flushOld(getQTime());
//	}

	/**
	 * Flush out old events that go beyond the end of the time window. Optimised version taking a time parameter, which
	 * should be approximately 'now'.
	 */
	private void flushOld(long now) {
		while (!accessHistory.isEmpty()) {
			if (accessHistory.getFirst().time + historyMillis >= now) {
				return;
			}
			totalCount -= accessHistory.getFirst().count;
			assert (totalCount >= 0);
			AccessInfo head = accessHistory.remove();
			assert(head.time + historyMillis < now);
		}
	}

	/**
	 * Calculates the access frequency of the recource being monitored.
	 * 
	 * @return Access frequency in Hz
	 */
	synchronized float getAccessFreq() {
		long now = System.currentTimeMillis();
		flushOld(now);
		if (totalCount == 0)
			return 0.0f;
		float freq = totalCount;
		if (created + historyMillis > now) {
			// Recent page, need to extrapolate data
			long view = now - created; // view is duration of known data
			if (view == 0)
				return Float.MAX_VALUE;
			float multiplier = historyMillis;
			multiplier /= view;
			freq *= multiplier;
		}
		return freq / (historyMillis / 1000f);
	}

	/**
	 * Gets the system time, quantized to the quantization period.
	 * 
	 * @return the current time, quantized
	 */
	private long getQTime() {
		long time = System.currentTimeMillis();
		long rem = time % quantaMillis;
		time -= rem;
		return time;
	}
}
