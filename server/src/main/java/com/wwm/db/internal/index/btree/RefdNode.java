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
package com.wwm.db.internal.index.btree;

import org.fuzzydb.client.Ref;

class RefdNode {

//	private static final long serialVersionUID = 1L;

	final NodeR node;
	final Ref<NodeW> ref;
	
	RefdNode(Ref<NodeW> ref, NodeR node) {
		this.ref = ref;
		this.node = node;
	}
}
