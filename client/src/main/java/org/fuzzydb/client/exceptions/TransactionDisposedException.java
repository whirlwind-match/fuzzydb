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
package org.fuzzydb.client.exceptions;

import com.wwm.db.core.exceptions.ArchException;

@SuppressWarnings("serial")
public class TransactionDisposedException extends ArchException {

	public TransactionDisposedException() {
		super();
	}

	public TransactionDisposedException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransactionDisposedException(String message) {
		super(message);
	}

	public TransactionDisposedException(Throwable cause) {
		super(cause);
	}
}
