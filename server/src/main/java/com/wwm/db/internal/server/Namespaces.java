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
package com.wwm.db.internal.server;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import com.wwm.db.internal.common.InitializingBean;

public class Namespaces implements Serializable, InitializingBean {
	
	private static final long serialVersionUID = 1L;
	private final ServerStore store;
	private final ConcurrentHashMap<Integer, Namespace> tableIdToNamespace = new ConcurrentHashMap<Integer, Namespace>();
	private final ConcurrentHashMap<String, Namespace> namespaces = new ConcurrentHashMap<String, Namespace>();
	
//	private transient boolean initialised = false; // triggers lazy-init after load from persistent storage.
	
	public Namespaces(ServerStore store) {
		this.store = store;
	}
	
	public Namespace getNamespaceFromTableId(int tableId) {
//		if (!initialised){ initialise(); }
		
		return tableIdToNamespace.get(tableId);
	}
	
	public Namespace getNamespace(String name) {
//		if (!initialised){ initialise(); }

		return namespaces.get(name);
	}
	
	public Namespace createNamespace(String name) {
//		if (!initialised){ initialise(); }

		Namespace namespace = new Namespace(this, name);
		namespaces.put(name, namespace);
		namespace.initialise();
		return namespace;
	}
	
	public int getNextTableId(Namespace namespace) {
//		if (!initialised){ initialise(); }

		int tableId = store.nextTableId();
		tableIdToNamespace.put(tableId, namespace);
		return tableId;
	}
	
	public ServerStore getStore() {
		return store;
	}
	
	public synchronized void initialise() {
		for (Namespace namespace : namespaces.values()) {
			namespace.initialise();
		}
	}
	
	public String getPath() {
		return store.getPath();
	}

	public int getStoreId() {
		return store.getStoreId();
	}

	public boolean deletePersistentData() {
		boolean success = true;
		for (Namespace namespace : namespaces.values()) {
			success &= namespace.deletePersistentData();
		}
		return success;
	}

	public String[] getNamespaces() {
		String[] tmp = new String[0];
		return namespaces.keySet().toArray(tmp);
	}
}
