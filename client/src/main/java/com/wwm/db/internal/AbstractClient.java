package com.wwm.db.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.springframework.util.Assert;

import com.wwm.db.Client;
import com.wwm.db.Helper;
import com.wwm.db.Ref;
import com.wwm.db.ServerStats;
import com.wwm.db.Store;
import com.wwm.db.core.LogFactory;
import com.wwm.db.core.UncaughtExceptionLogger;
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
import com.wwm.io.core.ArchInStream;
import com.wwm.io.core.ArchOutStream;
import com.wwm.io.core.Authority;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.ClassTokenCache;
import com.wwm.io.core.exceptions.ConnectionLostException;
import com.wwm.io.core.impl.DummyCli;
import com.wwm.io.core.layer1.ClientConnectionManager;
import com.wwm.io.core.layer1.ClientMessagingManager;
import com.wwm.io.core.messages.Command;
import com.wwm.io.core.messages.Response;

/**
 * Provides operations and knows if this is Authoritative or NonAuthoritative
 * 
 * @author Adrian Clarkson
 * @author Neale Upstone
 */
public abstract class AbstractClient implements Cloneable, Client {

	private static final Logger log = LogFactory.getLogger(AbstractClient.class);
	
    // Ensure we log all uncaught exceptions for all client apps
    static { UncaughtExceptionLogger.initialise(); }

	private static class ClientImplContext implements Helper {
	
	        private ClientConnectionManager connection = null;
	        private final ClassLoaderInterface cli = new DummyCli();
	        private final AtomicInteger nextId = new AtomicInteger(0);
	        private final Map<String, StoreImpl> stores = new HashMap<String, StoreImpl>();
	        private final MetaMap metaMap = new MetaMap();
	        private final ClassTokenCache ctc = new ClassTokenCache(false);
	
	        ClientImplContext() {
	
	        }
	
	        public void setConnection(ClientConnectionManager connection) {
	            this.connection = connection;
	        }
	
	        public ClassLoaderInterface getCli() {
	            return cli;
	        }
	
	        public ClientConnectionManager getConnection() {
	            return connection;
	        }
	
	        public int getNextId() {
	            return nextId.incrementAndGet();
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
	
	        // TODO: Extract this to own class which can then be independently tested for correct behaviour
	        public void addToMetaCache(MetaObject<?> mo) {
                metaMap.add(mo);
	        }
	
	        @Override
			public int getVersion(Object obj) throws UnknownObjectException {
                MetaObject<?> mo = metaMap.find(obj);
                if (mo != null) {
                    return mo.getVersion();
                }
	            throw new UnknownObjectException();
	        }
	
	        @SuppressWarnings("unchecked")
			public <E> Ref<E> getRef(E obj) throws UnknownObjectException {
                MetaObject<?> mo = metaMap.find(obj);
                if (mo != null) {
                    return (Ref<E>) mo.getRef();
                }
	            throw new UnknownObjectException();
	        }
	
	    } // end of inner class ClientImplContext

	private final int lazyFlushCount = 100;
	protected final ClientImplContext context;
	protected final Authority authority;
	protected AbstractClient peer;
	private final ArrayList<Integer> disposedTransactions = new ArrayList<Integer>();
	private final ArrayList<Integer> disposedQueries = new ArrayList<Integer>();

	/**
	 * Construct client and it's peer
	 */
	public AbstractClient(Authority authority) {
		super();
		this.context = new ClientImplContext();
		this.authority = authority;
	}

	
	/**
	 * Set an authoritative client
	 */
	public void setAuthorititivePeer(AbstractClient peer) {
		Assert.state(authority == Authority.NonAuthoritative, "this Client must be NonAuthoritative");
		Assert.state(peer.authority == Authority.Authoritative, "peer Client must be Authoritative");

		this.peer = peer;
		peer.peer = this;
	}
	

	public ArchInStream newInputStream(byte[] data) throws IOException {
	    return context.newInputStream(data);
	}

	public ArchOutStream newOutputStream(int storeId, OutputStream out) throws IOException {
	    return context.newOutputStream(storeId, out);
	}

