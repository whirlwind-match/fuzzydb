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

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.wwm.attrs.WhirlwindConfiguration;
import com.wwm.db.Ref;
import com.wwm.db.core.LogFactory;
import com.wwm.db.exceptions.KeyCollisionException;
import com.wwm.db.exceptions.ObjectExistsException;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.exceptions.WriteCollisionException;
import com.wwm.db.internal.MetaObject;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.common.InitializingBean;
import com.wwm.db.internal.common.MetaObjectSource;
import com.wwm.db.internal.index.IndexedTable;
import com.wwm.db.internal.search.Search;
import com.wwm.db.internal.table.TableFactory;
import com.wwm.db.internal.table.UserTable;
import com.wwm.db.internal.whirlwind.EmptySearch;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.AttributeCache;


public class Namespace implements Serializable, MetaObjectSource, InitializingBean {
	private static final long serialVersionUID = 1L;

	/** ThreadLocal namespace so we can set/get currentNamespace */
	static private ThreadLocal<Namespace> currentNamespace = new ThreadLocal<Namespace>();

	private static final Logger log = LogFactory.getLogger(Namespace.class);

	private final static int defaultObjectsPerPage = 100;

	private final String name;
	private final String diskName;
	private final Namespaces namespaces;
	private final Indexes indexes = new Indexes(this); // FIXME: Move to IndexManagerImpl - we associate indices with a type

	private final ConcurrentHashMap<String, UserTable<?>> nameToTableMap = new ConcurrentHashMap<String, UserTable<?>>();
	private final ConcurrentHashMap<Integer, UserTable<?>> idToTableMap = new ConcurrentHashMap<Integer, UserTable<?>>();

	private final AttributeCache attributeCache = new AttributeCache();

	public Namespace(Namespaces namespaces, String name) {
		this.name = name;
		this.namespaces = namespaces;

		String dn = null;

		if (namespaces.getPath() != null) {
			File dir = null;
			do {
				dn = FileUtil.makeUniqueDiskName(new File(namespaces.getPath()), name.equals("") ? "_default" : name);
				dir = new File(namespaces.getPath(), dn);
			} while (!dir.mkdirs());
		}

		this.diskName = dn;
	}


	//===========================================================
	// Write, pre-commit phase methods
	//===========================================================
	public <T> void testCanCreate(MetaObject<T> mo) throws ObjectExistsException, KeyCollisionException {
		RefImpl<T> ref = mo.getRef();
		getTable(ref).testCanCreate(mo);
		indexes.testCanAdd(mo);
	}

	public <T> void testCanUpdate(MetaObject<T> mo) throws UnknownObjectException, WriteCollisionException, KeyCollisionException {
		RefImpl<T> ref = mo.getRef();
		getTable(ref).testCanUpdate(mo);
	}

	public <T> void testCanDelete(RefImpl<T> ref) throws UnknownObjectException {
		getTable(ref).testCanDelete(ref);
	}

	//===========================================================
	// Write, commit phase methods
	//===========================================================
	public <T> void create(MetaObject<T> mo) {
		RefImpl<T> ref = mo.getRef();
		getTable(ref).create(mo);
		indexes.add(mo);
	}

	public <T> void update(MetaObject<T> mo) throws UnknownObjectException {
		UserTable<T> table = getTable(mo.getRef());
		MetaObject<T> old = table.getObject(mo.getRef());
		indexes.remove(old);	// This can be optimised for Reference indexes if the key value does not change
		indexes.add(mo);
		table.update(mo);
	}

	public <T> void delete(RefImpl<T> ref) throws UnknownObjectException {
		UserTable<T> table = getTable(ref);
		MetaObject<T> old = table.getObject(ref);
		indexes.remove(old);
		table.delete(ref);
	}


	//===========================================================
	// Read methods
	//===========================================================
	@Override
	public <T> MetaObject<T> getObject(Ref<T> ref) throws UnknownObjectException {
		RefImpl<T> refImpl = (RefImpl<T>) ref; // TODO: Push down removal of use of RefImpl to only those areas it is created
		UserTable<T> table = getTable(refImpl);
		return table.getObject(refImpl);
	}

	//=======================================================================


