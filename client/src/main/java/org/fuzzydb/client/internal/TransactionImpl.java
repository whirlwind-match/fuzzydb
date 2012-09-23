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
package org.fuzzydb.client.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.fuzzydb.client.DataOperations;
import org.fuzzydb.client.IndexDefinition;
import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.client.exceptions.AuthorityException;
import org.fuzzydb.client.exceptions.TransactionDisposedException;
import org.fuzzydb.client.exceptions.UnknownObjectException;
import org.fuzzydb.client.internal.comms.messages.BeginAndCommitCmd;
import org.fuzzydb.client.internal.comms.messages.BeginTransactionCmd;
import org.fuzzydb.client.internal.comms.messages.CommitCmd;
import org.fuzzydb.client.internal.comms.messages.CountClassCmd;
import org.fuzzydb.client.internal.comms.messages.CountClassRsp;
import org.fuzzydb.client.internal.comms.messages.EnsureIndexCmd;
import org.fuzzydb.client.internal.comms.messages.ListNamespacesCmd;
import org.fuzzydb.client.internal.comms.messages.ListNamespacesRsp;
import org.fuzzydb.client.internal.comms.messages.RetrieveByKeyCmd;
import org.fuzzydb.client.internal.comms.messages.RetrieveByRefCmd;
import org.fuzzydb.client.internal.comms.messages.RetrieveByRefsCmd;
import org.fuzzydb.client.internal.comms.messages.RetrieveBySpecCmd;
import org.fuzzydb.client.internal.comms.messages.RetrieveBySpecRsp;
import org.fuzzydb.client.internal.comms.messages.RetrieveFirstOfCmd;
import org.fuzzydb.client.internal.comms.messages.RetrieveMultiRsp;
import org.fuzzydb.client.internal.comms.messages.RetrieveSingleRsp;
import org.fuzzydb.client.marker.IWhirlwindItem;
import org.fuzzydb.client.whirlwind.CardinalAttributeMap;
import org.fuzzydb.core.LogFactory;
import org.fuzzydb.core.exceptions.ArchException;
import org.fuzzydb.core.marker.IAttributeContainer;
import org.fuzzydb.core.query.Result;
import org.fuzzydb.core.query.ResultSet;
import org.fuzzydb.core.query.RetrieveSpec;
import org.fuzzydb.core.query.RetrieveSpecResult;
import org.fuzzydb.core.whirlwind.SearchSpec;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.expressions.LogicExpr;
import org.fuzzydb.io.core.ArchInStream;
import org.fuzzydb.io.core.ArchOutStream;
import org.fuzzydb.io.core.exceptions.CommandTimedOutException;
import org.fuzzydb.io.core.messages.Command;
import org.fuzzydb.io.core.messages.Response;
import org.slf4j.Logger;


public class TransactionImpl implements Transaction {
	
	
	static private final Logger log = LogFactory.getLogger(TransactionImpl.class);

	private HashMap<String, ArrayList<MetaObject<?>>> created;
	private ArrayList<MetaObject<?>> updated;
	private ArrayList<RefImpl<?>> deleted;
	private final StoreImpl store;
	private boolean started = false;
	private boolean disposed = false;
	private final int tid;
	private volatile String namespace;
	private Stack<String> namespaceStack;
	private final int defaultFetchSize = 10;
	
	public TransactionImpl(StoreImpl store) {
		this.store = store;
		this.tid = store.getNextId();
		this.namespace = DataOperations.DEFAULT_NAMESPACE;
		log.debug("New transaction: {} on {}", tid, store);
	}
	
	public ArchInStream newInputStream(byte[] data) throws IOException {
		return store.newInputStream(data);
	}
	
	public ArchOutStream newOutputStream(OutputStream out) throws IOException {
		return store.newOutputStream(out);
	}
	
	/**
	 * 
	 * @throws CommandTimedOutException
	 */
	public Response execute(Command command) {
		if (!started) {
			if (command instanceof CommitCmd) {
				command = new BeginAndCommitCmd(store.getStoreId(), command.getCommandId(), tid, command);
			} else {
				command = new BeginTransactionCmd(store.getStoreId(), command.getCommandId(), tid, command);
			}
			started = true;
		}
		return store.execute(command);
	}
	
