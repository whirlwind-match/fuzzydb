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
 * Our base Exception for catching Indexer-specific exceptions. Implmented
 * constructors allow derived classes to access those methods from Java's
 * Exception.
 */
@SuppressWarnings("serial")
public class IndexerException extends RuntimeException {

	protected IndexerException() {
		super();
	}

	public IndexerException(Throwable cause) {
		super(cause);
	}

	public IndexerException(String message, Throwable cause) {
		super(message, cause);
	}

	public IndexerException(String message) {
		super(message);
	}
}