	@SuppressWarnings("unchecked")
	public <T> UserTable<T> getTable(RefImpl<T> ref) {
		int tableId = ref.getTable();
		return (UserTable<T>) idToTableMap.get(tableId);
	}

	@SuppressWarnings("unchecked")
	public <E> UserTable<E> getTable(Class<E> clazz) {
		String className = clazz.getName();
		return (UserTable<E>) nameToTableMap.get(className);
	}

	@SuppressWarnings("unchecked")
	public <E> UserTable<E> getCreateTable(Class<E> clazz) {
		String className = clazz.getName();
		UserTable<E> table = (UserTable<E>) nameToTableMap.get(className);
		if (table == null) {
			return createPageTable(clazz, defaultObjectsPerPage);
		}
		return table;
	}

	private synchronized <E> UserTable<E> createPageTable(Class<E> clazz, int objectsPerPage) {
		int id = namespaces.getNextTableId(this);
		UserTable<E> table = TableFactory.createPagedUserTable(this, clazz, id);
		//		Table table = TableFactory.createTable( this, clazz, id );
		String className = clazz.getName();
		nameToTableMap.put(className, table);
		idToTableMap.put(id, table);
		table.initialise();
		indexes.createIndexes(clazz);
		return table;
	}

	public String getPath() {
		String path = namespaces.getPath() + File.separatorChar + diskName;
		return path;
	}

	@Override
	public void initialise() {
		for (UserTable<?> table : idToTableMap.values()) {
			table.initialise();
		}
	}

	public int getStoreId() {
		return namespaces.getStoreId();
	}

	public Namespaces getNamespaces() {
		return namespaces;
	}

	/**
	 * Permanently delete this namespace and all associated objects, such as Tables.
	 * @return true if succeeded
	 */
	public boolean deletePersistentData() {
		boolean success = true;
		for (UserTable<?> table : idToTableMap.values()) {
			success &= table.deletePersistentData();
		}

		if (!success) {
			return false;
		}

		File namespaceFile = new File(getPath());
		if (!namespaceFile.exists()) {
			return true;
		}

		if (namespaceFile.listFiles().length != 0) {
			return false;
		}
		return namespaceFile.delete();
	}

	public String getName() {
		return name;
	}


	/**
	 * Cache of all Cacheable attributes for this namespace
	 * NOTE: Need to review.  This might need to be associated with repository.
	 * @return
	 */
	public AttributeCache getAttributeCache() {
		return attributeCache;
	}



	public WhirlwindConfiguration retrieve(Class<WhirlwindConfiguration> clazz, String field, String key) {
		return null;
	}


	/**
	 * Perform search specified by supplied specification
	 * @param searchSpec
	 * @param wantNominee
	 * @return Search object.  Search instance.  Not null.
	 */
	@SuppressWarnings("unchecked") // for (IndexedTable) up-cast.
	public Search search(SearchSpec searchSpec, boolean wantNominee) {
		Class<?> clazz = searchSpec.getClazz();
		IndexedTable<?> table = (IndexedTable) getTable( clazz );
		if (table == null) {
			return EmptySearch.getInstance(); // Internal to server.
		}
		return table.getSearch( searchSpec, wantNominee );
	}


	/**
	 * @param clazz
	 * @return
	 * @throws UnknownObjectException
	 * 		if can't find a table for any objects of this class.
	 */
	public <T> Iterator<MetaObject<T>> retrieveAll( Class<T> clazz ) throws UnknownObjectException {
		UserTable<T> table = getTable(clazz );
		if (table == null) {
			throw new UnknownObjectException();
		}
		return table.iterator();
	}


	public Logger getLog() {
		return log;
	}

	@Override
	public String toString() {
		return name.equals("") ? "_default" : name;
	}

	Indexes getIndexes() {
		return indexes;
	}

	/**
	 * Get the current namespace for the active thread
	 * @return
	 */
	public static MetaObjectSource getCurrentNamespace() {
		return currentNamespace.get();
	}

	/**
	 * Set the namespace associated with the current thread
	 * @param namespace
	 */
	public static void setCurrentNamespace(Namespace namespace) {
		currentNamespace.set(namespace);
	}
}
