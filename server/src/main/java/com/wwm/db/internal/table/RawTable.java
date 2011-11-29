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


import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.internal.pager.Element;
import com.wwm.db.internal.pager.ElementReadOnly;
import com.wwm.db.internal.server.Namespace;

/**
 * An interface that allows the creation, access, modification and deletion of {@link Element}s
 * 
 * @param <T> What gets stored in each Element (e.g. LeafNode or some user class)
 */
public interface RawTable<T> {

    public Namespace getNamespace();
    public int getStoreId();

    /**
     * Get class of objects being stored in this table.
     */
    public Class<?> getStoredClass();

    public void initialise();
    
    /**
     * Delete this table permanently.  e.g. deletes on-disk representation
     */
    public boolean deletePersistentData();

    /**
     * Locks the specified element for read. The caller must be a WorkerThread. The returned element must only be
     * accessed while it is locked.
     * 
     * @param elementId
     *            The element to lock
     * @return The element
     * @throws UnknownObjectException
     */
    public ElementReadOnly<T> lockElementForRead(long elementId) throws UnknownObjectException;

    /**
     * Locks the specified element for write. The calling thread must be a WorkerThread and it must have acquired write
     * privileges from the Transaction Coordinator. No other elements must be locked for write already. If multiple
     * elements must be locked, they must all be locked with a single function call.
     * 
     * @param elementId
     * @return
     * @throws UnknownObjectException
     */
    public Element<T> lockElementForWrite(long elementId) throws UnknownObjectException;

    public void unlockElementForRead(ElementReadOnly<T> element);

    public void unlockElementForWrite(ElementReadOnly<T> element);

    public void createElement(Element<T> element);
    public boolean doesElementExist(long elementId);

    /**
     * reserve space, and return the starting oid of the batch
     * <p>
     * Implementation must be thread-safe
     */
    public long allocNewIds(int count);
    public long allocOneRef();
    public long allocOneRefNear(long nearOid, long[] others);
    public long getNextOid();
}