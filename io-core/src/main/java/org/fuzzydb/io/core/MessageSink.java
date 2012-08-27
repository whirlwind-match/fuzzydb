package org.fuzzydb.io.core;

import java.io.IOException;

public interface MessageSink {

	/**
	 * Send a message back to the source.  This will either be the requested data, an acknowledgement, or an error.
	 * When implementing streaming of loggable commands (i.e. transaction log, and connection from a master database at a slave),
	 * then this should just ensure that the next command is able to be received.
	 * @param m The message to be sent
	 */
	void send(Message m) throws IOException;

	void close();
}