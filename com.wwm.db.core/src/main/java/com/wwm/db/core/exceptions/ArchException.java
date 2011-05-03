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
package com.wwm.db.core.exceptions;


/**
 * Our base Exception for catching DB-specific exceptions.
 * Implmented constructors allow derived classes to access those
 * methods from Java's Exception.
 * @author nu
 *
 */
@SuppressWarnings("serial")
public class ArchException extends RuntimeException {

    /**
     * TODO Not sure if we want this one implemented.
     */
    public ArchException(){
        super();
    }

    public ArchException(Throwable cause) {
        super(cause);
    }
    
    public ArchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchException(String message) {
        super(message);
    }
}