	@Override
	public Store createStore(String storeName) {
	    //throwIfNotAuthoritative();
	    Map<String, StoreImpl> stores = context.getStores();
	    synchronized (stores) {
	        CreateStoreCmd cmd = new CreateStoreCmd(getNextId(), storeName);
	        Assert.state(isAuthoritative(), "Can only create stores from an authoritative client"); 
	        CreateStoreRsp rsp = (CreateStoreRsp) executeCmd(cmd); //context.getConnection().execute(Authority.Authoritative, cmd);
	        log.info("Created store: {}", storeName);
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

	@Override
	public Collection<String> listStores() {
	    ListStoresCmd cmd = new ListStoresCmd(getNextId());
	    ListStoresRsp rsp = (ListStoresRsp) executeCmd(cmd);
	    return rsp.getStoreNames();
	}


	protected Response executeCmd(Command cmd) {
		ClientConnectionManager connection = context.getConnection();
		if (connection == null) {
			throw new ConnectionLostException();
		}
		return connection.execute(authority, cmd);
	}

	@Override
	public Collection<String> listDbClasses() {
	    // TODO Auto-generated method stub
	    throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getDbClass(String name) {
	    // TODO Auto-generated method stub
	    throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Class<?>> getDbClasses() {
	    // TODO Auto-generated method stub
	    throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getNamespaces(Class<?> dbClass) {
	    // TODO Auto-generated method stub
	    throw new UnsupportedOperationException();
	}

	/**
	 * Opens the named store.  If this store was created or opened already from
	 * this session, then the cached knowledge will be used.
	 * When commands are flushed, if the store was deleted, then this
	 * will fail.
	 * A store being deleted will rarely happen other than in tests, so
	 * this is by far the better approach than hitting the server every time.
	 */
	@Override
	public Store openStore(String storeName) {
	    Map<String, StoreImpl> stores = context.getStores();
	    synchronized (stores) {
	        StoreImpl store = stores.get(storeName);
	        if (store == null) {
	            OpenStoreCmd cmd = new OpenStoreCmd(getNextId(), storeName);
	            OpenStoreRsp rsp = (OpenStoreRsp) executeCmd(cmd);
	            assert(rsp.getOpenedStoreName().equals(storeName));
	            store = new StoreImpl(rsp.getOpenedStoreId(), storeName, this);
	            stores.put(storeName, store);
	        }
	        assert(store != null);
	        return authority==Authority.Authoritative ? store.getAuthStore() : store.getNonAuthStore();
	    }
	}

	/**
	 * TODO (maybe: we're talking read vs write transaction if we do it):
	 * Add server support for this API, such that OpenStoreCmd allows the store to be created.
	 * This implementation is flawed, but not in a major way: It is possible to try to open a
	 * store on nonAuth server, which doesn't exist, and then try to create it on the auth server
	 * when it does exist there.  This is no worse than the end-user would have done, for now.
	 * 
	 * @throws UnknownStoreException if storeName doesn't exist and !canCreate
	 */
	@Override
	public Store openStore(String storeName, boolean canCreate) {
		if (!listStores().contains(storeName) && canCreate) {
			return createStore(storeName);
		}
        return openStore(storeName);
	}

	@Override
	public void deleteStore(String storeName) {
	    //throwIfNotAuthoritative();
	    Map<String, StoreImpl> stores = context.getStores();
	    synchronized (stores) {
	        stores.remove(storeName);
	        DeleteStoreCmd cmd = new DeleteStoreCmd(getNextId(), storeName);
	        Assert.state(isAuthoritative(), "Can only delete stores from an authoritative client"); 
	        executeCmd(cmd);
	    }
	}

	@Override
	public Client getAuthClient() {
		Assert.state(peer != null || authority == Authority.Authoritative, "A NonAuthoritative client MUST have an authoritative peer");
	    return authority == Authority.Authoritative ? this : peer;
	}

	@Override
	public Client getNonAuthClient() {
	    return authority == Authority.NonAuthoritative || peer == null ? this : peer;
	}

	@Override
	public boolean isAuthoritative() {
	    return authority == Authority.Authoritative;
	}

	public <E> Ref<E> getRef(E obj) throws UnknownObjectException {
	    return context.getRef(obj);
	}

	@Override
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

	public synchronized void disposeTransaction(int tid)
			{
			    disposedTransactions.add(tid);
			    lazyFlushDisposed();
			}

	public synchronized void disposeQuery(int qid) {
	    disposedQueries.add(qid);
	    lazyFlushDisposed();
	}

	private synchronized void lazyFlushDisposed() {
	    if (disposedTransactions.size() + disposedQueries.size() >= lazyFlushCount) {
	        Command cmd = new DisposeCmd(getNextId(), disposedTransactions, disposedQueries);
	        executeCmd(cmd);
	    }
	}

	public void addToMetaCache(MetaObject<?> mo) {
	    context.addToMetaCache(mo);
	}

	@Override
	public void shutdownServer() {
	    executeCmd(new ShutdownCmd(getNextId()));
	}

	@Override
	public ServerStats getStats(boolean forceGC) {
	    throw new UnsupportedOperationException(); // FIXME: Implement
	}

	@Override
	public void disconnect() {
	    //		 FIXME: Adrian: Is this correct
	    context.getConnection().close();
	    context.setConnection(null);
	}


	protected void setConnection(ClientMessagingManager connection) {
		context.setConnection(connection);
	}

	@Override
	public boolean isConnected() {
	    return context.getConnection() != null;  // FIXME: Adrian is this correct.
	}


	@Override
	public void connect(InetSocketAddress addr) {
		throw new UnsupportedOperationException(); // Must implement in subclass
	}


	@Override
	public void connect(String server) {
		throw new UnsupportedOperationException(); // Must implement in subclass
	}

}