	private void addCreated(MetaObject<?> meta) {
		if (created == null) {
			created = new HashMap<String, ArrayList<MetaObject<?>>>(1);
		}
		ArrayList<MetaObject<?>> al = created.get(namespace);
		if (al == null) {
			al = new ArrayList<MetaObject<?>>(1);
			created.put(namespace, al);
		}
		al.add(meta);
		store.addToMetaCache(meta);
	}

	private void addUpdated(MetaObject<?> meta) {
		if (updated == null) {
			updated = new ArrayList<MetaObject<?>>(1);
		}
		updated.add(meta);
		store.addToMetaCache(meta);
	}

	private void addDeleted(Ref ref) {
		if (deleted == null) {
			deleted = new ArrayList<RefImpl<?>>(1);
		}
		deleted.add( (RefImpl<?>)ref );
	}
	
	
	@Override
	public synchronized void commit() {
		requiresAuth();
		requiresActive();
		
		Command cmd = new CommitCmd(store.getStoreId(), store.getNextId(), tid, created, updated, deleted);
		execute (cmd);
		
		// Commit sucessful, update all the version numbers
		if (created != null) {
			for (ArrayList<MetaObject<?>> al : created.values()) {
				for (MetaObject<?> mo : al) {
					mo.setVersion(1);
					store.addToMetaCache(mo);
				}
			}
		}
		
		// Update updated versions
		if (updated != null) {
			for (MetaObject<?> mo : updated) {
				mo.incrementVersion();
				store.addToMetaCache(mo);
			}
		}
		
		log.debug("Committed transaction: {}", tid);

		store.clearCurrentTransaction(this);
	}

	@Override
	public synchronized <E> long count(Class<E> clazz) {
		requiresActive();
		Command cmd = new CountClassCmd(store.getStoreId(), store.getNextId(), tid, namespace, clazz);
		CountClassRsp rsp = (CountClassRsp) execute(cmd);
		return rsp.getCount();
	}

	@Override
	public synchronized <E> Ref<E> create(E obj) {
		requiresActive();
		requiresAuth();
		RefImpl<E> ref = store.getNextRef(namespace, obj);
		MetaObject<E> mo = new MetaObject<E>(ref, 0, obj);
		addCreated(mo);
		return ref;
	}

	@Override
	public synchronized Ref[] create(Object[] objs) {
		requiresActive();
		requiresAuth();
		Ref[] refs = new Ref[objs.length];
		for (int i = 0; i < objs.length; i++) {
			refs[i] = create(objs[i]);
		}
		return refs;
	}

	@Override
	public synchronized Ref[] create(Collection<Object> objs) {
		requiresActive();
		requiresAuth();
		return create(objs.toArray());
	}

	@Override
	public void delete(Object obj) {
		delete(getRef(obj));
	}

	@Override
	public synchronized void delete(Ref ref) {
		requiresActive();
		requiresAuth();
		addDeleted(ref);
	}

	@Override
	public synchronized void delete(Ref[] ref) {
		requiresActive();
		requiresAuth();
		for (int i = 0; i < ref.length; i++) {
			addDeleted(ref[i]);
		}
	}

	@Override
	public synchronized void delete(Iterable<Ref> refs) {
		requiresActive();
		requiresAuth();
		for (Ref ref : refs) {
			addDeleted(ref);
		}
	}

	@Override
	public synchronized void dispose() {
		store.clearCurrentTransaction(this);
		if (disposed) return;
		disposed = true;
		if (started) {
			try {
				store.disposeTransaction(tid);
			} catch (ArchException e) {
				// silently ignore errors, no-one cares about a failed dispose
			}
		}
		log.debug("Disposed (i.e. no commit) transaction: {}", tid);
	}

