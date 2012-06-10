package com.wwm.db;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.springframework.context.Lifecycle;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.wwm.db.core.LogFactory;
import com.wwm.db.internal.server.Database;
import com.wwm.db.internal.server.DatabaseFactory;
import com.wwm.io.core.Authority;

public class EmbeddedClientFactory implements ClientFactory, Lifecycle {

	
	static private final Logger log = LogFactory.getLogger(EmbeddedClientFactory.class);
	
	private static EmbeddedClientFactory instance;

	private boolean isPersistent = false;
	
	private Database database;
	
	/** If we find our HTTP server for web services, then we can start it */
	private Lifecycle httpServer;

	private final ReceiverMessageSource databaseMessageSource;

	public static synchronized EmbeddedClientFactory getInstance() {
		if (instance == null) {
			instance = new EmbeddedClientFactory();
		}
		return instance;
	}
	
	
	
	private EmbeddedClientFactory() {
		databaseMessageSource = new ReceiverMessageSource();
	}


	/**
	 * Start the database if not already started
	 */
	private synchronized void startDatabase() {
		if (database != null) {
			return;
		}

		database = DatabaseFactory.createDatabase(databaseMessageSource, isPersistent());
		try {
			database.startServer();
		} catch (IOException e) {
			throw new RuntimeException("Failure starting database:" + e.getMessage(), e);
		}
		
		httpServer = startHttpServiceIfAvailable();
	}


	private Lifecycle startHttpServiceIfAvailable() {
		Class<?> cl; 
		Lifecycle httpServer = null;
		try {
			cl = Class.forName("com.wwm.atom.impl.HttpServerFactory");
		} catch (ClassNotFoundException e) {
			return null;
		}
		try {
			Method m = cl.getMethod("getInstance");
			httpServer = (Lifecycle) m.invoke(null);
			if (httpServer == null) {
				return null;
			}
			// NOTE: The implementation will also want to use the embedded database 
			// - it therefore shouldn't try got get a client instance until the first request 
			httpServer.start(); 
			return httpServer;
		} catch (InvocationTargetException e) {
			log.warn("Can't start HttpServer", e);
			throw new RuntimeException(e);
		} catch (Exception e) {
			log.error("Error getting HttpServer instance", e);
			throw new RuntimeException(e);
		}
	}
	
    /**
     * Create an embedded client connected to a singleton database instance within same VM
     */
    @Override
	public Client createClient() {
    	startDatabase();
    	DirectClient client = new DirectClient(Authority.Authoritative, databaseMessageSource);
		client.connect();
		return client;
    }
    
    public boolean isDatabaseClosed() {
    	return database.isClosed();
    }
    
    public synchronized void shutdownDatabase() {
    	database.close();
    	database = null;
    	if (httpServer != null && httpServer.isRunning()) {
    		httpServer.stop();
    	}
    }



    /**
     * Open the store for the given URL.
     * 
     * Should handle local/remote, and is allowed to create a store when running locally.
     */
    public Store openStore(String url) throws MalformedURLException {
    	return openStore(WWMDBProtocolHander.getAsURL(url));
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
		Client client = createClient();
			return client.openStore(url.getPath().substring(1), true); // FIXME: substring() to remove leading / should be tidier
	}


	@Override
	public void start() {
	}


	@Override
	public void stop() {
		shutdownDatabase();
		instance = null;
	}


	@Override
	public boolean isRunning() {
		return !isDatabaseClosed();
	}


	public boolean isPersistent() {
		return isPersistent;
	}


	public void setPersistent(boolean isPersistent) {
		this.isPersistent = isPersistent;
	}
}
