package com.wwm.db;

import org.springframework.util.Assert;

import com.wwm.db.internal.server.Database;
import com.wwm.io.core.Authority;

public class EmbeddedClientFactory {

	private static final EmbeddedClientFactory instance = new EmbeddedClientFactory();
	
	private Database database;

	private ReceiverMessageSource databaseMessageSource;
	
	
    /**
     * Create an embedded client connected to a singleton database instance within same VM
     */
    public static Client createEmbeddedClient() {
    	ReceiverMessageSource databaseMessageSource = instance.getClientMessageSource(); 
    	return new DirectClient(Authority.Authoritative, databaseMessageSource.getMessagesForReceiverQueue());
    	
    	
    	
    }

    
    private synchronized Database getDatabase() {
    	if (database != null) {
    		return database;
    	}
    	
    	// no database so create it
    	Assert.isNull(databaseMessageSource, "databaseMessageSource must be null");
    	databaseMessageSource = new ReceiverMessageSource();
    	database = new Database(databaseMessageSource);
    	return database;
    }
    
    // TODO: Create database with source
    /* RESULT should be the message source for the client... need queues each way between MessageInterface execute() and waitForMessages()
     * new SourcedMessageImpl(stack.getMessageInterface(), m.getMessage(), m.getPacket())
     */
    // ******* WOULD be simpler to just be a straight through connection.
    
    private ReceiverMessageSource getClientMessageSource() {
    	// Ensure we have a database available
    	instance.getDatabase();
    	
    	return databaseMessageSource;
	}

}
