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
package com.wwm.attrs.internal;

import java.io.Serializable;

import com.wwm.context.ContextManager;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.marker.ITraceWanted;
import com.wwm.util.DynamicRef;

/**
 * Extends AttrDefinitionMgr to allow it to store itself in a Db2 store.
 * 
 * Attributes are defined against a given Class for which a fuzzy search can be performed.  
 * A separate {@link AttrDefinitionMgr} is stored for each fuzzy class in that store.
 * 
 * TODO: This currently looks like it was written on a Friday afternoon, as we should be storing
 * a single instance per store which contains a map of the definitions for each class.
 * i.e. REWRITE BELOW TO STORE A MAP KEYED ON CLASS, NOT KEYED ON STORE
 */
public class SyncedAttrDefinitionMgr extends AttrDefinitionMgr implements Serializable, ITraceWanted {

    private static final long serialVersionUID = 1L;

    

    /**
     * Transient, as we don't try serialising a store to itself!
     * This should be set after we've created or retrieved from store.
     */
    transient Store store; // For when we write attr defs to database.

    private SyncedAttrDefinitionMgr(Store store) {
        this.store = store;
    }


    /**
     * Responsible for getting the up to date AttrDefinitionMgr for the supplied store.
     * This method should be used whenever other threads may also be modifying the ADM, otherwise, we
     * may have a version and find that another thread has committed it.
     * @return
     */
    public static synchronized DynamicRef<SyncedAttrDefinitionMgr> getInstance( Store store ) {
    	
    	String className = SyncedAttrDefinitionMgrs.class.getName();
		SyncedAttrDefinitionMgrs sadms = (SyncedAttrDefinitionMgrs) ContextManager.getCurrentAppContext().get(className);
    	
    	if (sadms == null) {
    		sadms = new SyncedAttrDefinitionMgrs();
    		ContextManager.getCurrentAppContext().set(className, sadms);
    	}
    	
        DynamicRef<SyncedAttrDefinitionMgr> w = sadms.get( store );
		if (w.getObject() == null) {
			SyncedAttrDefinitionMgr mgr = getFromStore( store );
            w.setObject(mgr);
        }
        else { // If already have it, refresh it
            Transaction tx = store.getAuthStore().begin();
            try {
            	SyncedAttrDefinitionMgr mgr = tx.refresh( w.getObject() );
                mgr.setStore( store );
                w.setObject(mgr);
            } catch (UnknownObjectException e) {
                w.setObject( getFromStore( store ) );
            }
        }
        return w;
    }
    

    /**
     * Get instance of AttrDefinitionMgr from store, or create one in the store
     * if one didn't exist
     * visibility is 'package' as we use this to test that it is getting correctly stored
     */
    static SyncedAttrDefinitionMgr getFromStore(Store store) {
    	// Only ever want first, and no need for index this way
        SyncedAttrDefinitionMgr mgr = store.getAuthStore().begin()
        	.retrieveFirstOf( SyncedAttrDefinitionMgr.class ); 

        if (mgr == null){
            mgr = new SyncedAttrDefinitionMgr( store );
            Transaction tx = store.getAuthStore().begin();
            tx.create(mgr);
            tx.commit();
        }
        mgr.setStore( store );
        return mgr;
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
