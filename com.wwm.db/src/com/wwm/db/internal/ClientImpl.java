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
package com.wwm.db.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import likemynds.util.UncaughtExceptionLogger;

import com.wwm.db.Client;
import com.wwm.db.GenericRef;
import com.wwm.db.Helper;
import com.wwm.db.Ref;
import com.wwm.db.ServerStats;
import com.wwm.db.Store;
import com.wwm.db.core.Settings;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.exceptions.AuthorityException;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.exceptions.UnknownStoreException;
import com.wwm.db.internal.comms.messages.CreateStoreCmd;
import com.wwm.db.internal.comms.messages.CreateStoreRsp;
import com.wwm.db.internal.comms.messages.DeleteStoreCmd;
import com.wwm.db.internal.comms.messages.DisposeCmd;
import com.wwm.db.internal.comms.messages.ListStoresCmd;
import com.wwm.db.internal.comms.messages.ListStoresRsp;
import com.wwm.db.internal.comms.messages.OpenStoreCmd;
import com.wwm.db.internal.comms.messages.OpenStoreRsp;
import com.wwm.db.internal.comms.messages.ShutdownCmd;
import com.wwm.io.packet.ArchInStream;
import com.wwm.io.packet.ArchOutStream;
import com.wwm.io.packet.ClassLoaderInterface;
import com.wwm.io.packet.ClassTokenCache;
import com.wwm.io.packet.exceptions.CommsErrorException;
import com.wwm.io.packet.impl.DummyCli;
import com.wwm.io.packet.layer1.Authority;
import com.wwm.io.packet.layer1.ClientConnectionManager;
import com.wwm.io.packet.layer1.ClientConnectionManagerImpl;
import com.wwm.io.packet.messages.Command;

public class ClientImpl implements Client {

    // Ensure we log all uncaught exceptions for all client apps
    static { UncaughtExceptionLogger.initialise(); }


    private static class ClientImplContext implements Helper {

        private ClientConnectionManager connection = null;
        private final ClassLoaderInterface cli = new DummyCli();
        private int nextId = 1;
        private final Map<String, StoreImpl> stores = new HashMap<String, StoreImpl>();
        private final MetaMap metaMap = new MetaMap();
        private final ClassTokenCache ctc = new ClassTokenCache(false);

        ClientImplContext() {

        }

        void setConnection(ClientConnectionManager connection) {
            this.connection = connection;
        }

        public ClassLoaderInterface getCli() {
            return cli;
        }

        public ClientConnectionManager getConnection() {
            return connection;
        }

        public synchronized int getNextId() {
            return nextId++;
        }

        public Map<String, StoreImpl> getStores() {
            return stores;
        }

        public ArchInStream newInputStream(byte[] data) throws IOException {
            return ArchInStream.newInputStream(data, ctc, cli);
        }

        public ArchOutStream newOutputStream(int storeId, OutputStream out) throws IOException {
            return ArchOutStream.newOutputStream(out, storeId, ctc);
        }

        public void requestClassData(Authority authority, int storeId, String className) throws IOException {
            connection.requestClassData(authority, storeId, className);
        }

        public void waitForClass(int storeId, String className) {
            cli.waitForClass(storeId, className);
        }

        public void addToMetaCache(MetaObject<?> mo) {
            synchronized (metaMap) {
                metaMap.add(mo);
            }
        }

        public int getVersion(Object obj) throws UnknownObjectException {
            synchronized (metaMap) {
                MetaObject<?> mo = metaMap.find(obj);
                if (mo != null) {
                    return mo.getVersion();
                }
            }
            throw new UnknownObjectException();
        }

        public Ref getRef(Object obj) throws UnknownObjectException {
            synchronized (metaMap) {
                MetaObject<?> mo = metaMap.find(obj);
                if (mo != null) {
                    return mo.getRef();
                }
            }
            throw new UnknownObjectException();
        }

        public synchronized <E> GenericRef<E> getGenericRef(E obj)  throws UnknownObjectException {
            return new GenericRefImpl<E>(getRef(obj));
        }
    } // end of inner class ClientImplContext

    private final int lazyFlushCount = 100;	// Number of transactions, queries etc. to have disposed before flushing

    private final ClientImplContext context;
    private final Authority authority;
    private final ClientImpl peer;
    private final ArrayList<Integer> disposedTransactions = new ArrayList<Integer>();
    private final ArrayList<Integer> disposedQueries = new ArrayList<Integer>();

    public ClientImpl(Authority authority) {
        this.authority = authority;
        context = new ClientImplContext();
        peer = new ClientImpl(this, authority == Authority.Authoritative ? Authority.NonAuthoritative : Authority.Authoritative, context);
    }

    private ClientImpl(ClientImpl peer, Authority authority, ClientImplContext context) {
        this.context = context;
        this.authority = Authority.NonAuthoritative;
        this.peer = peer;
    }

    public ArchInStream newInputStream(byte[] data) throws IOException {
        return context.newInputStream(data);
    }

    public ArchOutStream newOutputStream(int storeId, OutputStream out) throws IOException {
        return context.newOutputStream(storeId, out);
    }

    public void connect() throws ArchException {
        connect(Settings.getInstance().getPrimaryServer());
    }

    public void connect(String server) throws ArchException {
        InetSocketAddress address = new InetSocketAddress(
                server,
                Settings.getInstance().getPrimaryServerPort());
        connect(address);
    }

