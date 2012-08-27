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
package com.wwm.db.internal.server;

import org.fuzzydb.server.internal.server.Namespace;


/**
 * This aspect is concerned with ensuring that the Namespace is published to WorkerThread, when doing
 * work within a namespace... thus allowing WorkerThread.getCurrentNamespace() to be called from elsewhere.
 */
public aspect PublishNamespace {


	pointcut targetClass(): within(Namespace);
	
	// define a pointcut for all public operations
	pointcut publicOperation(): targetClass() && execution(public * *(..)
				);

	// define simple operations
	pointcut simpleOperation(): targetClass() && 
		(
			execution( public * getAttributeCache() )
			|| execution( public * getIndexes() )
			|| execution( public * getLog() )
			|| execution( public * getName() )
			|| execution( public * getNamespaces() )
			|| execution( public * getPager() )
			|| execution( public * getPath() )
			|| execution( public * getStoreId() ) 
			|| execution( public * toString() ) 
		);
	
	// now create a combined pointcut that gives us the public operations that aren't simple
	pointcut significantOperations(Namespace ns): this(ns) && publicOperation() && !simpleOperation();
	
	before(Namespace ns): significantOperations(ns) {
		Namespace.setCurrentNamespace( ns ); 
	}

	// DONT do this as it'll reset to null on a recursive call
//	after(Namespace ns): significantOperations(ns) {
//		WorkerThread.setCurrentNamespace( null );
//	}
}
