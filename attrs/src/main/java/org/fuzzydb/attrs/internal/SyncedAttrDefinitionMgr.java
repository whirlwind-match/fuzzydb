/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.attrs.internal;

import java.io.Serializable;

import com.wwm.context.ContextManager;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.marker.ITraceWanted;
import com.wwm.util.DynamicRef;

/**
 * Extends AttrDefinitionMgr to allow it to store itself in a Db2 store.<p>
 * 
 * <strike>Not currently true: Attributes are defined against a given Class for which a fuzzy search can be performed.  
 * A separate {@link AttrDefinitionMgr} is stored for each fuzzy class in that store.</strike><p>
 * 
 * NOTE: While we could create a key by which to select the relevant AttrDefinitionMgr for a given
 * fuzzy index, we don't.  We currently store one instance which is shared across the Store.
 * 
 * This could lead to memory sparseness issues in cases where LayoutAttrMap is used.<p>
 */
public class SyncedAttrDefinitionMgr extends AttrDefinitionMgr implements Serializable, ITraceWanted {

    private static final long serialVersionUID = 1L;
    
    private static final Object getInstanceLock = new Object();

    

    /**
     * Transient, as we don't try serialising a store to itself!
     * This should be set after we've created or retrieved from store.
     */
    transient Store store; // For when we write attr defs to database.

    /** Default ctor for serialization libraries */
    private SyncedAttrDefinitionMgr() {
    }

    private SyncedAttrDefinitionMgr(Store store) {
        this.store = store;
    }


    /**
     * Responsible for getting the up to date AttrDefinitionMgr for the supplied store.
     * This method should be used whenever other threads may also be modifying the ADM, otherwise, we
     * may have a version and find that another thread has committed it.
     * @return DynamicRef (a poor version of AtomicReference).  If we've already returned
     * the ADM for this store before, then we always re-use the same (so all threads see the update)
     */
    public static DynamicRef<SyncedAttrDefinitionMgr> getInstance( Store store ) {
    	
    	synchronized (getInstanceLock) {
            String className = SyncedAttrDefinitionMgrs.class.getName();
            SyncedAttrDefinitionMgrs sadms = (SyncedAttrDefinitionMgrs) ContextManager.getCurrentAppContext().get(
                    className);
            if (sadms == null) {
                sadms = new SyncedAttrDefinitionMgrs();
                ContextManager.getCurrentAppContext().set(className, sadms);
            }
            DynamicRef<SyncedAttrDefinitionMgr> w = sadms.get(store);
            if (w.getObject() == null) {
                SyncedAttrDefinitionMgr mgr = getFromStore(store);
                w.setObject(mgr);
            }
            else { // If already have it, refresh it
                Transaction tx = beginTx(store);
                try {
                    SyncedAttrDefinitionMgr mgr = tx.refresh(w.getObject());
                    tx.dispose();
                    mgr.setStore(store);
                    w.setObject(mgr);
                }
                catch (UnknownObjectException e) {
                    w.setObject(getFromStore(store));
                }
            }
            return w;
        }
    }
    

    /**
     * Get <b>first</b> instance of AttrDefinitionMgr from store, or create one in the store
     * if one didn't exist
     * visibility is 'package' as we use this to test that it is getting correctly stored.<p>
     * NOTE: We expect there to be only one and so does the server for now.
     */
    static SyncedAttrDefinitionMgr getFromStore(Store store) {
    	// Only ever want first, and no need for index this way
        Transaction readTx = beginTx(store);
		SyncedAttrDefinitionMgr mgr = readTx
            .retrieveFirstOf( SyncedAttrDefinitionMgr.class );
        readTx.dispose();

        if (mgr == null){
            mgr = new SyncedAttrDefinitionMgr( store );
            Transaction tx = beginTx(store);
            tx.create(mgr);
            tx.commit();
        }
        mgr.setStore( store );
        return mgr;
    }


	private static Transaction beginTx(Store store) {
		return store.getAuthStore().begin();
	}


    private void setStore(Store store) {
        this.store = store;
    }


    @Override
    protected void syncToStoreInternal() {
        Transaction tx = store.getAuthStore().begin();
        tx.update(this);
        tx.commit();
    }
}
