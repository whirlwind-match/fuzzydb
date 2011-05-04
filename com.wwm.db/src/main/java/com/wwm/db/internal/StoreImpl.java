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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.util.Assert;

import com.wwm.db.GenericRef;
import com.wwm.db.Helper;
import com.wwm.db.Ref;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.core.LogFactory;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.internal.comms.messages.AllocNewIdsCmd;
import com.wwm.db.internal.comms.messages.AllocNewIdsRsp;
import com.wwm.db.internal.comms.messages.BeginAndCommitCmd;
import com.wwm.db.internal.comms.messages.CommitCmd;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.marker.ITraceWanted;
import com.wwm.db.marker.IWhirlwindItem;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultSet;
import com.wwm.db.query.RetrieveSpec;
import com.wwm.db.query.RetrieveSpecResult;
import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.expressions.LogicExpr;
import com.wwm.io.core.ArchInStream;
import com.wwm.io.core.ArchOutStream;
import com.wwm.io.core.Authority;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.Message;
import com.wwm.io.core.messages.Command;
import com.wwm.io.core.messages.Response;

/**
 * TODO: Operations on the current transaction are not yet implemented... but could be.
 * 
 * @author Adrian Clarkson
 * @author Neale Upstone
 */
public class StoreImpl implements Store {

	private class StoreImplContext implements Helper {
		private static final int maxIdsToRequest = 1024;
		private final int storeId;
		private final String storeName;
		private final AbstractClient client;
		private final NewObjectIds newIdCache = new NewObjectIds();
		private String defaultNamespace = "";
		private final HashMap<Class<?>, Integer> nextIdReqCount = new HashMap<Class<?>, Integer>();
		public StoreImplContext(int storeId, String storeName, AbstractClient client ) {
			this.storeId = storeId;
			this.storeName = storeName;
			this.client = client;
		}

		public ArchInStream newInputStream(byte[] data) throws IOException {
			return client.newInputStream(data);
		}
		
		public ArchOutStream newOutputStream(OutputStream out) throws IOException {
			return client.newOutputStream(storeId, out);
		}
		
		public String getDefaultNamespace() {
			return defaultNamespace;
		}

		public void setDefaultNamespace(String defaultNamespace) {
			this.defaultNamespace = defaultNamespace;
		}

		public ClassLoaderInterface getCli() {
			return client.getCli();
		}

		public AbstractClient getClient() {
			return client;
		}

		public NewObjectIds getNewIdCache() {
			return newIdCache;
		}

		public int getStoreId() {
			return storeId;
		}

		public String getStoreName() {
			return storeName;
		}
		
		/**
		 * Get the next reference to an object of the supplied class in the specified namespace
		 */
		public <C> RefImpl<C> getNextRef(String namespace, Class<C> clazz) {
			NewObjectIds newIdCache = context.getNewIdCache();
			synchronized (newIdCache) {
				RefImpl<C> ref = null;
				if ((ref = newIdCache.getNextRef(namespace, clazz)) == null ) {
					
					Integer reqCount = nextIdReqCount.get(clazz);
					if (reqCount == null) {
						reqCount = 1;
						nextIdReqCount.put(clazz, 2);
					} else {
						if (reqCount < maxIdsToRequest) {
							int nextCount =  Math.min(reqCount*2, maxIdsToRequest);
							nextIdReqCount.put(clazz, nextCount);
						}
					}
					AllocNewIdsCmd cmd = new AllocNewIdsCmd(storeId, client.getNextId(), namespace, clazz, reqCount);
					AllocNewIdsRsp rsp = (AllocNewIdsRsp) client.getConnection().execute(Authority.Authoritative, cmd);
					newIdCache.addRefs(namespace, clazz, rsp.getSlice(), rsp.getTableid(), rsp.getFirstOid(), rsp.getCount());
					ref = newIdCache.getNextRef(namespace, clazz);
				}
				assert(ref != null);
				return ref;
			}
		}

		public void disposeTransaction(int tid) {
			client.disposeTransaction(tid);
		}

		public void addToMetaCache(MetaObject<?> mo) {
			client.addToMetaCache(mo);
		}

		public Ref getRef(Object obj) throws UnknownObjectException {
			return client.getRef(obj);
		}

		public synchronized <E> GenericRef<E> getGenericRef(E obj)  throws UnknownObjectException {
			return new GenericRefImpl<E>(getRef(obj));
		}
		
		public int getVersion(Object obj) throws UnknownObjectException {
			return client.getVersion(obj);
		}
		
	}

	private boolean allowTxOverlapInThread = false;
	
