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
package com.wwm.indexer.exceptions;

/**
 * Exception for errors in dealing with attributes (conversion, wrong content, etc)
 */
@SuppressWarnings("serial")
public class AttributeException extends IndexerException {

    protected AttributeException() {
        super();
    }

    public AttributeException(Throwable cause) {
        super(cause);
    }

    public AttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttributeException(String message) {
        super(message);
    }
}
