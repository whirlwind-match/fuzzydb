package com.wwm.db;

import java.io.IOException;
import java.util.Collection;

import org.fuzzydb.client.Client;
import org.fuzzydb.client.internal.AbstractClient;
import org.fuzzydb.io.core.Authority;
import org.fuzzydb.io.core.Message;
import org.fuzzydb.io.core.MessageSink;
import org.fuzzydb.io.core.SourcedMessage;
import org.fuzzydb.io.core.exceptions.NotListeningException;
import org.fuzzydb.io.core.layer1.ClientMessagingManager;
import org.fuzzydb.io.core.layer2.SourcedMessageImpl;



/**
 * A DirectClient is is able to communicate with a database via a Queue of SourcedMessages.
 * 
 * @author Neale
 */
public class DirectClient extends AbstractClient implements Client {

		
	private final ReceiverMessageSource databaseRequestMessageSource;

	private final MessageSink repliesMessageSink = new MessageSink() {
		
		public void send(Message m) throws IOException {
			try {
				replies.put(new SourcedMessageImpl(null, m, null));
			} catch (InterruptedException e) {
				throw new IOException("Interrupted while waiting to send()", e);
			}
			
		}
		
		public void close() {
		}
	};
	
	private final ReceiverMessageSource replies = new ReceiverMessageSource();
	
	/**
	 * 
	 * @param databaseRequestMessageSource - Queue used by the database as it's message source. The Client will
	 * place SourcedMessages on this queue.
	 */
	public DirectClient(Authority authority, ReceiverMessageSource databaseRequestMessageSource) {
		super(authority);
		this.databaseRequestMessageSource = databaseRequestMessageSource;
	}


	public void connect() {
		ClientMessagingManager connection = new ClientMessagingManager("DirectClient"){

			/**
			 * Wait for messages from the server - these messages will be created by MessageSink.send() on the Sink used in SourcedMessage
			 */
			@Override
			protected Collection<SourcedMessage> waitForMessages(int timeout)
					throws NotListeningException {
				return replies.waitForMessage(timeout);
			}

			@Override
			protected MessageSink getMessageInterface(Authority authority) {
				// something that provides send(Message) for messages to server
				return new MessageSink(){

					public void send(Message m) throws IOException {
						SourcedMessageImpl sm = new SourcedMessageImpl(repliesMessageSink, m, null);
						
						try {
							databaseRequestMessageSource.put(sm);
						} catch (InterruptedException e) {
							throw new IOException("Interrupted while waiting to send()", e);
						}
						
					}

					public void close() {
						// TODO: should we interrupt here... better would be to standardise
						// use of executors instead of too many of our own threads
					}
				};
			}

			@Override
			public void close() {
				
			}
			
		};
		setConnection(connection);
		connection.setDaemon(true);
		connection.start();

	}

}
