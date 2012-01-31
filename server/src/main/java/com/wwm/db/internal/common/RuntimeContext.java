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
package com.wwm.db.internal.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic application context for storing service beans that may be wanted elsewhere.
 */
public class RuntimeContext {
	
	
	private static final RuntimeContext instance = new RuntimeContext();
	
	// Currently does not support finding beans matching an interface although...
	private final Map<String, Object> beans = new HashMap<String, Object>();
	
	
	public static final RuntimeContext getInstance() {
		return instance; 
	}

	private RuntimeContext() {
	}
	
	public void addBean(Object bean) {
		// TODO assert duplicates extract interfaces etc, or just migrate to something like Guice
		beans.put(bean.getClass().getCanonicalName(), bean);
	}

	/**
	 * Adds a bean that fulfills the role of the given serviceInterface, such that
	 * it can be retrieved using {@link #getBean(Class)} against that interface.
	 */
	public <I, B extends I>void addBean(B bean, Class<I> serviceInterface) {
		beans.put(serviceInterface.getCanonicalName(), bean);
	}

	
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> beanClass) {
		return (T) beans.get(beanClass.getCanonicalName());
	}
	
}
