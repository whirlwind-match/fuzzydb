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
package com.wwm.db;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.wwm.context.IShutdown;
import com.wwm.db.core.Settings;
import com.wwm.db.core.exceptions.ArchException;

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
 * Also, storeMgr should close its' connections within finalise.
 */
public class StoreMgr implements IShutdown {

    private static StoreMgr instance;

    private Map<String, Store> storesByStoreName = new HashMap<String, Store>();
    private Map<String,Client> clientsByServer = new TreeMap<String,Client>();


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
        String storeName = url.getPath();
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

            Client client;
            try {
                client = getClient(url);
                store = client.openStore(storeName, true);
            } catch (ArchException e) {
                throw new RuntimeException(e);
            }
        }
        storesByStoreName.put(storeName, store);
        return store;
    }

    /**
     * Internal util for getting cached client connection, unique for client:port (i.e. single
     * client shared for stores on same server)
     */
    synchronized private Client getClient(URL url) {
        // NOTE: this can be inefficient, as it's only done once
        // for the first time for each new store

        String server = url.getHost();
        if (server == null || server.length() == 0) {
            server = "127.0.0.1";
        }
        int port = (url.getPort() != -1) ? url.getPort() : Settings.getInstance().getPrimaryServerPort();

        String key = server + ":" + port;
        Client client = clientsByServer.get(key);
        if (client != null) {
            return client;
        }

        InetSocketAddress addr = new InetSocketAddress(server, port);
        client = Factory.createClient();
        client.connect(addr);
        clientsByServer.put(key, client);
        return client;
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
