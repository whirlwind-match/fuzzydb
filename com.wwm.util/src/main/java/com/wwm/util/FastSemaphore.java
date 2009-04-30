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
package com.wwm.util;

/**Fast, lightweight Semaphore implementation
 * Stupid and unfair, but lean and mean.
 * @author ac
 *
 */
public class FastSemaphore {
	
	private int permits;
	private int multiWait = 0;
	
	public FastSemaphore(int permits) {
		this.permits = permits;
	}

	public FastSemaphore() {
		this.permits = 0;
	}
	
	/**
	 * Acquire all permits
	 */
	public synchronized void acquireUninterruptibly() {
		for (;;) {
			try {
				acquire();
				return;
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
	}

	public synchronized void acquireUninterruptibly(int count) {
		assert(count > 0);
		for (;;) {
			try {
				acquire(count);
				return;
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
	}
	
	public synchronized void acquire() throws InterruptedException {
		for (;;) {
			if (permits > 0) {
				permits--;
				return;
			}
			super.wait();
		}
	}

	public synchronized void acquire(int count) throws InterruptedException {
		assert(count > 0);
		for (;;) {
			if (permits >= count) {
				permits -= count;
				return;
			}
			if (count > 1) {
				multiWait++;
			}
			super.wait();
			if (count > 1) {
				multiWait--;
			}
		}
	}

	public synchronized boolean tryAcquire() {
		if (permits > 0) {
			permits--;
			return true;
		}
		return false;
	}

	public synchronized boolean tryAcquire(int count) {
		assert(count>0);
		if (permits >= count) {
			permits -= count;
			return true;
		}
		return false;
	}
	
	public synchronized void release() {
		permits++;
		if (permits > 0) {
			notifyLocal();
		}
	}

	public synchronized void release(int count) {
		assert(count > 0);
		permits += count;
		if (permits > 0) {
			if (count > 1) {
				super.notifyAll();
			} else {
				notifyLocal();
			}
		}
	}
	
	private void notifyLocal() {
		if (multiWait > 0) {
			super.notifyAll();
		} else {
			super.notify();
		}
		
	}
}

