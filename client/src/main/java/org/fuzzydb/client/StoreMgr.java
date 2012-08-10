/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.client;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.wwm.context.IShutdown;
import com.wwm.db.core.Settings;

/**
 * Implements access to a store by Url, and manages sharing of objects that should be shared, such
 * as Client connections and the store instances.
 *
 * Url is something along the lines of:
 * StoreMgr.getInstance().getStore("wwmdb://server/storeName?master=true&createStore=true").
 * ... although master and createStore are not implemented as such.
 * 
 * FIXME: Note: StoreMgr.getInstance() should not be used, but instead
 * we should put a StoreMgr instance in the application context, which
 * then allows those instances to be disposed of.
 * Also, storeMgr should close its' connections with an appropriate lifecycle/context shutdown hook
 */
public class StoreMgr implements IShutdown {

    private static StoreMgr instance;

    private final Map<String, Store> storesByStoreName = new HashMap<String, Store>();
    private final Map<String,Client> clientsByServer = new TreeMap<String,Client>();


    public static synchronized StoreMgr getInstance() {
        if (instance == null){
            instance = new StoreMgr();
        }
        return instance;
    }


    private StoreMgr(){
        // private to force singleton
    }


    /**
     * Get cached store instance for this URL.
     * This accepts URL for protocol "wwmdb", and implements defaults for
     * the server and port addresses where necessary.
     * The URL's path is used as the storeName
     */
    public Store getStore(String strUrl) {
        try {
            URL url = WWMDBProtocolHander.getAsURL(strUrl);
            assert(url.getProtocol().equals("wwmdb"));
            return getStore(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get cached store instance for this URL.
     * This accepts URL for protocol "wwmdb", and implements defaults for
     * the server and port addresses where necessary.
     * The URL path is used as the storeName
     */
    private Store getStore(URL url) {
    	Assert.state(url.getPath().startsWith("/"), "URL path must start with /");
        String storeName = url.getPath().substring(1);
        Store store = storesByStoreName.get(storeName);
        if (store != null) {
            return store;
        }

        synchronized (storesByStoreName) {
            // re-check now that have lock
            store = storesByStoreName.get(storeName);
            if (store != null) {
                return store;
            }

            store = openStore(url);
        }
        storesByStoreName.put(storeName, store);
        return store;
    }


    /**
     * Open store for remote or embedded store
     * @param url
     * @return
     */
	public Store openStore(URL url) {
		Client client = getClient(url);
		return client == null ? null : client.openStore(url.getPath().substring(1), true);
	}

    /**
     * Internal util for getting cached client connection, unique for client:port (i.e. single
     * client shared for stores on same server)
     * @return a connected Client
     */
    synchronized private Client getClient(URL url) {
        // NOTE: this can be inefficient, as it's only done once
        // for the first time for each new store

    	String server = url.getHost();
    	String key;
    	if (StringUtils.hasLength(server)) {
    		int port = getPort(url);
    		key = server + ":" + port;
    	}
    	else {
    		key = "[embedded]";
    	}

        Client client = clientsByServer.get(key);
        if (client != null) {
            return client;
        }

    	if (StringUtils.hasLength(server)) {
	        InetSocketAddress addr = new InetSocketAddress(server, getPort(url));
	        client = Factory.createClient();
	        client.connect(addr);
	        clientsByServer.put(key, client);
	        return client;
    	}
    	else {
    		try {
				Class<?> cl = Class.forName("org.fuzzydb.client.EmbeddedClientFactory");
				Method m = cl.getMethod("getInstance");
				ClientFactory factory = (ClientFactory) m.invoke(null);
				return factory.createClient();
			} catch (Exception e) {
				return null;
			}
    	}
    }


	private int getPort(URL url) {
		return (url.getPort() != -1) ? url.getPort() : Settings.getInstance().getPrimaryServerPort();
	}


    @Override
    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }

    /**
     * Close all connections when we are shutdown
     */
    synchronized public void shutdown() {
        storesByStoreName.clear();
        for (Iterator<Entry<String,Client>> iterator = clientsByServer.entrySet().iterator(); iterator.hasNext();) {
            Entry<String,Client> entry = iterator.next();
            entry.getValue().disconnect();
            iterator.remove();
        }
    }
}
