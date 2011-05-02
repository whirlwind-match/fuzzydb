package com.wwm.db;

import java.io.IOException;

import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.internal.server.Database;
import com.wwm.io.core.Authority;

public class EmbeddedClientFactory {

	private static EmbeddedClientFactory instance;
	
	private final Database database;

	private final ReceiverMessageSource databaseMessageSource;

	
	
	public static synchronized EmbeddedClientFactory getInstance() {
		if (instance == null) {
			instance = new EmbeddedClientFactory();
		}
		return instance;
	}
	
	
	
	private EmbeddedClientFactory() {
		databaseMessageSource = new ReceiverMessageSource();
		database = new Database(databaseMessageSource);
		try {
			database.startServer();
		} catch (IOException e) {
			throw new RuntimeException("Failure starting database:" + e.getMessage(), e);
		}
	}
	
    /**
     * Create an embedded client connected to a singleton database instance within same VM
     */
    public Client createEmbeddedClient() {
    	DirectClient client = new DirectClient(Authority.Authoritative, databaseMessageSource);
    	try {
			client.connect();
		} catch (ArchException e) {
			throw new RuntimeException("Failure connecting client to database:" + e.getMessage(), e);
		}
		return client;
    }
    
    public synchronized void shutdownDatabase() {
    	instance = null;
    	database.close();
    }
}
