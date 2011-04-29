package com.wwm.db;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import com.wwm.io.core.MessageSource;
import com.wwm.io.core.SourcedMessage;
import com.wwm.io.core.exceptions.NotListeningException;

/**
 * A MessageSource that can be used by a receiver to receive messages from multiple clients
 * 
 * @author Neale
 *
 */
public class ReceiverMessageSource implements MessageSource {

	
	private final BlockingQueue<SourcedMessage> messagesForReceiver = new SynchronousQueue<SourcedMessage>();
	
	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * NOTE: This implementation only returns one message at a time.  It could easily return more. 
	 */
	@Override
	public Collection<SourcedMessage> waitForMessage(int timeoutMillis)
			throws NotListeningException {
		try {
			SourcedMessage result = messagesForReceiver.poll(timeoutMillis, TimeUnit.MILLISECONDS);
			return (result == null) ? null : Collections.singletonList(result);
		} catch (InterruptedException e) {
			throw new NotListeningException(e);
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	
	public void send(SourcedMessage message) throws InterruptedException{
		messagesForReceiver.put(message);
	}
	
	public BlockingQueue<SourcedMessage> getMessagesForReceiverQueue() {
		return messagesForReceiver;
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
