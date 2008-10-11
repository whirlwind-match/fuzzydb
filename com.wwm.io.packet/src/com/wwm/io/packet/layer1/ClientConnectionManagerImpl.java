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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.wwm.db.core.Settings;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.io.packet.ClassLoaderInterface;
import com.wwm.io.packet.CommsStack;
import com.wwm.io.packet.TCPStack;
import com.wwm.io.packet.exceptions.CommandTimedOutException;
import com.wwm.io.packet.exceptions.ConnectionLostException;
import com.wwm.io.packet.exceptions.NotListeningException;
import com.wwm.io.packet.layer2.SourcedMessage;
import com.wwm.io.packet.messages.Command;
import com.wwm.io.packet.messages.ErrorRsp;
import com.wwm.io.packet.messages.Message;
import com.wwm.io.packet.messages.Response;

public class ClientConnectionManagerImpl extends Thread implements ClientConnectionManager {

	@Override
	public void run() {
		super.run();

		for (;;) {
			Collection<SourcedMessage> messages = null;			
			try {
				messages = cm.waitForMessage(1000);
			} catch (NotListeningException e) {
				// Connection lost!
				// signal all threads to wake up
				synchronized (pendingCommands) {
					for (Map.Entry<Integer, PendingCommand> entry : pendingCommands.entrySet()) {
						entry.getValue().setResponse(null);
					}
				}
				return;
			}
			if (messages != null) {
				for (SourcedMessage sm : messages) {
					Message message = sm.getMessage();
					synchronized (pendingCommands) {
						PendingCommand pc = pendingCommands.get(message.getCommandId());
						if (pc != null) {
							pc.setResponse((Response)message);
						}
					}
				}
			}
		}
	}

	private static class PendingCommand {
		private final Thread thread;
		private Response response;
		
		public PendingCommand(Thread thread) {
			this.thread = thread;
		}
		
		public synchronized void setResponse(Response response) {
			this.response = response;
			synchronized (thread) {
				thread.interrupt();
			}
		}
		
		public synchronized Response getResponse() {
			return response;
		}
		
	}
	
	private final ConnectionManagerImpl cm;
	private final CommsStack authStack;
	private final CommsStack nonAuthStack;
	private Map<Integer, PendingCommand> pendingCommands = Collections.synchronizedMap(new HashMap<Integer, PendingCommand>());
	private boolean neverTimesOut = false;
	
	public ClientConnectionManagerImpl(InetSocketAddress address, ClassLoaderInterface cli) throws IOException {
		super("ClientConnectionManager");
		
		SocketChannel sc = SocketChannel.open(address);
		
		// Build the stack
		// This is TCPStack but may support other stack flavours in future
		nonAuthStack = authStack = new TCPStack(sc, cli);

		// Create a connection manager to pump message out and in
		cm = new ConnectionManagerImpl();
		cm.addConnection(authStack);
		super.setDaemon(true);
		super.start();
	}
	
	private Response removePendingCommand(int cid) {
		PendingCommand pc = pendingCommands.remove(cid);
		if (pc != null) {
			return pc.getResponse();
		}
		return null;
	}
	
	public Response execute(Authority authority, Command command) throws ArchException {
		int cid = command.getCommandId();
		Response response = null;
		
		if (Thread.holdsLock(Thread.currentThread())) {
			throw new Error("App thread tried to execute a DB command while synchronized on itself, this causes a deadlock");
		}

		PendingCommand pc = new PendingCommand(Thread.currentThread());
		assert(!pendingCommands.containsKey(cid));
		pendingCommands.put(cid, pc);
		
		CommsStack stack = null;
		switch (authority) {
		case Authoritative:
			stack = authStack;
			break;
		case NonAuthoritative:
			stack = nonAuthStack;
			break;
		}
		
		try {
			stack.getMessageInterface().send(command);
		} catch (IOException e) {
			removePendingCommand(cid);
			close();
			throw new ConnectionLostException(e);
		}
		
		try {
			if (neverTimesOut ) {
				Thread.sleep(Long.MAX_VALUE);
			} else {
				Thread.sleep(Settings.getInstance().getCommandTimeoutSecs() * 1000);
			}
			// Timeout
			removePendingCommand(cid);
			//trace.timeout(cid);
			Thread.interrupted();	// Consume the interrupted flag, just in case the command arrived just after we timed out
			throw new CommandTimedOutException(command);
		} catch (InterruptedException e) {
			Thread.interrupted();
			response = removePendingCommand(cid);
			if (response instanceof ErrorRsp) {
				ErrorRsp er = (ErrorRsp) response;
				ArchException serverException = er.getError();

				// Generate local exception of same class as the server exception.
				ArchException localException;
				try {
					localException = serverException.getClass().newInstance();
				} catch (InstantiationException e1) {
					throw new Error(e1);
				} catch (IllegalAccessException e1) {
					throw new Error(e1);
				}
				localException.initCause(serverException);
				localException.setStackTrace(getStackTrace()); // set stack trace to here (rather than one set in newInstance()
				throw localException;
			}
			if (response==null) {
				throw new ConnectionLostException();	// a null response indicates a lost connection
			} else {
				return response;
			}
			//trace.trace(response);
		}
	}

	public void close() {
		nonAuthStack.getMessageInterface().close();
		authStack.getMessageInterface().close();
		cm.close();
	}

	public void requestClassData(Authority authority, int storeId, String className) throws IOException {
		CommsStack stack = null;
		switch (authority) {
		case Authoritative:
			stack = authStack;
			break;
		case NonAuthoritative:
			stack = nonAuthStack;
			break;
		}
		stack.requestClassData(storeId, className);
	}

}
