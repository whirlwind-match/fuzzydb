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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.wwm.db.internal.server.WorkerThread;
import com.wwm.util.FastSemaphore;

/**
 * A synchronization class. This controls access to a resource. It allows multiple threads to access for read, but only
 * one thread to access for write. All other threads are locked out while write access is granted.
 * 
 * When a block occurs, a call is made to ThreadManager to notify it that a blocking operation is beginning. This allows
 * TM to release another worker thread to execute an unrelated command.
 */
public class ExclusiveWrite {
	private static final int MAX_PERMITS = Integer.MAX_VALUE;

	private final FastSemaphore lock = new FastSemaphore(MAX_PERMITS);

	private final Set<Thread> readLocks = Collections.synchronizedSet(new HashSet<Thread>());

	private volatile Thread writeLock = null;

	public ExclusiveWrite() {
	}

	public void acquireRead() {
		assert (!readLocks.contains(Thread.currentThread()));
		if (!lock.tryAcquire()) {
			WorkerThread.beginIO();
			lock.acquireUninterruptibly();
			WorkerThread.endIO();
		}
		readLocks.add(Thread.currentThread());
	}

	public synchronized void releaseRead() {
		assert (readLocks.contains(Thread.currentThread()));
		lock.release();
		readLocks.remove(Thread.currentThread());
	}

	public void acquireWrite() {
		assert (!readLocks.contains(Thread.currentThread()));
		if (!lock.tryAcquire(MAX_PERMITS)) {
			WorkerThread.beginIO();
			lock.acquireUninterruptibly(MAX_PERMITS);
			WorkerThread.endIO();
		}
		synchronized (this) {
			assert (writeLock == null);
			writeLock = Thread.currentThread();
		}
	}

	public boolean tryAcquireWrite() {
		if (!readLocks.isEmpty()) {
			return false;
		}
		if (writeLock != null) { // volatile
			return false;
		}

		if (!lock.tryAcquire(MAX_PERMITS)) {
			return false;
		}
		synchronized (this) {
			assert (writeLock == null);
			writeLock = Thread.currentThread();
		}
		return true;
	}

	public synchronized void releaseWrite() {
		assert (writeLock == Thread.currentThread());
		writeLock = null;
		lock.release(MAX_PERMITS);
	}

	public synchronized void releaseWriteAcquireRead() {
		assert (writeLock == Thread.currentThread());
		lock.release(MAX_PERMITS - 1);
		writeLock = null;
		readLocks.add(Thread.currentThread());
	}

	public boolean hasReadLock() {
		return readLocks.contains(Thread.currentThread());
	}

	public boolean hasWriteLock() {
		return writeLock == Thread.currentThread();
	}

	public boolean hasEitherLock() {
		return hasReadLock() || hasWriteLock();
	}

}
