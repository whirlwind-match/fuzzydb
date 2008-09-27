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

import java.util.Map;
import java.util.TreeMap;

/**
 * Bean that contains all session data that we want to be able to share between
 * different requests for a given session.
 * 
 * Interface is designed to be like javax.servlet.Session where possible.
 * 
 * @author Neale Upstone
 */
public class SimpleSessionContext implements SessionContext {

	static private Object lock = new Object();
    static private int idCount = 0;
    private String sessionId;

    /**
     * Map of objects for this context.
     */
    private Map<String, Object> objects;

    /**
     * Construct one
     */
    public SimpleSessionContext() {
        synchronized (lock) {
            idCount++;
            sessionId = String.valueOf(idCount);
        }
        objects = new TreeMap<String, Object>();
    }


    /**
     * Get named object
     * @param name
     * @return object, or null
     */
    public Object get(String name) {
        return objects.get( name );
    }



    /**
     * Set object for a given name
     * @param name
     * @param objInstance
     */
    public void set(String name, Object objInstance) {
        objects.put( name, objInstance );
    }



    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String id) {
        sessionId = id;
    }


    public void invalidate() {
        // TODO Think I'm going to need a callback here to be able to go and invalidate
        // a JSP session
        for (Object o: objects.values()) {
            if (o instanceof IShutdown) {
                IShutdown service = (IShutdown) o;
                service.shutdown();
            }
        }

        objects.clear();
    }
}
