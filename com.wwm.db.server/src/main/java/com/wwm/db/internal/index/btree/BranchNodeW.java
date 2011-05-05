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

import com.wwm.db.GenericRef;
import com.wwm.db.internal.RefImpl;

public interface BranchNodeW extends BranchNodeR, NodeW {
	public static class SplitOut {
		public final BranchNodeW node;
		public final Comparable<Object> key;
		public SplitOut(BranchNodeW node, Comparable<Object> key) {
			super();
			this.node = node;
			this.key = key;
		}
	}

	public void addPendingOps(PendingOperations ops);
	public PendingOperations removePendingOps();
	
	public void addLeft(Comparable<Object> key, RefImpl ref);
	public void setRight(GenericRef<NodeW> ref);
	
	public SplitOut splitOutLeft();
}
