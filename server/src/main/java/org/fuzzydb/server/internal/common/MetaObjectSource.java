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

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.exceptions.UnknownObjectException;
import org.fuzzydb.client.internal.MetaObject;

/**
 * Allows the retrieval of any object in the same namespace/context.
 */
public interface MetaObjectSource {

	<T> MetaObject<T> getObject(Ref<T> ref) throws UnknownObjectException;

}