	@Override
	public synchronized Object execute(String methodName, Ref ref, Object param) {
		requiresActive();
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized String getNamespace() {
		return namespace;
	}

	@Override
	public synchronized long getVersion(Ref ref) {
		return getVersion(retrieve(ref));
	}

	@Override
	public synchronized String[] listNamespaces() {
		requiresActive();
		Command cmd = new ListNamespacesCmd(store.getStoreId(), store.getNextId(), tid);
		ListNamespacesRsp rsp = (ListNamespacesRsp) execute(cmd);

		return rsp.getNamespaces();
	}

	@Override
	public synchronized void modifyAttributes(IWhirlwindItem obj, CardinalAttributeMap<IAttribute> add, Collection<Long> remove) {
		requiresActive();
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void modifyField(Object obj, String field, Object newval) {
		requiresActive();
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void modifyNominee(IAttributeContainer obj, Object nominee) {
		requiresActive();
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void modifyNomineeField(IAttributeContainer obj, String field, Object newval) {
		requiresActive();
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void popNamespace() throws EmptyStackException {
		requiresActive();
		this.namespace = namespaceStack.pop();
	}

	@Override
	public synchronized void pushNamespace(String namespace) {
		requiresActive();
		if (namespaceStack == null) {
			namespaceStack = new Stack<String>();
		}
		namespaceStack.push(this.namespace);
		this.namespace = namespace;
	}

	@Override
	public synchronized <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr) {
		return query(clazz, index, expr, defaultFetchSize);
	}

	@Override
	public synchronized <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr, int fetchSize) {
		requiresActive();
		return new ResultSetImpl<E>(this, clazz, index, expr, fetchSize);
	}

	
	@Override
	public synchronized <E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search) {
		requiresActive();
		return query(resultClazz, search, 10); // TODO: Is 10 a sensible default (was what we had in DBv1)
	}

	@Override
	public synchronized <E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search, int fetchSize) {
		requiresActive();
		return new WWResultSet<E>(resultClazz, store, this, tid, search, fetchSize, false);
	}


	@Override
	public synchronized <E> long queryCount(Class<E> clazz, LogicExpr index, LogicExpr expr) {
		requiresActive();
		throw new UnsupportedOperationException();
	}

	
	@Override
	public synchronized <E> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search) {
		requiresActive();
		return queryNominee(resultClazz, search, 1);
	}

	@Override
	public synchronized <E> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search, int fetchSize) {
		requiresActive();
		return new WWResultSet<E>(resultClazz, store, this, tid, search, fetchSize, true);
	}

	
	@Override
	public synchronized <E> E refresh(E obj) {
		requiresActive();
		// TODO add current version check. (Right now this always fetches the object)
		return retrieve(getRef(obj));
	}

	@Override
	public synchronized <E> E retrieve(Ref<E> ref) {
		requiresActive();
		Command cmd = new RetrieveByRefCmd(store.getStoreId(), store.getNextId(), tid, ref);
		RetrieveSingleRsp rsp = (RetrieveSingleRsp) execute(cmd);
		
		MetaObject<?> mo = (MetaObject<?>)rsp.getCompactedObject();
		
		return (E) receiveObject(mo);
	}

	private <E> E receiveObject(MetaObject<E> mo) {
		if (mo == null) return null;
		store.addToMetaCache(mo);
		return mo.getObject();
	}
	
