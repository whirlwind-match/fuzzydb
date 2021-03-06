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
package org.fuzzydb.core.query;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RetrieveSpec {
	public void add(Class<? extends Object> clazz, String fieldName, Object key);
	public void addAll(Class<? extends Object> clazz, String fieldName, Collection<Object> keys);
	public Set<Class<? extends Object>> getClasses();
	public Map<String, RetrieveSpecItem> getSpecs(Class<? extends Object> clazz);
}
