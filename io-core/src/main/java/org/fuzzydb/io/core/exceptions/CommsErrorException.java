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
package org.fuzzydb.io.core.exceptions;

import org.fuzzydb.core.exceptions.ArchException;

@SuppressWarnings("serial")
public class CommsErrorException extends ArchException {

	public CommsErrorException() {
		
	}

	public CommsErrorException(Throwable cause) {
		super(cause);
	}
	
}
