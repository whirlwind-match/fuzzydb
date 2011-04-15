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
package com.wwm.db.internal.server;

import java.nio.channels.CancelledKeyException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wwm.db.core.LogFactory;

/**
 * WorkerThreadManager is responsible for ensuring that a limited (usually = cpu count)
 * number of WorkerThreads can be operating concurrently, and that if one gets blocked 
 * waiting for I/O, further threads can be released to keep the CPU busy.
 * This ensures that the correct number of threads is running at any one time.
 * 
 * TODO: This is convoluted and difficult to interact with. e.g. MaintThread wants to 
 * be able to act when there are no transactions in progress.  It is required to implement
 * it's own locking and scheduling.
 * Perhaps a better approach would be to allow jobs to be submitted as different classes:
 * - ExclusiveJob - requires all the permits
 * - ScalableJob - gets specified number of threads (CPU count), only releasing new threads 
 * when the server blocks on I/O.
 * A scheduled job, such as like server maintenance would also require some re-scheduling.
 * ... not an easy web to weave ...
 * 
 * Perhaps our implementation would benefit from using Latches or AtomicInteger. See
 * Effective Java chapter on Concurrency.
 */
public abstract class WorkerThreadManager implements IOManager {
	
	static private final Logger log = LogFactory.getLogger(WorkerThreadManager.class);

	private final static int maxRunnableThreads = 1; // min 1  Note: Have briefly tried 2
	private final static int maxIOThreads = 0;	// min 0
	private final static int maxTotalThreads = maxIOThreads + maxRunnableThreads;
	
	private volatile boolean stopping = false;
	
	private Set<WorkerThread> threads = new HashSet<WorkerThread>();
	private boolean started = false;
	
	private final Semaphore gate = new Semaphore(1); // Was FastSemaphore (not sure why)
	
	private int releasable = maxRunnableThreads-1;
	private int waiting = 1;
	
	public synchronized void endWait() {
		assert(waiting == 1);
		waiting--;
		tryRelease();
	}
	
	private synchronized void tryRelease() {
		if (waiting == 0 && releasable > 0) {
			releasable--;
			gate.release();
			waiting++;
		}
	}
	
	/**
	 * For debug.  Ensure that each beginIO is ended with an endIO by the same thread.
	 */
	private ThreadLocal<Boolean> threadIsDoingIO = new ThreadLocal<Boolean>();
	
	public void beginIO() {
		assert(threadIsDoingIO.get() == null);
		threadIsDoingIO.set(Boolean.TRUE);
		synchronized (this) {
			releasable++;
		}
		tryRelease();
	}

	public void endIO() {
		assert threadIsDoingIO.get() == Boolean.TRUE;
		threadIsDoingIO.set(null);

		synchronized (this) {
			releasable--;
		}
		
	}
	
	public synchronized void start() {
		assert(!started);
		for (int i = 0; i < maxTotalThreads; i++) {
			WorkerThread thread = new WorkerThread(this);
			threads.add(thread);
			thread.start();
		}
		started = true;
	}
	
	public void threadExecute() {
		do {
			boolean block = false;
			synchronized (this) {
				if (waiting == 0) {
					//gate.release();
					waiting++;
				} else {
					assert(waiting == 1);
					block = true;
				}
			}
			if (block) {
				try {
					gate.acquire();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				synchronized (this) {
					releasable++;
				}

			}
			try {
				if (!stopping) {
					runThreadLock.acquireUninterruptibly(); // we require a permit to proceed
					runWorker();
				}
			} catch (CancelledKeyException e) {
				return; // thread exits
			} catch (Throwable t) {
				log.log(Level.SEVERE, "** Caught Unexpected Exception **", t);
			} finally {
				if (!stopping) {
					runThreadLock.release();
				}
			}
		} while (!stopping);
	}
	
	
	public void shutdown() {
		stopping = true;
		gate.release(maxTotalThreads + 10); // release all waiting + some for good measure. 
		
		boolean alive = false;
		do {
			alive = false;
			for (WorkerThread thread : threads) {
				alive |= thread.isAlive();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { e.printStackTrace(); } // FIXME: Document this exception
		} while (alive);

		// FIXME: want to do this, but MaintThread will call stuff in here still... 
		// gate = null; // we've just trashed the semaphore and no longer need it, so get rid of it.
		log.info( getClass().getSimpleName() + " has now exited");

	}
	
	static private int MAX_PERMITS = Integer.MAX_VALUE;
	/**
	 * Lock to permit threads to run, or not.
	 */
	private final Semaphore runThreadLock = new Semaphore(MAX_PERMITS, true); // want fairness

	/**
	 * Wait until we can get all permits such that we're the only thread that's runnable.
	 */
	public void acquireExclusivity() {
		runThreadLock.acquireUninterruptibly(MAX_PERMITS);
	}

	/**
	 * Release all permits thus allowing other threads to run
	 */
	public void releaseExclusivity() {
		runThreadLock.release(MAX_PERMITS);
		
	}
	
	public abstract void runWorker();
}
