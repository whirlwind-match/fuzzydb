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
package com.wwm.indexer;

import java.util.logging.Logger;

import com.wwm.context.ContextManager;
import com.wwm.context.SessionContext;
import com.wwm.context.SessionContextMgrBase;
import com.wwm.db.core.LogFactory;
import com.wwm.indexer.internal.IndexerImpl;

/**
 * Local factory for items needed by this implementation.
 * 
 * FIXME: This needs multi-user/multi-store support
 */
class SessionServiceManager extends SessionContextMgrBase { // FIXME: Rename to ServiceFactory

    private static SessionServiceManager instance;

    static private Indexer indexer;

    static String baseFeedUrl = "feed"; // gets added to base somewhere to give fuzz/feed


    static synchronized public SessionServiceManager getInstance() {
        if (instance == null) {
            instance = new SessionServiceManager( ContextManager.getSession() );
        }

        return instance;
    }

    protected SessionServiceManager(SessionContext session, Logger log) {
        super(session, log);
    }

    public SessionServiceManager(SessionContext session) {
        super(session, LogFactory.getLogger(SessionServiceManager.class));
    }

    /**
     * FIXME: Make this something we store in the session!! (i.e. Indexer implements IShutdown
     * 
     * Get indexer for the current context (i.e. user etc)
     * @return
     */
    public synchronized Indexer getIndexer() {
        if (indexer == null) {
            indexer = getServiceInternal(IndexerImpl.class);
        }
        return indexer;
    }





}
