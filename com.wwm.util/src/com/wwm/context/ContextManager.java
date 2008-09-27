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
package com.wwm.context;

import java.util.logging.Logger;

/**
 * Provides access to Request, Session and Application contexts
 */
public class ContextManager {

    protected static Logger log;


    /**
     * Non-null if all threads should see the same request context,
     * otherwise requestContextForThread is used to get/set one
     * per thread.
     */
    private static RequestContext globalRequestContext = null;

    /**
     * The request context when operating for each thread
     */
    private static ThreadLocal<RequestContext> requestContextForThread = new ThreadLocal<RequestContext>();


    /**
     * Normally, request context is isolated between threads, but in a Java GUI app
     * we want to initialise at startup, and then have all threads in the VM
     * see one ApplicationContext and ThreadContext.
     */
    public static void setSingleForAllThreads() {
        if (globalRequestContext != null) {
            return;
        }
        globalRequestContext = new RequestContext();
    }

    /**
     * See if there is a current session available
     * @return true if there is a session, false if not
     */
    public static boolean hasSession(){
        return getSession() != null;
    }

    /**
     * For internal use, as we want to make RequestContext.getCurrentSessionContext() package scope
     * @return
     */
    public static SessionContext getSession() {
        return getCurrentSessionContext();
    }

    /**
     * get the user object that has been associated with the current request (usually a HttpServletRequest)
     * @return
     */
    public static Object getUserRequestContext() {
        return getCurrentRequestContext().userRequestObject;
    }

    /**
     * Set a request object for the current thread.  This allows
     * data that may not be passed through a framework to be
     * used elsewhere if possible.
     * In a web environment, this should be ServletRequestWrapper( HttpServletRequest )
     * TODO: Make a nice wrapper supporting get/set to save having to cast the result everywhere.
     * @param user
     */
    public static void setUserRequestObject(Object context) {
        getCurrentRequestContext().userRequestObject = context;
    }

    /**
     * Get the current application context (i.e. for the current thread)
     * @return
     */
    public static SessionContext getCurrentSessionContext() {
        return getCurrentRequestContext().sessionContext;
    }

    /**
     * Get the current application context (i.e. for the current thread)
     * @return
     */
    public static ApplicationContext getCurrentAppContext() {
        return getCurrentRequestContext().appContext;
    }

    /**
     * Return whichever is the valid request context.
     * @return
     */
    public static RequestContext getCurrentRequestContext() {
        if (globalRequestContext != null) {
            return globalRequestContext;
        }

        RequestContext threadContext = requestContextForThread.get();

        if ( threadContext == null ) {
            threadContext = new RequestContext();
            requestContextForThread.set( threadContext );
        }
        return threadContext;
    }

    /**
     * Registers a session context for the current thread/request.
     * 
     * (Note: This is registered with the request context, which is often associated
     * with a single thread, but is not necessarily.  Registering the session with the
     * request means that if multiple threads are processing a single request, then
     * the new session will become visible to others).
     * TODO: Review as to whether any atomic behaviour is needed in creation of a new
     * session.
     */
    public static void addSession(SessionContext session) {
        RequestContext currentRequestContext = ContextManager.getCurrentRequestContext();
        assert(currentRequestContext.sessionContext == null);
        currentRequestContext.sessionContext = session;
    }

    public static void initForThread(ApplicationContext applicationContext, SessionContext sessionContext, Object request) {
        getCurrentRequestContext().initForThread(applicationContext, sessionContext, request);
    }


}