	private static Logger log = LogFactory.getLogger(StoreImpl.class);
	
	
	public void setAllowTxOverlapInThread(boolean allowTxOverlapInThread) {
		this.allowTxOverlapInThread = allowTxOverlapInThread;
	}
	
	
	private final StoreImplContext context;
	private final Authority authority;
	private final StoreImpl peer;
	
	private final ThreadLocal<Transaction> currentTransaction = new ThreadLocal<Transaction>();
	
	
	
	
	/**Constructs an Authoritative store
	 * @param storeId
	 * @param storeName
	 * @param client
	 */
	public StoreImpl(int storeId, String storeName, AbstractClient client) {
		this.context = new StoreImplContext(storeId, storeName, client);
		this.authority = Authority.Authoritative;
		this.peer = new StoreImpl(this);
	}
	
	/**Constructs a Non-Authoritative store
	 * @param authPeer The Authoritative peer
	 */
	private StoreImpl(StoreImpl authPeer) {
		this.context = authPeer.getContext();
		this.authority = Authority.NonAuthoritative;
		this.peer = authPeer;
	}
	
	public ArchInStream newInputStream(byte[] data) throws IOException {
		return context.newInputStream(data);
	}
	
	public ArchOutStream newOutputStream(OutputStream out) throws IOException {
		return context.newOutputStream(out);
	}
	
	public Response execute(Command command) {
		
		TEMP_logIfTraceWanted(command, false);
		
		return context.getClient().getConnection().execute(authority, command);
	}
	
	private void TEMP_logIfTraceWanted(Message m, boolean receiving) {
		if (m instanceof BeginAndCommitCmd){
			BeginAndCommitCmd bacc = (BeginAndCommitCmd) m;
			CommitCmd cc = (CommitCmd) bacc.getPayload();
			if (cc.getUpdated() != null && cc.getUpdated().get(0).getObject() instanceof ITraceWanted){
				System.err.println( (receiving ? "<- " : "-> " ) +
					cc.getUpdated().get(0) );
//							"*** Set breakpoint here: (PacketCodec.java:234)");
//					new Exception().printStackTrace();
				
				// We should be seeing that Synced... has values in the 
				// ids map, but at the server end, that map is
				// appearing empty... WHY!
			}
		}
	}

	
	public int getStoreId() {
		return context.getStoreId();
	}
	
	/**
	 * Get next unique Id for client side stuff.  e.g. for cmdId, or queryId
	 * @return
	 */
	public int getNextId() {
		return context.getClient().getNextId();
	}
	
	@SuppressWarnings("unchecked")
	public <E> RefImpl<E> getNextRef(String namespace, E obj) {
		assert(authority == Authority.Authoritative);
		Class<E> clazz = (Class<E>) obj.getClass();
		return context.getNextRef(namespace, clazz);
	}
	
	public Transaction begin() {
		if (allowTxOverlapInThread && currentTransaction.get() != null) {
			log.warning("Multiple transactions active in one Thread. Store.currentTransaction() will only return the last started");
		}
		else {
			Assert.state(currentTransaction.get() == null, "Current transaction already active on this thread. Nested transactions not supported");
		}
		TransactionImpl transaction = new TransactionImpl(this, context.getDefaultNamespace());
		currentTransaction.set(transaction);
		return transaction;
	}

	public Transaction currentTransaction() {
		return currentTransaction.get();
	}
	
	void clearCurrentTransaction() {
		currentTransaction.remove();
	}

	public Store getAuthStore() {
		return authority == Authority.Authoritative ? this : peer;
	}

	public Class<?> getDbClass(String className) {
		throw new UnsupportedOperationException(); // to do
	}

	public Collection<Class<?>> getDbClasses() {
		throw new UnsupportedOperationException(); // to do
	}

	public Collection<String> getNamespaces(Class<?> dbClass) {
		throw new UnsupportedOperationException(); // to do
	}

	public Store getNonAuthStore() {
		return authority == Authority.NonAuthoritative ? this : peer;
	}

	public String getStoreName() {
		return context.getStoreName();
	}

	public void setDefaultNamespace(String namespace) {
		throw new UnsupportedOperationException(); // to do
	}

	public void commit() {
		throw new UnsupportedOperationException(); // to do
	}

	public <E> long count(Class<E> clazz) {
		throw new UnsupportedOperationException(); // to do
	}

	public Ref create(Object obj) {
		throw new UnsupportedOperationException(); // to do
	}

	public Ref[] create(Object[] objs) {
		throw new UnsupportedOperationException(); // to do
	}

