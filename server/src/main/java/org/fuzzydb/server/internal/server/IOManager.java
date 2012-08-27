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
package org.fuzzydb.server.internal.server;

/**
 * Manages access to I/O resources that may block
 */
public interface IOManager {
	
	/**
	 * Called by a WorkerThread to indicate is it about to perform a blocking IO operation.
	 * This call must be paired with a call to endIO(). 
	 */
	public void beginIO();
	
	public void endIO();
}
