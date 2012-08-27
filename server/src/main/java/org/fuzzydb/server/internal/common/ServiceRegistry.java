/******************************************************************************
 * Copyright (c) 2005-2012 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.server.internal.common;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Generic application context for storing service beans that may be wanted elsewhere.
 */
public class ServiceRegistry {
	
	private static final ServiceRegistry instance = new ServiceRegistry();
	
	private Injector injector;
	
	private ServiceRegistry() {
	}
	
	/**
	 * Initialise global instance with the modules for configuration
	 */
	static public void initialise(Module ... modules) {
		instance.injector = Guice.createInjector(modules);
	}
	
	public static <S> S getService(Class<S> type) {
		return instance.injector.getInstance(type);
	}
}
