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

import java.util.concurrent.Semaphore;
import org.slf4j.Logger;

import com.wwm.db.core.LogFactory;

/**
 * Initialiser extends WorkerThread so that database operations can get at current transaction etc.
 */
public class Initialiser extends WorkerThread {

	static private Logger log = LogFactory.getLogger(Initialiser.class);

	private final Database database;
	private final Repository repository;
	private final Semaphore finished = new Semaphore(0);

	/**
	 * 
	 * @param injector we need one of these to be able to inject services into materialised objects
	 */
	public Initialiser(Repository repository, Database database, WorkerThreadManager manager) {
		super("Initialiser", manager);
		this.database = database;
		this.repository = repository;
	}
	
	public void initialise() {
		super.start();
		finished.acquireUninterruptibly(); // we wait here until released() by run().
	}
	
	
	@Override
	public void run() {
		log.info("Initialising Transient Data... (no transaction writes can occur here)");
		try {
			// Init repos with transient data
			repository.initTransientData();
			log.info("Initialise completed.");
		} catch (Throwable e){
			log.error( "Unexpected Exception", e );
		} finally {
			finished.release();
		}
	}
}
