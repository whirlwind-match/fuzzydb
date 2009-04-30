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
package com.wwm.io.packet.layer1;

import java.util.Collection;

import com.wwm.io.packet.CommsStack;
import com.wwm.io.packet.exceptions.NotListeningException;
import com.wwm.io.packet.layer2.SourcedMessage;

public interface ConnectionManager {
	/**Blocks until messages arrives, then returns them.
	 * Throws an exception if the Server is not listening, or unlisten is called while another thread is blocking.
	 * @param timeoutMillis How long to wait
	 * @return A collection of received messages with information on where they came from.  Returns null if times out.
	 * @throws NotListeningException The server is not listening, or was unlistened while this thread was blocking
	 */
	public Collection<SourcedMessage> waitForMessage(int timeoutMillis) throws NotListeningException;
	//public void closeAllConnections();
	public void addConnection(CommsStack stack);
	public void close();
	
	public int getNumberOfConnections();
}
