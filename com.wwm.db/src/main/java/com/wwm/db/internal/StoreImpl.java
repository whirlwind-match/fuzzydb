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
import java.util.Stack;

import org.slf4j.Logger;
import org.springframework.util.Assert;

import com.wwm.db.DataOperations;
import com.wwm.db.Helper;
import com.wwm.db.Ref;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.core.LogFactory;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.exceptions.UnknownTransactionException;
import com.wwm.db.internal.comms.messages.AllocNewIdsCmd;
import com.wwm.db.internal.comms.messages.AllocNewIdsRsp;
import com.wwm.db.internal.comms.messages.BeginAndCommitCmd;
import com.wwm.db.internal.comms.messages.CommitCmd;
import com.wwm.db.marker.ITraceWanted;
import com.wwm.io.core.ArchInStream;
import com.wwm.io.core.ArchOutStream;
import com.wwm.io.core.Authority;
import com.wwm.io.core.Message;
import com.wwm.io.core.messages.Command;
import com.wwm.io.core.messages.Response;

/**
 * Equivalent to a JDBC Connection on which transactions can be managed and operations performed.
 * 
 * @author Adrian Clarkson
 * @author Neale Upstone
 */
public final class StoreImpl extends AbstractDataOperationsProxy implements Store {

	private class StoreImplContext implements Helper {
		private static final int maxIdsToRequest = 1024;
		private final int storeId;
		private final String storeName;
		private final AbstractClient client;
		private final NewObjectIds newIdCache = new NewObjectIds();
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

		public <E> Ref<E> getRef(E obj) throws UnknownObjectException {
			return client.getRef(obj);
		}

		public int getVersion(Object obj) throws UnknownObjectException {
			return client.getVersion(obj);
		}

		@Override
		public String toString() {
			return "Store [storeId=" + storeId + ", storeName="
					+ storeName + ", client=" + client + "]";
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
	
	private final ThreadLocal<Stack<Transaction>> currentTransaction = new ThreadLocal<Stack<Transaction>>();
	
	
	
	
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
	
	@Override
	protected DataOperations getDataOperations() {
		Transaction transaction = currentTransaction();
		if (transaction == null) {
			throw new UnknownTransactionException("Store " + getStoreName() + " does not have an active transaction.");
		}
		return transaction;
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
				log.trace( (receiving ? "<- " : "-> " ) +
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
		TransactionImpl transaction = new TransactionImpl(this);

		if (!allowTxOverlapInThread) {
			addToStack(transaction);
		}
		return transaction;
	}

	private void addToStack(TransactionImpl transaction) {
		// First on this thread
		if (currentTransaction.get() == null) {
			currentTransaction.set(new Stack<Transaction>());
		} else {
			if (!currentTransaction.get().empty()) { 
					// && allowTxOverlapInThread  ) {
				log.warn("Multiple transactions active in one Thread. Store.currentTransaction() will return the most recent uncommitted transaction");
			}
		}
		currentTransaction.get().push(transaction);
	}

	public Transaction currentTransaction() {
		Assert.state(!allowTxOverlapInThread, "cannot use currentTransaction() when overlapped transactions in thread are enabled");
		return currentTransaction.get() == null || currentTransaction.get().empty() 
			? null : currentTransaction.get().peek();
	}
	
	void clearCurrentTransaction(Transaction tx) {
		if (!allowTxOverlapInThread) {
			Assert.state(currentTransaction() == tx, "You attempted to overlap transactions in a thread.  You can nest your own transactions but you must commit()/dispose() the inner transaction first");
			currentTransaction.get().pop();
		}
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

	public boolean isAuthoritative() {
		return authority==Authority.Authoritative;
	}

	public int getVersion(Object obj) throws UnknownObjectException {
		return context.getVersion(obj);
	}

	public final <E> Ref<E> getRef(E object) {
		return context.getRef(object);
	};

	public final StoreImplContext getContext() {
		return context;
	}

	public void disposeTransaction(int tid) {
		context.disposeTransaction(tid);
	}

	public void addToMetaCache(MetaObject<?> mo) {
		context.addToMetaCache(mo);	
	}

	public void troff() {
		// do nothing - backwards compat for Db1 port
	}

	public void tron(Logger log) {
		// do nothing - backwards compat for Db1 port
	}
	
	@Override
	public String toString() {
		return context.toString();
	}
}
