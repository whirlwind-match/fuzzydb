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
import java.util.HashMap;

import com.wwm.db.services.IndexImplementationsService;

public class Namespaces implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final ServerStore store;
	private final HashMap<Integer, Namespace> tableIdToNamespace = new HashMap<Integer, Namespace>();
	private final HashMap<String, Namespace> namespaces = new HashMap<String, Namespace>();
	private transient InitialisationContext context;
	
//	private transient boolean initialised = false; // triggers lazy-init after load from persistent storage.
	
	public Namespaces(ServerStore store) {
		this.store = store;
	}
	
	public synchronized Namespace getNamespaceFromTableId(int tableId) {
//		if (!initialised){ initialise(); }
		
		return tableIdToNamespace.get(tableId);
	}
	
	public synchronized Namespace getNamespace(String name) {
//		if (!initialised){ initialise(); }

		return namespaces.get(name);
	}
	
	public synchronized Namespace createNamespace(String name) {
//		if (!initialised){ initialise(); }

		Namespace namespace = new Namespace(this, name);
		namespaces.put(name, namespace);
		namespace.initialise(context);
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
	
	public synchronized void initialise(InitialisationContext initialisationContext) {
		this.context = initialisationContext;
		for (Namespace namespace : namespaces.values()) {
			namespace.initialise(initialisationContext);
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

	public IndexImplementationsService getIndexImplementationsService() {
		return context.database.getIndexImplementationsService();
	}
}
