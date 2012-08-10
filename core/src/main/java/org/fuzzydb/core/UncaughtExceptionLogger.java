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
package org.fuzzydb.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import org.slf4j.Logger;


/**
 * Default handler to tell Thread what to do with uncaught exceptions in threads.
 * Without installing an uncaught exception handler, the threads would
 * just die silently.
 * To use, place the following code as a static initialiser in main
 * class for application (e.g. server.Database):<p>
 * <code>
 * static { UncaughtExceptionLogger.initialise(); }
 * </code>
 * @author Neale Upstone
 *
 */
public class UncaughtExceptionLogger implements UncaughtExceptionHandler {

	static private Logger log = LogFactory.getLogger(UncaughtExceptionLogger.class);
	
	static private UncaughtExceptionLogger instance = null;
	
	/**
	 * Install a singleton instance of UncaughtExceptionLogger as the 
	 * default uncaught exception handler.
	 */
	static public synchronized void initialise() {
		if (instance == null) {
			instance = new UncaughtExceptionLogger();
			Thread.setDefaultUncaughtExceptionHandler( instance );
		}
	}
	
	/**
	 * Log exceptions
	 */
	public void uncaughtException(Thread t, Throwable e) {
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		e.printStackTrace( new PrintStream( s ) );
		log.error( "Fatal Exception in Thread: " + t.getName(), e );
		log.error( "Details - Exception = : " + s.toString() );
		
	}

}
