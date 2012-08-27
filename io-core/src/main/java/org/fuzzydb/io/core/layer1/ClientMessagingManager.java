package org.fuzzydb.io.core.layer1;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.fuzzydb.core.LogFactory;
import org.fuzzydb.core.Settings;
import org.fuzzydb.core.exceptions.ArchException;
import org.fuzzydb.io.core.Authority;
import org.fuzzydb.io.core.ClassDefinitionSource;
import org.fuzzydb.io.core.Message;
import org.fuzzydb.io.core.MessageSink;
import org.fuzzydb.io.core.SourcedMessage;
import org.fuzzydb.io.core.exceptions.CommandTimedOutException;
import org.fuzzydb.io.core.exceptions.ConnectionLostException;
import org.fuzzydb.io.core.exceptions.NotListeningException;
import org.fuzzydb.io.core.messages.Command;
import org.fuzzydb.io.core.messages.ErrorRsp;
import org.fuzzydb.io.core.messages.Response;
import org.slf4j.Logger;


/**
 * Manage messaging between a client and one or more database instances.
 * 
 * @author Neale Upstone
 */
public abstract class ClientMessagingManager extends Thread implements ClientConnectionManager {

	
	protected final Logger log = LogFactory.getLogger(this.getClass());
	
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
			} catch (RuntimeException e) {
				e.printStackTrace();
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

	public final Response execute(Authority authority, Command command) {
		log.trace("Executing command {}...", command);
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
			log.trace("Sent command {}...", command);
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
			log.trace("Interrupted after sending {}.  Trying to receive...", command);
			response = removePendingCommand(cid);
			if (response instanceof ErrorRsp) {
				ErrorRsp er = (ErrorRsp) response;
				ArchException serverException = er.getError();
				log.debug("Sent command resulted in error: {}", er);
	
				// Generate local exception of same class as the server exception.
				ArchException localException;
				try {
					Constructor<? extends ArchException> constructor = serverException.getClass().getConstructor(String.class);
					localException = constructor.newInstance(serverException.getMessage());
					localException.initCause(serverException);
					throw localException;
				} catch (InstantiationException e1) {
					throw new RuntimeException(e1);
				} catch (IllegalAccessException e1) {
					throw new RuntimeException(e1);
				} catch (SecurityException e1) {
					throw new RuntimeException(e1);
				} catch (NoSuchMethodException e1) {
					throw new RuntimeException(e1);
				} catch (IllegalArgumentException e1) {
					throw new RuntimeException(e1);
				} catch (InvocationTargetException e1) {
					throw new RuntimeException(e1);
				}
			}
			if (response==null) {
				throw new ConnectionLostException();	// a null response indicates a lost connection
			} else {
				log.trace("Received response {}", response);
				return response;
			}
			//trace.trace(response);
		}
	}

	public final void requestClassData(Authority authority, int storeId, String className)
			throws IOException {
			
				MessageSink messageInterface = getMessageInterface(authority);
				if (messageInterface instanceof ClassDefinitionSource) {
					((ClassDefinitionSource) messageInterface).requestClassData(storeId, className);
				}
				else {
					throw new UnsupportedOperationException();
				}
			}

	/**
	 * Find where to send messages for this Authority
	 */
	abstract protected MessageSink getMessageInterface(Authority authority);

	abstract public void close();
}
