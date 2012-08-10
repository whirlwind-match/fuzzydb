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

import org.fuzzydb.core.exceptions.ArchException;

@SuppressWarnings("serial")
// FIXME: we should override the constructor to update some Db stats (JMX object), so we can
// look at whether there are large number of collisions happening.
// These stats should be logged to disk, or committed to database, periodically (if changed).
public class WriteCollisionException extends ArchException {

	public WriteCollisionException() {
		super();
	}

	public WriteCollisionException(String message, Throwable cause) {
		super(message, cause);
	}

	public WriteCollisionException(String message) {
		super(message);
	}

	public WriteCollisionException(Throwable cause) {
		super(cause);
	}

}
