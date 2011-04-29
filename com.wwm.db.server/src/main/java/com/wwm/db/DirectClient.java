package com.wwm.db;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Queue;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.internal.AbstractClient;
import com.wwm.io.core.Authority;
import com.wwm.io.core.MessageInterface;
import com.wwm.io.core.SourcedMessage;
import com.wwm.io.core.exceptions.NotListeningException;
import com.wwm.io.core.layer1.ClientConnectionManager;
import com.wwm.io.core.layer1.ClientMessagingManager;


/**
 * A DirectClient is able to provide a MessageSource with which to start a database
 * @author Neale
 *
 */
public class DirectClient extends AbstractClient implements Client {

	private final ReceiverMessageSource serverMessageInterface;

	public DirectClient(Authority authority, Queue<SourcedMessage> queue) {
		super(authority);
		this.serverMessageInterface = createMessageInterfaceFor(queue); 
	}

	private ReceiverMessageSource createMessageInterfaceFor(
			Queue<SourcedMessage> queue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connect() throws ArchException {
		ClientConnectionManager x = new ClientMessagingManager("DirectClient"){

			@Override
			protected Collection<SourcedMessage> waitForMessages(int timeout)
					throws NotListeningException {
				// TODO Auto-generated method stub
				
				
				
				return null;
			}

			@Override
			protected MessageInterface getMessageInterface(Authority authority) {
				return null;
			}

			@Override
			public void close() {
				// TODO Auto-generated method stub
				
			}
			
		};
		context.setConnection(x);
		// TODO Auto-generated method stub

	}

	@Override
	public void connect(String server) throws ArchException {
		// TODO Auto-generated method stub
	}

	@Override
	public void connect(InetSocketAddress addr) throws ArchException {
		// TODO Auto-generated method stub
	}
}
