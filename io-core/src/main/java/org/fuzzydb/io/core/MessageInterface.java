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
package org.fuzzydb.io.core;

import java.io.IOException;
import java.util.Collection;

import org.fuzzydb.io.core.messages.PacketMessage;


public interface MessageInterface extends MessageSink {
	/**
	 * Reads as many messages as are available.
	 * If no messages are available, this method returns null.
	 * Never returns an empty Collection.
	 * This method is fast and does not block on IO if the underlying transport is a network.
	 * @return Collection of PacketMessage, or null if no messages are available at this time.
	 * @throws IOException 
	 */
	Collection<PacketMessage> read() throws IOException;
	
}