	public Ref[] create(Collection<Object> objs) {
		throw new UnsupportedOperationException(); // to do
	}

	public void delete(Object obj) {
		throw new UnsupportedOperationException(); // to do
	}
	
	public void delete(Ref ref) {
		throw new UnsupportedOperationException(); // to do
	}

	public void delete(Ref[] ref) {
		throw new UnsupportedOperationException(); // to do
	}

	public void delete(Collection<Ref> ref) {
		throw new UnsupportedOperationException(); // to do
	}

	public void dispose() {
		throw new UnsupportedOperationException(); // to do
	}

	public Object execute(String methodName, Ref ref, Object param) {
		throw new UnsupportedOperationException(); // to do
	}

	public String getNamespace() {
		throw new UnsupportedOperationException(); // to do
	}

	public long getVersion(Ref ref) {
		throw new UnsupportedOperationException(); // to do
	}

	public String[] listNamespaces() {
		throw new UnsupportedOperationException(); // to do
	}

	public void modifyAttributes(IWhirlwindItem obj, CardinalAttributeMap<IAttribute> add, Collection<Long> remove) {
		throw new UnsupportedOperationException(); // to do
	}

	public void modifyField(Object obj, String field, Object newval) {
		throw new UnsupportedOperationException(); // to do
	}

	public void modifyNominee(IAttributeContainer obj, Object nominee) {
		throw new UnsupportedOperationException(); // to do
	}

	public void modifyNomineeField(IAttributeContainer obj, String field, Object newval) {
		throw new UnsupportedOperationException(); // to do
	}

	public void popNamespace() {
		throw new UnsupportedOperationException(); // to do
	}

	public void pushNamespace(String namespace) {
		throw new UnsupportedOperationException(); // to do
	}

	public <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr) {
		throw new UnsupportedOperationException(); // to do
	}

	public <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr, int fetchSize) {
		throw new UnsupportedOperationException(); // to do
	}

	public <E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search) {
		throw new UnsupportedOperationException(); // to do
	}

	public <E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search, int fetchSize) {
		throw new UnsupportedOperationException(); // to do
	}

	public <E> long queryCount(Class<E> clazz, LogicExpr index, LogicExpr expr) {
		throw new UnsupportedOperationException(); // to do
	}

	public <E> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search) {
		throw new UnsupportedOperationException(); // to do
	}

	public <E> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search, int fetchSize) {
		throw new UnsupportedOperationException(); // to do
	}

	public <E> E refresh(E obj) {
		throw new UnsupportedOperationException(); // to do
	}

	public Object retrieve(Ref ref) {
		throw new UnsupportedOperationException(); // to do
	}

	public Map<Ref, Object> retrieve(Collection<Ref> refs) {
		throw new UnsupportedOperationException(); // to do
	}

	public RetrieveSpecResult retrieve(RetrieveSpec spec) {
		throw new UnsupportedOperationException(); // to do
	}

	public <E> E retrieve(Class<E> clazz, String keyfield, Comparable<?> keyval) {
		throw new UnsupportedOperationException(); // to do
	}

	public <E> Collection<E> retrieveAll(Class<E> clazz, String keyfield, Comparable<?> keyval) {
		throw new UnsupportedOperationException(); // to do
	}

	public void setNamespace(String namespace) {
		throw new UnsupportedOperationException(); // to do
	}

	public void update(Object obj) {
		throw new UnsupportedOperationException(); // to do
	}

	public void update(Object[] objs) {
		throw new UnsupportedOperationException(); // to do
	}

	public void update(Collection<Object> objs) {
		throw new UnsupportedOperationException(); // to do
	}

	public boolean isAuthoritative() {
		return authority==Authority.Authoritative;
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

	public final StoreImplContext getContext() {
		return context;
	}

	public void disposeTransaction(int tid) {
		context.disposeTransaction(tid);
	}

	public void addToMetaCache(MetaObject<?> mo) {
		context.addToMetaCache(mo);	
	}

	public void forceStart() {
		throw new UnsupportedOperationException();
	}

	public <E> E retrieveFirstOf(Class<E> clazz) {
		throw new UnsupportedOperationException();
	}

	public <E> GenericRef<E> createGeneric(E obj) {
		throw new UnsupportedOperationException();
	}

	public <E> E retrieve(GenericRef<E> ref) {
		throw new UnsupportedOperationException();
	}

	public Store getStore() {
		return this;
	}

	public void troff() {
		// do nothing - backwards compat for Db1 port
	}

	public void tron(Logger log) {
		// do nothing - backwards compat for Db1 port
	}
}