//	@SuppressWarnings({ "unused", "unchecked" })
//	private <T> T receiveObject(CompactedObject<T> deflated) {
//		T inflated = null;
//		try {
//			do {
//				ArchInStream in = newInputStream(deflated.getData());
//				try {
//					inflated = (T) in.readObject();
//				} catch (ClassNotFoundException e) {
//					String className = e.toString();	// TODO: Needs processing to extract class name
////					store.requestClassData(className);
//					store.waitForClass(className);
//				}
//			} while (inflated == null);
//			
//		} catch (IOException e) {
//			throw new CommsErrorException(e);
//		}
//		
//		MetaObject<T> mo = new MetaObject<T>(deflated.getRef(), deflated.getVersion(), inflated);
//		store.addToMetaCache(mo);
//		return inflated;
//	}

	@Override
	public synchronized <E> Map<Ref<E>, E> retrieve(Collection<Ref<E>> refs) {
		requiresActive();
		RetrieveByRefsCmd<E> cmd = new RetrieveByRefsCmd<E>(store.getStoreId(), store.getNextId(), tid, refs);
		RetrieveMultiRsp rsp = (RetrieveMultiRsp) execute(cmd);
		
		Object[] mos = rsp.getCompactedObjects();
		//Object obj = receiveObject(rsp.getCompactedObject());
		HashMap<Ref<E>, E> result = new HashMap<Ref<E>, E>();
		for (int i = 0; i < mos.length; i++) {
			MetaObject<E> mo = (MetaObject<E>) mos[i];
			Ref<E> ref = mo.getRef();
			E o = receiveObject(mo);
			result.put(ref, o);
		}
		return result;
	}

	@Override
	public synchronized RetrieveSpecResult retrieve(RetrieveSpec spec) {
		requiresActive();
		Command cmd = new RetrieveBySpecCmd(store.getStoreId(), namespace, store.getNextId(), tid, spec);
		RetrieveBySpecRsp rsp = (RetrieveBySpecRsp) execute(cmd);
		RetrieveSpecResultImpl result = rsp.getResult();
		result.addAllToMetaCache(store);
		return result;
	}

	// FIXME: test that this works for clazz, null, null
	@Override
	@SuppressWarnings("unchecked")
	public synchronized <E> E retrieve(Class<E> clazz, String keyfield, Comparable<?> keyval) {
		requiresActive();
		Command cmd = new RetrieveByKeyCmd(store.getStoreId(), namespace, store.getNextId(), tid, clazz, keyval, keyfield);
		RetrieveSingleRsp rsp = (RetrieveSingleRsp) execute(cmd);
		MetaObject<E> mo = (MetaObject<E>)rsp.getCompactedObject();
		return receiveObject(mo);
	}

	@Override
	public synchronized <E> Collection<E> retrieveAll(Class<E> clazz, String keyfield, Comparable<?> keyval) {
		requiresActive();
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void setNamespace(String namespace) {
		requiresActive();
		this.namespace = namespace;
	}

	@Override
	public synchronized <E> Ref<E> save(E obj) {
		requiresAuth();
		requiresActive();
		try {
			RefImpl<E> ref = getRef(obj);
			MetaObject<E> meta = new MetaObject<E>(ref, getVersion(obj), obj);
			addUpdated(meta);
			return ref;
		} catch (UnknownObjectException e) {
			RefImpl<E> ref = store.getNextRef(namespace, obj);
			MetaObject<E> mo = new MetaObject<E>(ref, 0, obj);
			addCreated(mo);
			return ref;
		}
	}
	@Override
	public synchronized <E> void update(E obj) {
		requiresAuth();
		requiresActive();
		MetaObject<E> meta = new MetaObject<E>(getRef(obj), getVersion(obj), obj);
		addUpdated(meta);
	}

	@Override
	public synchronized void update(Object[] objs) {
		requiresAuth();
		requiresActive();
		for (int i = 0; i < objs.length; i++) {
			update(objs[i]);
		}
	}

	@Override
	public synchronized void update(Collection<Object> objs) {
		requiresAuth();
		requiresActive();
		for (Object obj : objs) {
			update(obj);
		}
	}

	private void requiresAuth() throws AuthorityException {
		if (!isAuthoritative()) {
			throw new AuthorityException();
		}
	}

	private void requiresActive() throws TransactionDisposedException {
		if (disposed) {
			throw new TransactionDisposedException();
		}	
	}

	
	@Override
	public synchronized boolean isAuthoritative() {
		return store.isAuthoritative();
	}

	@Override
	public synchronized <T> RefImpl<T> getRef(T obj) throws UnknownObjectException {
		return (RefImpl<T>)store.getRef(obj);
	}

	@Override
	public synchronized int getVersion(Object obj) throws UnknownObjectException {
		return store.getVersion(obj);
	}

	@Override
	public void forceStart() {
		if (!started) {
			store.execute(new BeginTransactionCmd(store.getStoreId(), store.getNextId(), tid, null));
			started = true;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> E retrieveFirstOf(Class<E> clazz) {
		requiresActive();
		
		RetrieveFirstOfCmd cmd = new RetrieveFirstOfCmd(store.getStoreId(), namespace, store.getNextId(), tid, clazz);
		
		RetrieveSingleRsp rsp = (RetrieveSingleRsp) execute(cmd);
		MetaObject<E> mo = (MetaObject<E>)rsp.getCompactedObject();
		if (mo == null) {
			return null;
		}
		return receiveObject(mo);
	}

	@Override
	public StoreImpl getStore() {
		return store;
	}

	public int getTid() {
		return tid;
	}

	@Override
	public void ensureIndex(IndexDefinition def) {
		requiresAuth();
		
		execute(new EnsureIndexCmd(store.getStoreId(), store.getNextId(), tid, namespace, def));
	}
	
}
