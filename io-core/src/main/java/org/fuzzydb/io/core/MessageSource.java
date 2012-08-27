package org.fuzzydb.io.core;

import java.util.Collection;

import org.fuzzydb.io.core.exceptions.NotListeningException;


public interface MessageSource {

	/**
	 * Start receiving messages. 
	 */
	void start();
	
	/**
	 * Blocks until messages arrives, then returns them.
	 * Throws an exception if the MessageSource is not listening, or stops listening while another thread is blocking.
	 * @param timeoutMillis How long to wait
	 * @return A collection of received messages with information on where they came from.  Returns null if times out.
	 * @throws NotListeningException The server is not listening, or stopped listening while this thread was blocking
	 */
	Collection<SourcedMessage> waitForMessage(int timeoutMillis) throws NotListeningException;

	void close();

}