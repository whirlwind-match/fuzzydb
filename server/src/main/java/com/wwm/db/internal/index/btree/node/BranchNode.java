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
package com.wwm.db.internal.index.btree.node;

import java.util.TreeMap;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.internal.RefImpl;

import com.wwm.db.internal.index.btree.BranchNodeW;
import com.wwm.db.internal.index.btree.PendingOperations;

class BranchNode extends Node implements BranchNodeW {
	
	private static final long serialVersionUID = 1L;
	private final TreeMap<Comparable<Object>, RefImpl> children  = new TreeMap<Comparable<Object>, RefImpl>();
	private RefImpl rightChild;

	private PendingOperations pendingOps;
	
	
	BranchNode() {
		pendingOps = new PendingOperations();
	}
	
	public PendingOperations getPendingOps()
	{
		return pendingOps;
	}

	private BranchNode(BranchNode node) {
		super(node);
		if (node.children != null) {
			this.children.putAll(node.children);
		}
		
		this.rightChild = node.rightChild;
		
		this.pendingOps = node.pendingOps.clone();
	}


	@Override
	public BranchNodeW clone() {
		return new BranchNode(this);
	}

	public void addPendingOps(PendingOperations ops) {
		pendingOps.addPendingOps(ops);
	}
	
	public int getPendingOpCount() {
		return pendingOps.getPendingOpCount();
	}
	
	public PendingOperations removePendingOps() {
		PendingOperations rval = pendingOps;
		pendingOps = new PendingOperations();
		return rval;
	}

	public void addLeft(Comparable<Object> key, RefImpl ref) {
		children.put(key, ref);
	}

	public void setRight(Ref ref) {
		rightChild = (RefImpl) ref;
	}

	public TreeMap<Comparable<Object>, RefImpl> getChildren() {
		return children;
	}

	public RefImpl getRightChild() {
		return rightChild;
	}
	
	public int getChildCount() {
		return children.size() + 1; // + 1 becuase there is always rightChild
	}
	
	public SplitOut splitOutLeft() {
		assert(children.size() >= 3); // need at least 3 keyed children and a right - 4 in total - to ensure the split nodes have 2 or more each
		//assert(pendingOps.getPendingOpCount() == 0); // We arn't going to split the pending ops too so make sure there arn't any
		
		BranchNode newLeft = new BranchNode();
		int childrenToMove = children.size()/2;	//	if size=3, this becomes 1 which is the min value. We also take one more left child to become the new right child.
		for (int i = 0; i < childrenToMove; i++) {
			Comparable<Object> key = children.firstKey();
			RefImpl value = children.remove(key);
			newLeft.addLeft(key, value);
		}
		Comparable<Object> key = children.firstKey();
		RefImpl value = children.remove(key);
		newLeft.setRight(value);
		
		PendingOperations leftOps = pendingOps.extractLeft(key);
		newLeft.addPendingOps(leftOps);
		
		return new SplitOut(newLeft, key);
	}

	public long[] getChildOids() {
		long[] rval = new long[children.size()];
		int i = 0 ;
		for (RefImpl ref : children.values()) {
			rval[i] = ref.getOid();
			i++;
		}
		return rval;
	}
}
