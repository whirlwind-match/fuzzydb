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
package org.fuzzydb.server.internal.index.btree.node;

import java.io.Serializable;

import org.fuzzydb.server.internal.index.btree.NodeW;



public abstract class Node implements Serializable, NodeW {

	private static final long serialVersionUID = 1L;

	Node() {
		
	}
	
	protected Node(Node node) {
	}

	@Override
	public abstract NodeW clone();
	
}
