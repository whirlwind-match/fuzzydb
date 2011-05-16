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

import java.net.MalformedURLException;

import com.wwm.context.ContextManager;
import com.wwm.db.EmbeddedClientFactory;
import com.wwm.db.Store;

/**
 * FIXME: Look at where this is called from, and adapt it to resolve the
 * store from the environment.
 * In same way as the Db1 based system, we need to be able to access
 * session and request data from any place.
 * Items needed:
 * - dbClient, or DbClientFactory - for DAO for postcode converter
 * -
 * 
 */
public class IndexerFactory {

    private static final String CURRENT_STORE_URL = "CurrentStoreURL";

    // FIXME: Rename to ServiceFactory
    public static String baseFeedUrl = "feed"; // gets added to base somewhere to give fuzz/feed


    /**
     * Get indexer for the current context (i.e. user etc)
     * @return
     */
    static public Indexer getIndexer() {
        return SessionServiceManager.getInstance().getIndexer();
    }


    /**
     * Validate and set the store URL for the current user
     * @param strUrl
     */
    public static void setCurrentStoreUrl(String strUrl) {
        ContextManager.getCurrentSessionContext().set(CURRENT_STORE_URL, strUrl);
    }

    /**
     * Get the store in use for the current user.  This always asks for the store by
     * URL from StoreMgr, therefore allowing a session to have migrated between
     * servers.
     */
    public static Store getCurrentStore() {
        // NOTE: This could just get the store from the Session, but this approach does
        // allow the session to have migrated between servers, and is therefore more
        // robust.
        String url = (String) ContextManager.getCurrentSessionContext().get(CURRENT_STORE_URL);
        assert url != null;
		try {
			return EmbeddedClientFactory.getInstance().openStore(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
    }
}
