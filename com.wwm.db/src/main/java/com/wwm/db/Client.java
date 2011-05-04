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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

import com.wwm.db.core.exceptions.ArchException;


/**Client interface.
 * @author ac
 *
 */
public interface Client extends Authority, Helper {

    /**Connect to the server.
     * The server and port is taken from the configuration file. If there is no file the default settings are used.
     * @throws ArchException
     * @throws IOException
     */
    public void connect();

    /**Connect to the specified server.
     * If a port is specified thsi is used. Otherwise the port is taken from the configuration file. If there is no file the default settings are used.
     * 
     * @param server A string specifying server. e.g. "127.0.0.1", "127.0.0.1:20000", "www.archopolis.com:20000"
     * @throws ArchException
     * @throws IOException
     */
    public void connect(String server);

    /**Connect to the specified server and port.
     * @param addr The full address of the server and port to conect to.
     * @return
     * @throws ArchException
     * @throws IOException
     */
    public void connect(InetSocketAddress addr);


    /**Creates a new store and returns an access object. An individual transaction is created and committed for this operation.
     * This is always Authoritative regardless of the authority of this client.
     * @param storeName
     * @return
     * @throws ArchException
     */
    public Store createStore(String storeName);

    /**Obtain a list of Stores. An individual transaction is used (and disposed of) for this operation.
     * The response is Authoritative only if this Client is Authoritative.
     * @return
     * @throws ArchException
     */
    public Collection<String> listStores();

    public Collection<String> listDbClasses();
    public Class<?> getDbClass(String name);
    public Collection<Class<?>> getDbClasses();
    public Collection<String> getNamespaces(Class<?> dbClass);

    /**
     * Opens the specified Store.
     * The Store object will be Authoritative if this Client is Authoritative.
     * @param storeName The name of the Store to open.
     * @return the Store.
     * @throws ArchException
     */
    public Store openStore(String storeName);

    /**
     * Opens the specified Store.  If the Store does not exist, then it is created,
     * if canCreate is true.
     * The Store object will be Authoritative if this Client is Authoritative.
     * @param storeName The name of the Store to open.
     * @param canCreate True if Client is allowed to create a new store
     * @return the Store.
     * @throws ArchException
     */
    public Store openStore(String storeName, boolean canCreate);


    /**Deletes the specified Store. An individual transaction is created and committed for this operation.
     * This is always Authoritative regardless of the authority of this client.
     * @param storeName The Store to delete.
     * @throws ArchException
     */
    public void deleteStore(String storeName);

    /**Returns an Authoritative version of this client.
     * This is guaranteed to be a low cost operation, the intended use is for applications to toggle between authoritative and non-authoritative Client views with this function.
     * If this client is already Authoritative, it safely returns a reference to this client.
     * @return the Authoritative client view.
     */
    public Client getAuthClient();

    /**Returns a Non-Authoritative version of this client.
     * This is guaranteed to be a low cost operation, the intended use is for applications to toggle between authoritative and non-authoritative Client views with this function.
     * If this client is already Non-Authoritative, it safely returns a reference to this client.
     * If there is only a single client (i.e. no peer), then it returns that client. 
     * @return the Non-Authoritative client view.
     */
    public Client getNonAuthClient();

    /**
     * Shuts down the server.
     * @throws ArchException
     */
    public void shutdownServer();

    // FIXME =============== ADRIAN PLEASE REVIEW BELOW (and in ClientImpl) ==================
    /**
     * Get memory usage, and possibly other stats from the server.
     * @param forceGC - force a garbage collection before checking memory usage.
     * @throws ArchException
     */
    public ServerStats getStats(boolean forceGC);

    public void disconnect();

    public boolean isConnected();
}