    public void connect(InetSocketAddress addr) throws ArchException {
        try {
            context.setConnection(new ClientConnectionManagerImpl(addr, context.getCli()));
        } catch (IOException e) {
            throw new CommsErrorException(e);
        }
    }

    public Store createStore(String storeName) throws ArchException {
        //throwIfNotAuthoritative();
        Map<String, StoreImpl> stores = context.getStores();
        synchronized (stores) {
            CreateStoreCmd cmd = new CreateStoreCmd(getNextId(), storeName);
            CreateStoreRsp rsp = (CreateStoreRsp) context.getConnection().execute(Authority.Authoritative, cmd);
            // All failures result in the lower layers throwing an exception.
            assert(rsp.getNewStoreName().equals(storeName));
            StoreImpl store = new StoreImpl(rsp.getNewStoreId(), storeName, this);
            stores.put(storeName, store);
            return store.getAuthStore();
        }
    }

    @SuppressWarnings("unused")
    private void throwIfNotAuthoritative() throws AuthorityException {
        if (authority != Authority.Authoritative) {
            throw new AuthorityException();
        }
    }

    public Collection<String> listStores() throws ArchException {
        ListStoresCmd cmd = new ListStoresCmd(getNextId());
        ListStoresRsp rsp = (ListStoresRsp) context.getConnection().execute(authority, cmd);
        return rsp.getStoreNames();
    }

    public Collection<String> listDbClasses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Class<?> getDbClass(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<Class<?>> getDbClasses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<String> getNamespaces(Class<?> dbClass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Store openStore(String storeName) throws ArchException {
        Map<String, StoreImpl> stores = context.getStores();
        synchronized (stores) {
            StoreImpl store = stores.get(storeName);
            if (store == null) {
                OpenStoreCmd cmd = new OpenStoreCmd(getNextId(), storeName);
                OpenStoreRsp rsp = (OpenStoreRsp) context.getConnection().execute(authority, cmd);
                assert(rsp.getOpenedStoreName().equals(storeName));
                store = new StoreImpl(rsp.getOpenedStoreId(), storeName, this);
                stores.put(storeName, store);
            }
            assert(store != null);
            return authority==Authority.Authoritative ? store.getAuthStore() : store.getNonAuthStore();
        }
    }

    /**
     * TODO: Add server support for this API, such that OpenStoreCmd allows the store to be created.
     * This implementation is flawed, but not in a major way: It is possible to try to open a
     * store on nonAuth server, which doesn't exist, and then try to create it on the auth server
     * when it does exist there.  This is no worse than the end-user would have done, for now.
     */
    public Store openStore(String storeName, boolean canCreate) throws ArchException {
        try {
            return openStore(storeName);
        } catch (UnknownStoreException e){
            if (canCreate){
                return createStore(storeName);
            }
            throw e; // exception stands if we're not allowed to create
        }
    }


    public void deleteStore(String storeName) throws ArchException {
        //throwIfNotAuthoritative();
        Map<String, StoreImpl> stores = context.getStores();
        synchronized (stores) {
            stores.remove(storeName);
            DeleteStoreCmd cmd = new DeleteStoreCmd(getNextId(), storeName);
            context.getConnection().execute(Authority.Authoritative, cmd);
        }
    }

    public Client getAuthClient() {
        return authority == Authority.Authoritative ? this : peer;
    }

    public Client getNonAuthClient() {
        return authority == Authority.NonAuthoritative ? this : peer;
    }

    public boolean isAuthoritative() {
        return authority == Authority.Authoritative;
    }

    public Ref getRef(Object obj) throws UnknownObjectException {
        return context.getRef(obj);
    }

    public synchronized <E> GenericRef<E> getGenericRef(E obj)  throws UnknownObjectException {
        return new GenericRefImpl<E>(getRef(obj));
    }

    public int getVersion(Object obj) throws UnknownObjectException {
        return context.getVersion(obj);
    }

    public int getNextId() {
        return context.getNextId();
    }

    public ClassLoaderInterface getCli() {
        return context.getCli();
    }

    public ClientConnectionManager getConnection() {
        return context.getConnection();
    }

    public synchronized void disposeTransaction(int tid) throws ArchException {
        disposedTransactions.add(tid);
        lazyFlushDisposed();
    }

    public synchronized void disposeQuery(int qid) throws ArchException {
        disposedQueries.add(qid);
        lazyFlushDisposed();
    }

    private synchronized void lazyFlushDisposed() throws ArchException {
        if (disposedTransactions.size() + disposedQueries.size() >= lazyFlushCount) {
            Command cmd = new DisposeCmd(getNextId(), disposedTransactions, disposedQueries);
            getConnection().execute(authority, cmd);
        }
    }

    public void requestClassData(int storeId, String className) throws IOException {
        context.requestClassData(authority, storeId, className);
    }

    public void waitForClass(int storeId, String className) {
        context.waitForClass(storeId, className);
    }

    public void addToMetaCache(MetaObject<?> mo) {
        context.addToMetaCache(mo);
    }

    public void shutdownServer() throws ArchException {
        context.getConnection().execute(authority, new ShutdownCmd(getNextId()));
    }



    public ServerStats getStats(boolean forceGC) {
        throw new UnsupportedOperationException(); // FIXME: Implement
    }

    public void disconnect() {
        //		 FIXME: Adrian: Is this correct
        context.getConnection().close();
        context.setConnection(null);
    }

    public boolean isConnected() {
        return context.getConnection() != null;  // FIXME: Adrian is this correct.
    }
}
