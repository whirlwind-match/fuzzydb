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
 * Bean that contains all session data that we want to be able to share between
 * different requests for a given session.
 * 
 * Interface is designed to be like javax.servlet.Session where possible.
 * 
 * @author Neale Upstone
 */
public interface SessionContext {
    
    /**
     * Get named object
     * @param name
     * @return object, or null
     */
    public Object get(String name);

    /**
     * Set object for a given name
     * @param name
     * @param objInstance
     */
    public void set(String name, Object objInstance);
 
    
    /**
     * Get the sessionId
     * @return String session id
     */
    public String getSessionId();

    /**
     * Set the sessionId.
     * Used where we have injected code to set up session when it was available.
     * @param id
     */
    public void setSessionId(String id);

    /**
     * Invalidate the session (should at least clear out all session objects)
     */
    public void invalidate();
}
