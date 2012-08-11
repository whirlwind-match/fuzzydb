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
package org.fuzzydb.util.context;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;


/**
 * Object for allowing our apps to data that is in the request scope, including what
 * session and application context is associated with this request.
 *
 */
public class RequestContext {

    protected static Logger log;

    /**
     * ApplicationContext associated with this request context
     */
    ApplicationContext appContext = null;

    /**
     * SessionContext associated with this request context
     */
    SessionContext sessionContext = null;

    /**
     * User request context associated with this request context
     */
    Object userRequestObject;

    /**
     * Map of attributes that we keep just for this request.
     * This is private to the application, but cleared when setUserRequestContext is called
     * (this is because this object is kept around for each request that services requests)
     */
    private TreeMap<String,Object> map = null;

    public RequestContext() {
        super();
    }

    /**
     * Initialise the cached data for this thread.
     * This call passes in the context objects which are associated with the current application and session,
     * and also clears out the internal map.
     * Care is take here, as the object persists from request to request, one instance per thread, when it
     * is being used within a web application server that maintains a pool of request handling threads.
     * @param appContext - the current application context (to be available to the current thread)
     * @param sessionContext - current session context (to be available to the current thread)
     * @param userRequestObject - a request object for the current thread.  This allows
     * data that may not be passed through a framework to be used elsewhere if possible.
     * e.g. HttpServletRequest
     */
    void initForThread(ApplicationContext appContext, SessionContext sessionContext, Object userRequestObject) {
        this.appContext = appContext;
        this.sessionContext = sessionContext;
        this.userRequestObject = userRequestObject;
        this.map = null;
    }


    /**
     * Get object from map in our own request context.  Note, this is not mapped
     * to a framework request context, such as an HttpRequest.  It is internal.
     */
    public static Object get(String key) {
        return ContextManager.getCurrentRequestContext().getMap().get(key);
    }

    private Map<String, Object> getMap() {
        // Lazy init. Don't create if we don't need it.
        if (map == null){
            map = new TreeMap<String,Object>();
        }
        return map;
    }

    /**
     * Put object to map in our own request context.  Note, this is not mapped
     * to a framework request context, such as an HttpRequest.  It is internal.
     */
    public static void put(String key, Object value) {
        ContextManager.getCurrentRequestContext().getMap().put(key, value);
    }

}