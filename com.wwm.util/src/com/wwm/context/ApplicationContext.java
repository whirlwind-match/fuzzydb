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


/**
 * Interface for a bean that contains all application data that we want to 
 * be able to share between different sessions of a given application instance.
 * 
 * Implementations may be dumb Maps, or actually access some data available in the 
 * context, such as for Axis, we can use the MessageContext.getCurrentContext() to 
 * find our session and application information.
 * 
 * @author Neale Upstone
 */
public interface ApplicationContext {

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
     * Invalidates the current Application Context Data
     */
    public void invalidate();
    
   }
