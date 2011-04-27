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
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.wwm.io.packet.ClassLoaderInterface;
import com.wwm.io.packet.CommsStack;
import com.wwm.io.packet.TCPStack;

/**
 * A MessageSource capable of listening on multiple InetSockets
 */
public abstract class ServerImpl extends ConnectionManagerImpl implements Server {
		
	//private Selector selector;	// Always null when there are no listers active.
	private Map<SelectionKey, ServerSocketChannel> listners = Collections.synchronizedMap(new HashMap<SelectionKey, ServerSocketChannel>());
	//private Map<SelectionKey, CommsStack> connections = Collections.synchronizedMap(new HashMap<SelectionKey, CommsStack>());
	private ClassLoaderInterface cli;
	private boolean closing = false;
	
	public ServerImpl(ClassLoaderInterface cli) throws IOException
	{
		super();
		this.cli = cli;
	}

	public void listen(String hostname, int port) throws IOException {
		InetSocketAddress isa = new InetSocketAddress(hostname, port);
		listen(isa);
	}
	
	public void listen(InetSocketAddress address) throws IOException {
		// Create a new server socket and set to non blocking mode
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);

		ssc.socket().bind(address);
		
		// Register accepts on the server socket with the selector. This
		// step tells the selector that the socket wants to be put on the
		// ready list when accept operations occur, so allowing multiplexed
		// non-blocking I/O to take place.
		SelectionKey acceptKey;
		acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
		listners.put(acceptKey, ssc);
	}
	
	public synchronized void listen(int port) throws IOException {
		// Bind the server socket to the local host and port
		// InetAddress lh = InetAddress.getLocalHost();	// DANGER: Returns address of primary adaptor contrary to what the docs say!
		InetSocketAddress isa = new InetSocketAddress(port);	// Uses Wildcard address
		listen(isa);
	}

	@Override
	public void close() {

		if (closing) return;

		closing  = true;

		synchronized(listners) {
			for (Map.Entry<SelectionKey, ServerSocketChannel> entry : listners.entrySet()) {
				entry.getKey().cancel();
					try {
						entry.getValue().close();
					} catch (IOException e) {
						// Failing to close the listen socket, silently ignore
						e.printStackTrace();
					}
			}
			listners.clear();
		}

		// Close all connections
		super.close();
	}

	@Override
	protected void processKey(SelectionKey sk) {
		ServerSocketChannel ssc = listners.get(sk);
		if (ssc != null) {
			accept(ssc);
		} else {
			assert (false); // we couldn't find the key, we must
							// have leaked one
		}
	}
/*
	public Collection<SourcedMessage> waitForMessage() throws NotListeningException {
		// This method is unsynchronized to allow access from multiple threads.
		int keysAdded = 0;
		Collection<SourcedMessage> messages = new ArrayList<SourcedMessage>();
		
		for (;;) {
			Selector safeSelector = selector;	// Assign this before the test as other threads can set it to null at any time
			if (safeSelector == null) throw new NotListeningException();
			try {
				if ((keysAdded = safeSelector.select()) == 0) {
					// This should only happen if the selector is woken up (selector.wakeup()) or the thread is interrupted.
					// Instead of returning null we throw this exception for API consistency.
					// It is expected that this may happen during unlisten().
					// This method never returns null.
					throw new NotListeningException();
				}
			} catch (ClosedSelectorException e1) {
				// Another thread did the unlisten() and close the selector while we were sitting on it
				throw new NotListeningException();
			} catch (IOException e) {
				// Error condition, the selector broke
				e.printStackTrace();
				throw new NotListeningException();
			}
			
		    // Someone is ready for I/O, get the ready keys
		    Set<SelectionKey> readyKeys = safeSelector.selectedKeys();
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
							Collection<Message> incoming = null;
							incoming = stack.getMessageInterface().read();
							if (incoming != null) {
								for (Message m : incoming) {
									messages.add(new SourcedMessageImpl(m
											.getStoreId(), stack
											.getMessageInterface(), m));
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
					// It must be an accept - new connection
					ServerSocketChannel ssc = listners.get(sk);
					if (ssc != null) {
						accept(ssc);
					} else {
						assert (false); // we couldn't find the key, we must
										// have leaked one
					}
				}
		    }
		    if (messages.size() > 0) {
		    	return messages;
		    }
		}
	}
*/
	private synchronized void accept(ServerSocketChannel ssc) {
		SocketChannel sc;
		try {
			sc = ssc.accept();
		} catch (IOException e) {
			return; // Disconnection?
		}
		if (sc == null) {
			// Nothing to accept, maybe another thread got it?
			return;
		}
		
		CommsStack stack;
		
		try {
			// Build the stack
			// This is TCPStack but may support other stack flavours in future
			stack = new TCPStack(sc, cli);
		} catch (IOException e) {
			return;	// Socket broken somehow?
		}

		addConnection(stack);
	}

}
