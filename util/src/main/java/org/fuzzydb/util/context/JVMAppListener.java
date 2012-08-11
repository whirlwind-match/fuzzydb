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


/**
 * Class to listen for servlet start/stop events, and session start/end events, and ensure
 * that we make things available elsewhere.
 * 
 * This version is for applications running directly in the JVM, so applies to things like
 * the SearchDemo app, JUnit tests, and DbInitialiser
 * 
 * @author Neale Upstone
 *
 */
public class JVMAppListener  {

    static private JVMAppListener instance;

    private ApplicationContext appContext = new SimpleApplicationContext();
    private SessionContext sessionContext = new SimpleSessionContext();
    private boolean singleSessionForAllThreads = false;

    private JVMAppListener() {
        sessionContext.setSessionId("--JUnit Session one per JVM--");
    }

    static public JVMAppListener getInstance() {
        if (instance == null) {
            instance = new JVMAppListener();
        }

        return instance;
    }



    /**
     * Anything need doing when registering
     */
    public void contextInitialized() {
        // This just mirrors the unused functions in WebAppListener
    }

    public void contextDestroyed() {
        // This just mirrors the unused functions in WebAppListener
    }


    /**
     * Things to do at beginning of a new session.
     */
    public void sessionCreated() {
        // This just mirrors the unused functions in WebAppListener
    }

    /**
     * Do whatever needs doing at end of the session.
     */
    public void sessionDestroyed() {
        // This just mirrors the unused functions in WebAppListener
    }


    /**
     * Prior to the request, we need to pass our app context and session context data
     * into our ThreadLocal based container, so that correct ones for this request
     * can be retrieved during servicing of the request.
     * The good news about ThreadLocals is that they are tied to the Thread instance, so when
     * the Thread is destroyed, so is the data we have tied to that thread.
     */
    public void preRequest() {
        if (singleSessionForAllThreads) {
            ContextManager.initForThread( appContext, sessionContext, null );
        } else {
            ContextManager.initForThread( appContext, new SimpleSessionContext(), null );
        }
    }

    /**
     * Sets single user application mode, where multiple threads will be referring to the same
     * session.  e.g. AWT GUI has diff thread when you click a button.
     */
    public void setSingleSession() {
        singleSessionForAllThreads = true;
        ContextManager.setSingleForAllThreads();
    }


    public void postRequest() {
        // nothing to do
    }
}
