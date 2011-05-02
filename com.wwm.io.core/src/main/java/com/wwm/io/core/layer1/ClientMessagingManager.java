package com.wwm.io.core.layer1;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.wwm.db.core.Settings;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.io.core.Authority;
import com.wwm.io.core.ClassDefinitionSource;
import com.wwm.io.core.Message;
import com.wwm.io.core.MessageInterface;
import com.wwm.io.core.SourcedMessage;
import com.wwm.io.core.exceptions.CommandTimedOutException;
import com.wwm.io.core.exceptions.ConnectionLostException;
import com.wwm.io.core.exceptions.NotListeningException;
import com.wwm.io.core.messages.Command;
import com.wwm.io.core.messages.ErrorRsp;
import com.wwm.io.core.messages.Response;

/**
 * Manage messaging between a client and one or more database instances.
 * 
 * @author Neale Upstone
 */
public abstract class ClientMessagingManager extends Thread implements ClientConnectionManager {

	private static final int MILLIS_PER_SEC = 1000;
	private static final int TIMEOUT = 1000;

	protected static class PendingCommand {
			private final Thread thread;
			private Response response;
			
			public PendingCommand(Thread thread) {
				this.thread = thread;
			}
			
			/**
			 * Set the response received against this pending command, and wake the thread that is
			 * waiting on it.
			 */
			public synchronized void setResponseAndWakeRecipient(Response response) {
				this.response = response;
				synchronized (thread) {
					thread.interrupt();
				}
			}
			
			public synchronized Response getResponse() {
				return response;
			}
			
		}

	/** pendingCommands - Need full sync as we want removals be instantaneous */
	private final Map<Integer, PendingCommand> pendingCommands = Collections.synchronizedMap(new HashMap<Integer, PendingCommand>());
	private final boolean neverTimesOut = false;


	public ClientMessagingManager(String name) {
		super(name);
	}

	@Override
	public final void run() {
		super.run();
	
		for (;;) {
			Collection<SourcedMessage> messages = null;			
			try {
				messages = waitForMessages(TIMEOUT);
			} catch (NotListeningException e) {
				// Connection lost!
				// signal all threads to wake up
				synchronized (pendingCommands) {
					for (Map.Entry<Integer, PendingCommand> entry : pendingCommands.entrySet()) {
						entry.getValue().setResponseAndWakeRecipient(null);
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
							pc.setResponseAndWakeRecipient((Response)message);
						}
					}
				}
			}
		}
	}


	protected abstract Collection<SourcedMessage> waitForMessages(int timeout) throws NotListeningException;

	private Response removePendingCommand(int cid) {
		PendingCommand pc = pendingCommands.remove(cid);
		if (pc != null) {
			return pc.getResponse();
		}
		return null;
	}

	public final Response execute(Authority authority, Command command) throws ArchException {
		int cid = command.getCommandId();
		Response response = null;
		
		if (Thread.holdsLock(Thread.currentThread())) {
			throw new RuntimeException("App thread tried to execute a DB command while synchronized on itself, this causes a deadlock");
		}
	
		PendingCommand pc = new PendingCommand(Thread.currentThread());
		assert(!pendingCommands.containsKey(cid));
		pendingCommands.put(cid, pc);		
	
		try {
			getMessageInterface(authority).send(command);
		} catch (IOException e) {
			removePendingCommand(cid);
			close();
			throw new ConnectionLostException(e);
		}
		
		try {
			if (neverTimesOut ) {
				Thread.sleep(Long.MAX_VALUE);
			} else {
				Thread.sleep(Settings.getInstance().getCommandTimeoutSecs() * MILLIS_PER_SEC);
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
					throw new RuntimeException(e1);
				} catch (IllegalAccessException e1) {
					throw new RuntimeException(e1);
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

	public final void requestClassData(Authority authority, int storeId, String className)
			throws IOException {
			
				MessageInterface messageInterface = getMessageInterface(authority);
				if (messageInterface instanceof ClassDefinitionSource) {
					((ClassDefinitionSource) messageInterface).requestClassData(storeId, className);
				}
				else {
					throw new UnsupportedOperationException();
				}
			}

	/**
	 * Get the message interface for this Authority
	 */
	abstract protected MessageInterface getMessageInterface(Authority authority);

	abstract public void close();
}
