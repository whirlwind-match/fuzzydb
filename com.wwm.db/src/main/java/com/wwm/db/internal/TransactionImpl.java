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
import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.wwm.db.Factory;
import com.wwm.db.GenericRef;
import com.wwm.db.Ref;
import com.wwm.db.Transaction;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.exceptions.AuthorityException;
import com.wwm.db.exceptions.TransactionDisposedException;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.internal.comms.messages.BeginAndCommitCmd;
import com.wwm.db.internal.comms.messages.BeginTransactionCmd;
import com.wwm.db.internal.comms.messages.CommitCmd;
import com.wwm.db.internal.comms.messages.CountClassCmd;
import com.wwm.db.internal.comms.messages.CountClassRsp;
import com.wwm.db.internal.comms.messages.ListNamespacesCmd;
import com.wwm.db.internal.comms.messages.ListNamespacesRsp;
import com.wwm.db.internal.comms.messages.RetrieveByKeyCmd;
import com.wwm.db.internal.comms.messages.RetrieveByRefCmd;
import com.wwm.db.internal.comms.messages.RetrieveByRefsCmd;
import com.wwm.db.internal.comms.messages.RetrieveBySpecCmd;
import com.wwm.db.internal.comms.messages.RetrieveBySpecRsp;
import com.wwm.db.internal.comms.messages.RetrieveFirstOfCmd;
import com.wwm.db.internal.comms.messages.RetrieveMultiRsp;
import com.wwm.db.internal.comms.messages.RetrieveSingleRsp;
import com.wwm.db.marker.IAttributeContainer;
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
import com.wwm.io.core.messages.Command;
import com.wwm.io.core.messages.Response;

public class TransactionImpl implements Transaction {

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
	
	public TransactionImpl(StoreImpl store, String namespace) {
		this.store = store;
		this.tid = store.getNextId();
		this.namespace = namespace;
		Factory.setCurrentTransaction(this);
	}
	
	public ArchInStream newInputStream(byte[] data) throws IOException {
		return store.newInputStream(data);
	}
	
	public ArchOutStream newOutputStream(OutputStream out) throws IOException {
		return store.newOutputStream(out);
	}
	
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
		
