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
package org.fuzzydb.server.internal.index.btree;

import java.util.ArrayList;

interface LeafNodeR extends NodeR, Cloneable {

	public abstract LeafNodeW clone();

	public abstract int getCount();

	public abstract int getKeyCount();

	public Comparable<Object> getMaxKey();
	
	ArrayList<Object> getChildren(Comparable<Object> key);
	
}