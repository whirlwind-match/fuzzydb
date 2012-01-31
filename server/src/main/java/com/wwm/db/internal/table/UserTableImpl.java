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

import com.wwm.db.exceptions.ObjectExistsException;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.exceptions.WriteCollisionException;
import com.wwm.db.internal.MetaObject;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.common.ServiceRegistry;
import com.wwm.db.internal.server.Namespace;
import com.wwm.db.internal.server.VersionedObject;

/**
 * An impl of UserTable that stores data from MetaObjects into 
 * VersionedObject in a supplied table.
 */
public class UserTableImpl<T> implements Serializable, UserTable<T> {

	public class TableIterator implements Iterator<MetaObject<T>> {

		Iterator<RefObjectPair<T,VersionedObject<T>>> tableIterator = table.iterator();
		public boolean hasNext() {
			return tableIterator.hasNext();
		}

		public MetaObject<T> next() {
			RefObjectPair<T,VersionedObject<T>> refObj = tableIterator.next();
			if (refObj == null){
				return null;
			}
			return new MetaObject<T>(refObj.ref, refObj.obj.getVersion(), refObj.obj.getObject() );
		}

		public void remove() {
			tableIterator.remove();
		}
	}


	private static final long serialVersionUID = 1L;

	private final Table<T, VersionedObject<T>> table;
	
	
	public UserTableImpl(Table<T,VersionedObject<T>> table) {
		this.table = table;
	}


	public void initialise(ServiceRegistry context) {
		table.initialise();
	}


	public void testCanCreate(MetaObject<T> mo) throws ObjectExistsException {
		if (doesElementExist(mo.getRef())) {
			throw new ObjectExistsException();
		}
	}

	public void testCanUpdate(MetaObject<T> mo) throws UnknownObjectException, WriteCollisionException {
		MetaObject<T> previous = getObject(mo.getRef());
		if ( previous.getVersion() != mo.getVersion() 
				|| !canSeeLatest(mo.getRef()) ) {
			throw new WriteCollisionException(); 
		}
	}

	public void testCanDelete(RefImpl<T> ref) throws UnknownObjectException {
		if ( !canSeeLatest(ref) ){
			throw new UnknownObjectException();
		}
	}

	public void create(MetaObject<T> mo) {
		table.create(mo.getRef(), new VersionedObject<T>(mo.getObject()));
	}
	public void update(MetaObject<T> mo) throws UnknownObjectException {
		table.update(mo.getRef(), VersionedObject.nextVersion(mo.getObject(), mo.getVersion()));
	}


	public void delete(RefImpl<T> ref) throws UnknownObjectException {
		table.delete(ref);
	}

	public MetaObject<T> getObject(RefImpl<T> ref) throws UnknownObjectException {
		VersionedObject<T> vo = table.getObject(ref);
		if (vo != null) {
			return new MetaObject<T>(ref, vo.getVersion(), vo.getObject());
		}
		return null;
	}

	
	public boolean doesElementExist(RefImpl<T> ref) {
		return table.doesElementExist(ref);
	}



	/**
	 * Locks the specified elements for write. The calling thread must be a WorkerThread and it must have acquired write
	 * privileges from the Transaction Coordinator. No other elements must be locked for write already. If multiple
	 * elements must be locked, they must all be locked with a single function call.
	 * 
	 * @param elements
	 *            The elements to lock
	 * @return
	 * 
	 * public Map<Long, Element> lockElementsForWrite(Set<Long> elements);
	 */


	public long allocNewIds(int count) {
		return table.allocNewIds(count);
	}

	public int getTableId() {
		return table.getTableId();
	}

	public Namespace getNamespace() {
		return table.getNamespace();
	}

	public Class<T> getStoredClass() {
		return (Class<T>) table.getStoredClass();
	}
	
    private boolean canSeeLatest(RefImpl<T> ref) throws UnknownObjectException {
		return table.canSeeLatest(ref);
	}


	public boolean deletePersistentData() {
		return table.deletePersistentData();
	}


	public Iterator<MetaObject<T>> iterator() {
		return new TableIterator();
	}
	
	public long getElementCount() {
		return table.getElementCount();
	}

}
