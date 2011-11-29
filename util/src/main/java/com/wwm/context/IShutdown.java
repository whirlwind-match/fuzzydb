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
 * For implementation by any context object that needs shutting down when the object
 * is removed from the context, or the context destroyed (e.g. session context invalidated).
 */
public interface IShutdown {

    void shutdown();

}
