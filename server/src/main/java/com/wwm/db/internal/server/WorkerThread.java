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

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;

import com.wwm.db.core.LogFactory;

/**
 * A WorkerThread is the slave to WorkerThreadManager, which creates and destroys threads according
 * to how many are blocked on I/O. 
 */
public class WorkerThread extends Thread {
	
	static private Logger log = LogFactory.getLogger(WorkerThread.class);
	
	static private AtomicInteger threadNumber = new AtomicInteger(0);

	private final WorkerThreadManager manager;
	
	public WorkerThread(WorkerThreadManager manager) {
		super("WorkerThread-" + threadNumber.incrementAndGet() );
		this.manager = manager;
		this.setDaemon(true);
	}
	
	public WorkerThread(String name, WorkerThreadManager manager) {
		super(name);
		this.manager = manager;
		this.setDaemon(true);
	}

	@Override
	public void run() {
		// removed by Neale: super.run() is for when you supply (Runnable target), but we do our own.		
		//		super.run();
		manager.threadExecute(); // We have multiple threads kicked off, and the actual work is grabbed in WorkerThreadManager
		log.info( getName() + " now exited");
	}
	
	/**
	 * Get current thread as a WorkerThread.
	 */
	public static WorkerThread currentThread() {
		return (WorkerThread)Thread.currentThread();
	}
	
	/**
	 * Wait until we can lock out all other threads, such that we will then
	 * be the only thread running.
	 */
	protected void acquireExclusivity() {
		manager.acquireExclusivity();
	}
	
	protected void releaseExclusivity() {
		manager.releaseExclusivity();
	}

	public static void beginIO() {
		// Allow non-worker thread to call this, so we can do tests without being in a worker thread
		Thread thread = Thread.currentThread();
		if (thread instanceof WorkerThread) {
			((WorkerThread) thread).manager.beginIO();
		}
	}

	public static void endIO() {
		// Allow non-worker thread to call this, so we can do tests without being in a worker thread
		Thread thread = Thread.currentThread();
		if (thread instanceof WorkerThread) {
			((WorkerThread) thread).manager.endIO();
		}
	} 
}
