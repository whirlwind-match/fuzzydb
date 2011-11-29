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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Collection;

import com.wwm.io.core.Authority;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.MessageInterface;
import com.wwm.io.core.MessageSink;
import com.wwm.io.core.SourcedMessage;
import com.wwm.io.core.exceptions.NotListeningException;
import com.wwm.io.core.layer1.ClientMessagingManager;
import com.wwm.io.packet.TCPStack;

/**
 * ClientMessagingManager that uses sockets for it's connection with a single server.
 * 
 * TODO: add support for connecting to multiple servers (auth, non-auth)
 * 
 * @author Adrian Clarkson
 * @author Neale Upstone
 */
public class ClientConnectionManagerImpl extends ClientMessagingManager {

	private final MessageInterface authMessageInterface;
	private final MessageInterface nonAuthMessageInterface;
	private final ConnectionManagerImpl cm;
	
	public ClientConnectionManagerImpl(InetSocketAddress address, ClassLoaderInterface cli) throws IOException {
		super("ClientConnectionManager");
		
		SocketChannel sc = SocketChannel.open(address);
		
		// Build the stack
		// This is TCPStack but may support other stack flavours in future
		TCPStack stack = new TCPStack(sc, cli);
		nonAuthMessageInterface = authMessageInterface = stack.getMessageInterface();

		// Create a connection manager to pump message out and in
		cm = new ConnectionManagerImpl(){
			public void start() { 
				// effectively connect() in this scenario.  
			}
		};
		cm.addConnection(stack);
		super.setDaemon(true);
		super.start();
	}
	
	@Override
	protected Collection<SourcedMessage> waitForMessages(int timeout)
			throws NotListeningException {
		return cm.waitForMessage(timeout);
	}
	
	/**
	 * Get the message interface for this Authority
	 */
	@Override
	protected MessageSink getMessageInterface(Authority authority) {
		MessageSink stack = null;
		switch (authority) {
		case Authoritative:
			stack = authMessageInterface;
			break;
		case NonAuthoritative:
			stack = nonAuthMessageInterface;
			break;
		}
		return stack;
	}

	@Override
	public void close() {
		nonAuthMessageInterface.close();
		authMessageInterface.close();
		cm.close();
	}
}
