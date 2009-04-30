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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.io.packet.CommsStack;
import com.wwm.io.packet.exceptions.NotListeningException;
import com.wwm.io.packet.layer2.SourcedMessage;
import com.wwm.io.packet.layer2.SourcedMessageImpl;
import com.wwm.io.packet.messages.PacketMessage;

public class ConnectionManagerImpl implements ConnectionManager {

	static private final Logger log = LogFactory.getLogger(ConnectionManagerImpl.class); 
	
	protected Selector selector;
	protected Map<SelectionKey, CommsStack> connections = Collections.synchronizedMap(new HashMap<SelectionKey, CommsStack>());

	protected ConnectionManagerImpl() throws IOException {
		selector = SelectorProvider.provider().openSelector();
	}

	public int getNumberOfConnections() {
		return connections.size();
	}
	
	public void close()
	{
		synchronized(connections) {
			for (Map.Entry<SelectionKey, CommsStack> entry : connections.entrySet()) {
				entry.getKey().cancel();
				entry.getValue().getMessageInterface().close();
			}
			connections.clear();
		}
		try {
			selector.close();
		} catch (IOException e) { e.printStackTrace(); } // FIXME: Document this exception
	}
	
	protected Collection<SourcedMessage> processReadyKeys(Set<SelectionKey> readyKeys) {
		Collection<SourcedMessage> messages = new ArrayList<SourcedMessage>();
	    Iterator<SelectionKey> i = readyKeys.iterator();

	    // Walk through the ready keys collection.
	    while (i.hasNext()) {
			SelectionKey sk = i.next();
			i.remove();
			
			// See if its a connection
			CommsStack stack = connections.get(sk);
			if (stack != null) {
				try {
					if (sk.isReadable()) {
						stack.getDriver().eventReadable();
						Collection<PacketMessage> incoming = null;
						incoming = stack.getMessageInterface().read();
						if (incoming != null) {
							for (PacketMessage m : incoming) {
								messages.add(new SourcedMessageImpl(m.getMessage().getStoreId(), stack
										.getMessageInterface(), m.getMessage(), m.getPacket()));
							}
						}
					}
					if (sk.isWritable()) {
						stack.getDriver().eventWritable();
					}
				} catch (IOException e) {
					// Exception thown by underlying read/write operation
					// Probably a disconnection event
					stack.getMessageInterface().close();
					sk.cancel();
					connections.remove(sk);
				}
			} else {
				processKey(sk);
			}
	    }
	    if (messages.size() > 0) {
	    	return messages;
	    }
		return null;
	}
	
	protected void processKey(SelectionKey sk) {
		// Unknown key
		assert (false);
	}

	/**
	 * {@inheritDoc}
	 * Implementation note: Forces implementation of timeout by assuming no time passed if select(timeout) returned
	 * keys.  Testing shows that we sometimes get keys but no messages very quickly.
	 */
	public synchronized Collection<SourcedMessage> waitForMessage(int timeoutMillis) throws NotListeningException {
		// This method is unsynchronized to allow access from multiple threads (clearly it isn't!! - Need to check if I changed it and why!)
		Collection<SourcedMessage> messages;
		
		int selectTimeout = 500;
		if (timeoutMillis == 0){ // if we get zero, simulate old behaviour
			// ensure we enter loop and stay there, and pass zero on to wait indefinitely
			selectTimeout = 0;
			timeoutMillis = 1;
		}
		
		for ( ; timeoutMillis > 0; ) {
			try {
				if (selector.keys().size() == 0){ // no keys means no remaining connections
//			    	System.out.println("no keys");
					throw new NotListeningException();
				}
				// Block for up to selectTimeout waiting for IO. If the timeout expires, this causes
				// us to go round the loop which checks again if there are actually any
				// keys registered. This ensures that if all keys are deregistered while
				// we are blocking, we unblock and throw the right exception rather
				// than blocking forever.
				if (selector.select(selectTimeout) > 0) {
//					System.out.println("keys.size=" + selector.keys().size() );
//					System.out.println("selectedKeys.size=" + selector.selectedKeys().size() );
				    // Someone is ready for I/O, get the ready keys
				    Set<SelectionKey> readyKeys = selector.selectedKeys();
				    messages = processReadyKeys(readyKeys);
				    if (messages != null && messages.size() > 0){
				    	return messages;
				    } else {
//				    	log.info("No messages returned by processReadyKeys()");
				    }
				} else {
					timeoutMillis -= selectTimeout;
//					log.info("timed out");
				}
			} catch (ClosedSelectorException e1) {
				// Another thread did the unlisten() and close the selector while we were sitting on it
				throw new NotListeningException();
			} catch (IOException e) {
				// Error condition, the selector broke
				e.printStackTrace();
				throw new NotListeningException();
			}
		}
		return null;
	}
	
	public void addConnection(CommsStack stack) {
		// Register the new channel with the selector
		SelectionKey sk;
		try {
			sk = stack.getDriver().getSocketChannel().register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			stack.getDriver().setSelectionKey(sk);
		} catch (ClosedChannelException e) {
			return;	// Channel has been closed, don't connect
		}
	
		// Add to connection collection
		connections.put(sk, stack);
	}
}