		store.clearCurrentTransaction();
	}

	public synchronized <E> long count(Class<E> clazz) {
		requiresActive();
		Command cmd = new CountClassCmd(store.getStoreId(), store.getNextId(), tid, namespace, clazz);
		CountClassRsp rsp = (CountClassRsp) execute(cmd);
		return rsp.getCount();
	}

	public synchronized <E> Ref create(E obj) {
		requiresActive();
		requiresAuth();
		RefImpl<E> ref = store.getNextRef(namespace, obj);
		MetaObject<E> mo = new MetaObject<E>(ref, 0, obj);
		addCreated(mo);
		return ref;
	}

	public synchronized Ref[] create(Object[] objs) {
		requiresActive();
		requiresAuth();
		Ref[] refs = new Ref[objs.length];
		for (int i = 0; i < objs.length; i++) {
			refs[i] = create(objs[i]);
		}
		return refs;
	}

	public synchronized Ref[] create(Collection<Object> objs) {
		requiresActive();
		requiresAuth();
		return create(objs.toArray());
	}

	public void delete(Object obj) {
		delete(getRef(obj));
	}

	public synchronized void delete(Ref ref) {
		requiresActive();
		requiresAuth();
		addDeleted(ref);
	}

	public synchronized void delete(Ref[] ref) {
		requiresActive();
		requiresAuth();
		for (int i = 0; i < ref.length; i++) {
			addDeleted(ref[i]);
		}
	}

	public synchronized void delete(Collection<Ref> refs) {
		requiresActive();
		requiresAuth();
		for (Ref ref : refs) {
			addDeleted(ref);
		}
	}

	public synchronized void dispose() {
		store.clearCurrentTransaction();
		if (disposed) return;
		disposed = true;
		if (started) {
			try {
				store.disposeTransaction(tid);
			} catch (ArchException e) {
				// silently ignore errors, no-one cares about a failed dispose
			}
		}
	}

	public synchronized Object execute(String methodName, Ref ref, Object param) {
		requiresActive();
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public synchronized String getNamespace() {
		return namespace;
	}

	public synchronized long getVersion(Ref ref) {
		return getVersion(retrieve(ref));
	}

	public synchronized String[] listNamespaces() {
		requiresActive();
		Command cmd = new ListNamespacesCmd(store.getStoreId(), store.getNextId(), tid);
		ListNamespacesRsp rsp = (ListNamespacesRsp) execute(cmd);

		return rsp.getNamespaces();
	}

	public synchronized void modifyAttributes(IWhirlwindItem obj, CardinalAttributeMap<IAttribute> add, Collection<Long> remove) {
		requiresActive();
		throw new UnsupportedOperationException();
	}

	public synchronized void modifyField(Object obj, String field, Object newval) {
		requiresActive();
		throw new UnsupportedOperationException();
	}

	public synchronized void modifyNominee(IAttributeContainer obj, Object nominee) {
		requiresActive();
		throw new UnsupportedOperationException();
	}

	public synchronized void modifyNomineeField(IAttributeContainer obj, String field, Object newval) {
		requiresActive();
		throw new UnsupportedOperationException();
	}

	public synchronized void popNamespace() throws EmptyStackException {
		requiresActive();
		this.namespace = namespaceStack.pop();
	}

	public synchronized void pushNamespace(String namespace) {
		requiresActive();
		if (namespaceStack == null) {
			namespaceStack = new Stack<String>();
		}
		namespaceStack.push(this.namespace);
		this.namespace = namespace;
	}

	public synchronized <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr) {
		return query(clazz, index, expr, defaultFetchSize);
	}

	public synchronized <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr, int fetchSize) {
		requiresActive();
		return new ResultSetImpl<E>(this, clazz, index, expr, fetchSize);
	}

	
	public synchronized <E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search) {
		requiresActive();
		return query(resultClazz, search, 10); // TODO: Is 10 a sensible default (was what we had in DBv1)
	}

	public synchronized <E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search, int fetchSize) {
		requiresActive();
		return new WWResultSet<E>(resultClazz, store, this, tid, search, fetchSize, false);
	}


	public synchronized <E> long queryCount(Class<E> clazz, LogicExpr index, LogicExpr expr) {
		requiresActive();
		throw new UnsupportedOperationException();
	}

	
	public synchronized <E> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search) {
		requiresActive();
		return queryNominee(resultClazz, search, 1);
	}

	public synchronized <E> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search, int fetchSize) {
		requiresActive();
		return new WWResultSet<E>(resultClazz, store, this, tid, search, fetchSize, true);
	}

	
	public synchronized <E> E refresh(E obj) {
		requiresActive();
		// TODO add current version check. (Right now this always fetches the object)
		return retrieve(getRef(obj));
	}

	public synchronized Object retrieve(Ref ref) {
		requiresActive();
		Command cmd = new RetrieveByRefCmd(store.getStoreId(), store.getNextId(), tid, ref);
		RetrieveSingleRsp rsp = (RetrieveSingleRsp) execute(cmd);
		
		MetaObject<?> mo = (MetaObject<?>)rsp.getCompactedObject();
		
		return receiveObject(mo);
	}

	private Object receiveObject(MetaObject<?> mo) {
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

	public synchronized Map<Ref, Object> retrieve(Collection<Ref> refs) {
		requiresActive();
		Command cmd = new RetrieveByRefsCmd(store.getStoreId(), store.getNextId(), tid, refs);
		RetrieveMultiRsp rsp = (RetrieveMultiRsp) execute(cmd);
		
		Object[] mos = rsp.getCompactedObjects();
		//Object obj = receiveObject(rsp.getCompactedObject());
		HashMap<Ref, Object> result = new HashMap<Ref, Object>();
		for (int i = 0; i < mos.length; i++) {
			MetaObject<?> mo = (MetaObject<?>)mos[i];
			Ref ref = mo.getRef();
			Object o = receiveObject(mo);
			result.put(ref, o);
		}
		return result;
	}

	public synchronized RetrieveSpecResult retrieve(RetrieveSpec spec) {
		requiresActive();
		Command cmd = new RetrieveBySpecCmd(store.getStoreId(), namespace, store.getNextId(), tid, spec);
		RetrieveBySpecRsp rsp = (RetrieveBySpecRsp) execute(cmd);
		RetrieveSpecResultImpl result = rsp.getResult();
		result.addAllToMetaCache(store);
		return result;
	}

	// FIXME: test that this works for clazz, null, null
	@SuppressWarnings("unchecked")
	public synchronized <E> E retrieve(Class<E> clazz, String keyfield, Comparable<?> keyval) {
		requiresActive();
		Command cmd = new RetrieveByKeyCmd(store.getStoreId(), namespace, store.getNextId(), tid, clazz, keyval, keyfield);
		RetrieveSingleRsp rsp = (RetrieveSingleRsp) execute(cmd);
		MetaObject mo = (MetaObject)rsp.getCompactedObject();
		return (E)receiveObject(mo);
	}

	public synchronized <E> Collection<E> retrieveAll(Class<E> clazz, String keyfield, Comparable<?> keyval) {
		requiresActive();
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public synchronized void setNamespace(String namespace) {
		requiresActive();
		this.namespace = namespace;
	}

	public synchronized <E> void update(E obj) {
		requiresAuth();
		requiresActive();
		MetaObject<E> meta = new MetaObject<E>(getRef(obj), getVersion(obj), obj);
		addUpdated(meta);
	}

	public synchronized void update(Object[] objs) {
		requiresAuth();
		requiresActive();
		for (int i = 0; i < objs.length; i++) {
			update(objs[i]);
		}
	}

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

	
	public synchronized boolean isAuthoritative() {
		return store.isAuthoritative();
	}

	@SuppressWarnings("unchecked") 
	public synchronized <T> RefImpl<T> getRef(T obj) throws UnknownObjectException {
		return (RefImpl<T>)store.getRef(obj);
	}

	public synchronized <E> GenericRef<E> getGenericRef(E obj)  throws UnknownObjectException {
		return new GenericRefImpl<E>(getRef(obj));
	}
	
	public synchronized int getVersion(Object obj) throws UnknownObjectException {
		return store.getVersion(obj);
	}

	public void forceStart() {
		if (!started) {
			store.execute(new BeginTransactionCmd(store.getStoreId(), store.getNextId(), tid, null));
			started = true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <E> E retrieveFirstOf(Class<E> clazz) {
		requiresActive();
		
		RetrieveFirstOfCmd cmd = new RetrieveFirstOfCmd(store.getStoreId(), namespace, store.getNextId(), tid, clazz);
		
		RetrieveSingleRsp rsp = (RetrieveSingleRsp) execute(cmd);
		MetaObject mo = (MetaObject)rsp.getCompactedObject();
		if (mo == null) {
			return null;
		}
		return (E)receiveObject(mo);
	}

	public <E> GenericRef<E> createGeneric(E obj) {
		return new GenericRefImpl<E>(create(obj));
	}

	@SuppressWarnings("unchecked")
	public <E> E retrieve(GenericRef<E> ref) {
		return (E) retrieve((Ref)ref);
	}

	public StoreImpl getStore() {
		return store;
	}

	public int getTid() {
		return tid;
	}

}
