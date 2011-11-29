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
package com.wwm.abdera.util.server;

/**
 * Exception to indicate that content was not found.
 * In an HTTP environment, this can be used to indicate that SC_NOT_FOUND would be returned as a status code.
 */
public class NotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NotFoundException() {
		super();
	}
	
	public NotFoundException(String message) {
		super(message);
	}
	
	public NotFoundException(Throwable cause){
		super(cause);
	}
	
	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}
