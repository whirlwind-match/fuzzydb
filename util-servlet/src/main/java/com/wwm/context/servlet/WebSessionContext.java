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

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import com.wwm.context.SessionContext;


/**
 * Bean that contains all session data that we want to be able to share between
 * different requests for a given session.
 * 
 * Interface is designed to be like javax.servlet.Session where possible.
 * 
 * @author Neale Upstone
 *
 */
public class WebSessionContext implements SessionContext {


    private HttpSession httpSession;

    /**
     * Construct one
     */
    public WebSessionContext(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public String getSessionId() {
        return httpSession.getId();
    }

    public void setSessionId(String id) {
        // Do nothing.
        throw new UnsupportedOperationException("setSessionId() not supported for WebSessionContext");
    }

    /**
     * Get named object
     * @param name
     * @return object, or null
     */
    public Object get(String name) {
        return httpSession.getAttribute( name );
    }

    /**
     * Set object for a given name
     * @param name
     * @param objInstance
     */
    public void set(String name, Object objInstance) {
        httpSession.setAttribute( name, objInstance );
    }

    /**
     * Invalidate session
     */
    public void invalidate() {
        httpSession.invalidate();
    }

    @SuppressWarnings("unchecked")
	@Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        Enumeration<String> en = httpSession.getAttributeNames();
        while ( en.hasMoreElements() ) {
            str.append( en.nextElement() );
            if ( en.hasMoreElements() ) {
                str.append( ", " );  // NOTE: Don't do any more than this for now, as http Session contains this, so would be recursive.
            }
        }

        return str.toString();
    }
}
