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
package com.wwm.db.dao;

import org.slf4j.Logger;

import com.wwm.db.Store;
import com.wwm.db.StoreMgr;
import com.wwm.db.Transaction;
import com.wwm.db.core.LogFactory;
import com.wwm.db.exceptions.WriteCollisionException;

/**
 * A DAO that assumes that you're passing it DbObject, as used in DB1.
 * 
 * NOTE: NOT thread safe.  Only use a single DAO instance for a single transaction at a time.
 */
public class Db2ObjectDAO implements SimpleDAO {


    static private Logger log = LogFactory.getLogger(Db2ObjectDAO.class);

    private String storeUrl;
    private Store store;
    private Transaction tx;

    // Default for instantiation from Settings
    public Db2ObjectDAO(){
        storeUrl = "wwmdb:/_DefactoStoreForDb2ObjectDao";
        openStore();
    }

    public Db2ObjectDAO( String storeUrl ) {
        this.storeUrl = storeUrl;
        openStore();
    }



    private void openStore() {
        store = StoreMgr.getInstance().getStore(storeUrl);
        log.info("Opened Store '" + storeUrl + "'");
    }


    public void begin() {
        tx = store.getAuthStore().begin();
    }

    public void commit() throws DaoWriteCollisionException {
        try {
            tx.commit();
        } catch (WriteCollisionException e){
            throw new DaoWriteCollisionException();  // translate to Dao exception
        }
    }

    public Object create(Object object, Object key) {
        assert(key == null); // Key is ignored. If specified...
        return tx.create( object );
    }

    public <T> T retrieve(Class<T> clazz, Object key) {
        return tx.retrieveFirstOf(clazz); // NOTE: ignoring key
    }

    public void update(Object object, Object ref) {
        tx.update( object );
    }

}
