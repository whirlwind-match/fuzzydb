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
package com.wwm.db;


import com.wwm.db.internal.ClientImpl;
import com.wwm.db.internal.RetrieveSpecImpl;
import com.wwm.db.query.RetrieveSpec;
import com.wwm.io.packet.layer1.Authority;



/**
 * FIXME: We could do with looking closely at this.
 * 
 * We need a simplified model of getting a pooled database connection, preferably via a JDBC-style URL.
 * We also need to be able to ditch that and start afresh during testing, etc.
 * The best approach would be to use an application context for managing that, and to pool Client's, and the
 * stores that they connect to.
 * 
 */
public final class Factory {

    /**
     * For easily being able to get at the current transaction.
     */
    static private ThreadLocal<Transaction> currentTransaction = new ThreadLocal<Transaction>();


    /**Creates a new non-authoritative client.
     * @return
     */
    public static Client createClient() {
        return new ClientImpl(Authority.NonAuthoritative);
    }

    public static RetrieveSpec createRetrieveSpec() {
        return new RetrieveSpecImpl();
    }

    public static void setCurrentTransaction( Transaction tx){
        currentTransaction.set(tx);
    }

    /** Get the last transaction created within this Thread */
    public static Transaction getCurrentTransaction(){
        return currentTransaction.get();
    }
}
