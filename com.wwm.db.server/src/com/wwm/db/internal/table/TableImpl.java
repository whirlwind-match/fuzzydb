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
package com.wwm.db.internal.table;

import java.io.Serializable;
import java.util.Iterator;

import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.pager.Element;
import com.wwm.db.internal.pager.ElementReadOnly;
import com.wwm.db.internal.server.Database;
import com.wwm.db.internal.server.Namespace;

/**
 * Implementation of Table that stores objects into locked Elements in a RawTable
 *
 * @param <RT>
 * @param <T>
 */
public class TableImpl<RT,T> implements Serializable, Table<RT,T> {

	private class TableIterator implements Iterator<RefObjectPair<RT,T>> {

		private long currentOid = 0;
		private long lastOid = table.getNextOid() - 1;
		
		public boolean hasNext() {
			// Find next object that exists
			while( currentOid <= lastOid ){
				try {
					if ( canSeeLatestByOid(currentOid) ){
						return true;
					}
				} catch (UnknownObjectException e) {
					// This happens if this transaction allocated the oids but hasn't 
					// written the Element yet, or never will.  Creates on same transaction would be
					// good example.
					return false; 					
				}
				currentOid++;
			}
			return false;
		}

		public RefObjectPair<RT,T> next() {
			// use hasNext() to move us to next object (if it exists)
			if (!hasNext()){
				return null;  // no obj so return null
			}
			try {
				T object = getObjectByOid(currentOid);
				assert( object != null); // hasNext() made sure of this
				RefImpl<RT> ref = new RefImpl<RT>(getSlice(), getTableId(), currentOid);
				RefObjectPair<RT,T> result = new RefObjectPair<RT,T>( ref, object );
				currentOid++;
				return result;
			} catch (UnknownObjectException e) {
				throw new Error("Failed to get object we just checked exists.");
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	
	private static final long serialVersionUID = 1L;

	private final RawTable<T> table;
	private final int tableId;

	private transient boolean initialised = false; // Lazy initialisation: always reset to false when read back from persistent store. 

	private long elementCount = 0;

	
	public TableImpl(RawTable<T> table, int tableId) {
		this.table = table;
		this.tableId = tableId;
	}
	
	private synchronized void incrementCount() {
		elementCount++;
	}

	private synchronized void decrementCount() {
		elementCount--;
	}

	public synchronized long getElementCount() {
		return elementCount;
	}
	
	public synchronized long allocNewIds(int count) {
		return table.allocNewIds(count);
	}

	public synchronized RefImpl<RT> allocOneRef() {
		long oid = table.allocOneRef();
		return new RefImpl<RT>(getSlice(), tableId, oid);
	}

	public synchronized RefImpl<RT> allocOneRefNear(RefImpl<RT> nearRef, long[] others) {
		long oid = table.allocOneRefNear(nearRef.getOid(), others);
		return new RefImpl<RT>(getSlice(), tableId, oid);
	}
	
	public void create(RefImpl<RT> ref, T object) {
		if (!initialised){ initialise(); }
		long oid = ref.getOid();
		assert( ref.getTable() == tableId);
		assert(oid < table.getNextOid()); // pick up silly bugs, such as passing wrong Ref.
		Element<T> element = new Element<T>(oid, object);
		table.createElement(element);
		incrementCount();
	}

	public void delete(RefImpl<RT> ref) throws UnknownObjectException {
		if (!initialised){ initialise(); }
		assert( ref.getTable() == tableId);
		long oid = ref.getOid();
		Element<T> element = table.lockElementForWrite(oid);
		element.delete();
		table.unlockElementForWrite(element);
		decrementCount();
	}

	public boolean deletePersistentData() {
		if (!initialised){ initialise(); }
		return table.deletePersistentData();
	}

	public T getObject(RefImpl<RT> ref) throws UnknownObjectException {
		if (!initialised){ initialise(); }
		assert( ref.getTable() == tableId);
		long oid = ref.getOid();
		return getObjectByOid(oid);
	}

	@SuppressWarnings("unchecked")
	protected T getObjectByOid(long oid) throws UnknownObjectException {
		Object object = null;
		ElementReadOnly element = table.lockElementForRead(oid);
		try {
			object = element.getVersion();
		} finally {
			table.unlockElementForRead(element);
		}
		return (T)object;
	}

	public int getTableId() {
		return tableId;
	}

	public Namespace getNamespace() {
		return table.getNamespace();
	}

	public Class<?> getStoredClass() {
		return table.getStoredClass();
	}
	
	public void initialise() {
		table.initialise();
		initialised = true;
	}

	public void update(RefImpl<RT> ref, T object) throws UnknownObjectException {
		if (!initialised){ initialise(); }
		assert( ref.getTable() == tableId);
		Element<T> element = table.lockElementForWrite(ref.getOid());
		assert(!element.isDeleted());
		element.addVersion( object );
		table.unlockElementForWrite(element);
	}

	public void createUpdate(RefImpl<RT> ref, T object) throws UnknownObjectException {
		if (doesElementExist(ref)) {
			update(ref, object);
		} else {
			create(ref, object);
		}
	}
	
	public boolean doesElementExist(RefImpl<RT> ref) {
		if (!initialised){ initialise(); }
		assert( ref.getTable() == tableId);
		long elementId = ref.getOid();
		return table.doesElementExist(elementId);
	}

	public boolean canSeeLatest(RefImpl<RT> ref) throws UnknownObjectException {
		if (!initialised){ initialise(); }
		assert( ref.getTable() == tableId);
		long oid = ref.getOid();
		boolean result = canSeeLatestByOid(oid);
		return result;

	}

	protected boolean canSeeLatestByOid(long oid) throws UnknownObjectException {
		ElementReadOnly<T> element = table.lockElementForRead(oid);
		boolean result = element.canSeeLatest();
		table.unlockElementForRead(element);
		return result;
	}
	
	public Iterator<RefObjectPair<RT,T>> iterator() {
		if (!initialised){ initialise(); }
		return new TableIterator();
	}

	public T getObjectNonIO(RefImpl<RT> ref) throws UnknownObjectException {
		if (!initialised){ initialise(); }
		assert( ref.getTable() == tableId);
		// FIXME make this method do what its supposed to!
		return getObject(ref);
	}

	protected int getSlice() {
		int slice = Database.getSliceId();
		return slice;
	}

	public RefImpl<RT> allocOneRefNear(RefImpl<RT> nearRef) {
		return allocOneRefNear(nearRef, null);
	}

}
