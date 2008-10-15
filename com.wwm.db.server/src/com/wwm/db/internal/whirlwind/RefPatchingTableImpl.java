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
package com.wwm.db.internal.whirlwind;



import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.table.RawTable;
import com.wwm.db.internal.table.TableImpl;

/**
 * As TableImpl, except it calls object.setRef( ref ) from within getObject() &
 * getObjectNonIO() allow an object to know it's own identity, but without
 * needing to store that in the persistent store.
 *  
 * @author Neale
 */
public class RefPatchingTableImpl<T extends RefAware> extends TableImpl<T,T> implements RefPatchingTable<T> {

	private static final long serialVersionUID = 1L;

	public RefPatchingTableImpl(RawTable<T> table, int tableId) {
		super(table, tableId);
	}

	@Override
	public T getObject(RefImpl ref) throws UnknownObjectException {
		T object = super.getObject(ref);
		if (object != null){
			object.setRef(ref);
		}
		return object;
	}

	@Override
	public T getObjectNonIO(RefImpl ref) throws UnknownObjectException {
		T object = super.getObjectNonIO(ref);
		if (object != null){
			object.setRef(ref);
		}
		return object;
	}
	
	@Override
	public void create(RefImpl ref, T object) {
		super.create(ref, object);
		object.setRef(ref);
		object.setImmutable(); // Tell the object that it can no longer be written to
	}
	
	@Override
	public void update(RefImpl ref, T object) throws UnknownObjectException {
		super.update(ref, object);
		object.setImmutable(); // Tell the object that it can no longer be written to
	}
}
