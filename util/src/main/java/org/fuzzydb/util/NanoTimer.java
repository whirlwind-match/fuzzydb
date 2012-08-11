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
package org.fuzzydb.util;

public class NanoTimer {

	private long startNanos = System.nanoTime(); // start time
	private long lapTimeNanos = System.nanoTime(); // last lap Time

	/**
	 * Timer.  Get elapsed time since NanoTimer was created
	 * @return milliseconds since NanoTimer was created.
	 */
	public float getMillis() {
		return (System.nanoTime() - startNanos) / 1000000.0f;
	}
	
	
	/**
	 * Lap Timer.  Returns time since getLap..() function was last called (or time since started)
	 * @return milliseconds since start or last getLap..() call whichever is later
	 */
	public long getLapMillis() {
		long now = System.nanoTime();
		long millis = (now - lapTimeNanos) / 1000000;
		lapTimeNanos = now; // update last value
		
		return millis;
	}
	
}
