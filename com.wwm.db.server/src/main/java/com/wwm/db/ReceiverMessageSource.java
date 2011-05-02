package com.wwm.db;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.wwm.io.core.MessageSource;
import com.wwm.io.core.SourcedMessage;
import com.wwm.io.core.exceptions.NotListeningException;

/**
 * TODO: Rename to QueuingMessageSource
 * 
 * Allows messages to be queued for a receiver to poll using the MessageSource interface.  
 * When sent, they are placed in a queue as SourcedMessages, which are made available for polling.
 * 
 * @author Neale Upstone
 */
public class ReceiverMessageSource implements MessageSource {

	private static final int QUEUE_CAPACITY = 10;
	
	private final BlockingQueue<SourcedMessage> messagesForReceiver = new ArrayBlockingQueue<SourcedMessage>(QUEUE_CAPACITY);
	
	public void start() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * NOTE: This implementation only returns one message at a time.  It could easily return more. 
	 */
	public Collection<SourcedMessage> waitForMessage(int timeoutMillis)
			throws NotListeningException {
		try {
			SourcedMessage result = messagesForReceiver.poll(timeoutMillis, TimeUnit.MILLISECONDS);
			return (result == null) ? null : Collections.singletonList(result);
		} catch (InterruptedException e) {
			throw new NotListeningException(e);
		}
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	
	public void put(SourcedMessage message) throws InterruptedException {
		messagesForReceiver.put(message);
	}
	
	
	/**
	 * Get the symmetric dataSource
	 * @return
	 */
	public MessageSource getClientMessageSource() {
		// TODO Auto-generated method stub
		return null;
	}

}
