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
package org.fuzzydb.client.marker;

/**
 * Interface for DB user objects that require namespacing.
 * Several namespace names are reserved:
 * 'default' specifies the default namespace and is the same as not using a namespace.
 * 'system' is reserved.
 *  
 * @author ac
 *
 */
@Deprecated
public interface Namespaced {
	public String getNamespace();
}
