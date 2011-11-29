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
package com.wwm.context.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.wwm.context.ApplicationContext;
import com.wwm.context.ContextManager;
import com.wwm.context.SessionContext;


/**
 * Class to listen for servlet start/stop events, and session start/end events, and ensure
 * that we make things available elsewhere.
 * 
 * This class can be registered in web.xml to configure then servlet container to call
 * here at the appropriate points.
 * 
 * @author Neale Upstone
 *
 */
public class WebContainerAppListener implements ServletContextListener, HttpSessionListener {

    /** Singleton instance */
    static private WebContainerAppListener instance;

    /*
     * Get singleton - note, this is across all web apps within this JVM
     * so should not be modified to contain any data
     */
    static public WebContainerAppListener getInstance() {
        if (instance == null) {
            instance = new WebContainerAppListener();
        }

        return instance;
    }


    /**
     * Anything need doing when registering
     */
    public void contextInitialized(ServletContextEvent event) {
        @SuppressWarnings("unused")
        ServletContext context = event.getServletContext();

        // See if there is already an instance provided in the web context (I guess that
        // this may have been done by a framework ...)


    }

    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

    }


    /**
     * Things to do at beginning of a new session.
     */
    public void sessionCreated(HttpSessionEvent event) {
        @SuppressWarnings("unused")
        HttpSession session = event.getSession(); //etc

    }

    /**
     * Do whatever needs doing at end of the session.
     */
    public void sessionDestroyed(HttpSessionEvent arg0) {
        // TODO Auto-generated method stub

    }


    /**
     * Prior to the request, we need to pass our app context and session context data
     * into our ThreadLocal based container, so that correct ones for this request
     * can be retrieved during servicing of the request.
     * The good news about ThreadLocals is that they are tied to the Thread instance, so when
     * the Thread is destroyed, so is the data we have tied to that thread.
     * The BAD news, which we do deal with, is that under a Web App Server, the threads don't get destroyed
     * so we need to do some cleaning up.
     * 
     * @param applicationContext - supplied from external singleton bean, so we don't need
     * @param request
     */
    public void preRequest( ApplicationContext applicationContext, HttpServletRequest request ) {

        HttpSession session = request.getSession(false); // Don't create if haven't got one already

        SessionContext sessionContext = getSessionContext(session);
        // Now make them available for this request
        ContextManager.initForThread( applicationContext,
                sessionContext, // Must set to null if necessary, as re-using Thread
                request );
    }


    /**
     * Get our SessionContext for this session.  This is stored in the HttpSession for
     * persistence between requests, so this method will first try to retrieve it from
     * the session, and if one isn't found, it creates one and then adds it to the session.
     * @param session
     * @return
     */
    static public SessionContext getSessionContext(HttpSession session) {

        if (session == null) {
            return null; // No session, so we return null to indicate sessionless operation
        }

        // Session context comes from session
        SessionContext sessionContext = (SessionContext) session.getAttribute( SessionContext.class.getName() );
        if (sessionContext == null) {
            sessionContext = new WebSessionContext(session);
            session.setAttribute( SessionContext.class.getName(), sessionContext);
        }
        return sessionContext;
    }


    public void postRequest( HttpServletRequest request ) {
        // nothing to do
    }
}
