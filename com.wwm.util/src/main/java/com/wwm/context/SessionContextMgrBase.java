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

import org.slf4j.Logger;



/**
 * Base class that is extended by a local session service manager to expose it's own services.
 * 
 * FIXME: This is still a bit of a mess, as getServiceInternal() could be used as a static public, directly from
 * ServiceFactory, but is, at least used sanely, if not obviously.
 * The problem is that if ServiceFactory were to be able to call it directly, getServiceInternal would then
 * be exposed outside of the package.  The existing pattern does provide more safety, so we prob ought to
 * stick with it and review more broadly (see below).
 * 
 * TODO: Review where we are vs using Spring Beans to manage instances.  When we created our original pattern,
 * Spring Beans didn't support sessions.
 * 
 */
public abstract class SessionContextMgrBase {

    protected Logger log;


    /**
     * Ensure cannot be instantiated using default constructor.  This is not intended to be deployed as a
     * web service.  It is self managing, and creates it's own instance within a session, but only if a
     * client web service requires it to.
     */
    @SuppressWarnings("unused") // see comment above
    private SessionContextMgrBase() {
        assert( false ); // Not intended to be used.
    }

    /**
     * Private Constructor
     */
    protected SessionContextMgrBase(SessionContext session, Logger log) {
        this.log = log;
        if ( session == null ){
            log.warn( "Created session context with null session. Proceeding to see what happens...");
        }
        else {
            log.debug("{}, Create Session:", session.getSessionId());
        }
    }


    /**
     * Return the sessionId that we are operating within (e.g. Axis sessionId, or JSP session ID
     * @return String sessionId
     */
    public String getSessionId() {
        SessionContext session = ContextManager.getCurrentSessionContext();
        if (session != null) {
            return session.getSessionId();
        }
        else {
            return "[NULL SESSION!]";
        }
    }


    /**
     * invalidateSession() - force ContainerManager to invalidate the session,
     * even if it is operating as a singleton instance
     */
    public void invalidateSession() {
        SessionContext session = ContextManager.getCurrentSessionContext();
        log.trace(session.getSessionId() + ", invalidateSession: Thread = " + Thread.currentThread().getId());
        session.invalidate();
    }

    /**
     * Get the relevant instance of the object from the session, and if there isn't one, create
     * and instance and put it in the session.
     */
    protected <T extends IShutdown> T getServiceInternal(Class<T> serviceClass) {

        SessionContext session = ContextManager.getSession();
        T objInstance = null;
        synchronized(session) {
            String name = serviceClass.getSimpleName();
            objInstance = serviceClass.cast(session.get( name ));

            // If there wasn't one
            if (objInstance == null) {
                try {
                    objInstance = serviceClass.newInstance();
                    session.set( name, objInstance );
                    log.trace(session.getSessionId() + ", getService -> new :" + name);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Unexpected exception creating service", e );
                }
            }
        }
        return serviceClass.cast(objInstance);
    }

}