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
package com.wwm.io.packet.layer2;

import java.io.IOException;
import java.util.Collection;

import com.wwm.io.packet.messages.Message;
import com.wwm.io.packet.messages.PacketMessage;

public interface MessageInterface {
	/**
	 * Reads as many messages as are available.
	 * If no messages are available, this method returns null.
	 * Never returns an empty Collection.
	 * This method is fast and does not block on IO if the underlying transport is a network.
	 * @return Collection of PacketMessage, or null if no messages are available at this time.
	 * @throws IOException 
	 */
	public Collection<PacketMessage> read() throws IOException;
	
	/**
	 * Send a message back to the source.  This will either be the requested data, an acknowledgement, or an error.
	 * When implementing streaming of loggable commands (i.e. transaction log, and connection from a master database at a slave),
	 * then this should just ensure that the next command is able to be received.
	 * @param m The message to be sent
	 */
	public void send(Message m) throws IOException;
	public void send(Message m[]) throws IOException;
	public void send(Collection<Message> m) throws IOException;
	
	public void requestClassData(int storeId, String className) throws IOException;
	
	public void close();
}
