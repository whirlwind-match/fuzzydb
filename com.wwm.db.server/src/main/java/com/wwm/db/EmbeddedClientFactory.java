package com.wwm.db;

import java.io.IOException;
import java.net.URL;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.wwm.db.exceptions.UnknownStoreException;
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
		client.connect();
		return client;
    }
    
    public boolean isDatabaseClosed() {
    	return database.isClosed();
    }
    
    public synchronized void shutdownDatabase() {
    	instance = null;
    	database.close();
    }



    /**
     * Open the store for the given URL.
     * 
     * Should handle local/remote, and is allowed to create a store when running locally.
     */
	public Store openStore(URL url) {
		
		Assert.state(url.getProtocol().equals("wwmdb"));
		String host = url.getHost();
		
		if (!StringUtils.hasLength(host)) {
			return openEmbeddedStore(url);
		}
		
		// If host is specified, use StoreMgr to get access.
		return StoreMgr.getInstance().getStore(url.toExternalForm());
	}



	private Store openEmbeddedStore(URL url) {
		Client client = createEmbeddedClient();
		try {
			return client.openStore(url.getPath());
		} catch (UnknownStoreException e) {
			return client.createStore(url.getPath());
		}
	}
}
