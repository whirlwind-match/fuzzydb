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

import java.util.HashMap;
import java.util.Map.Entry;

import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.index.btree.node.NodeFactory;
import com.wwm.db.internal.index.btree.node.RootSentinel;
import com.wwm.db.internal.table.Table;

abstract class Operator<T> {

    protected final BTree<T> tree;
    protected final IndexPointerStyle style;
    protected final Table<NodeW, NodeW> table;

    private final HashMap<RefImpl, NodeW> writeBehind = new HashMap<RefImpl, NodeW>();

    protected Operator(BTree<T> tree) {
        super();
        this.tree = tree;
        this.style = tree.getStyle();
        this.table = tree.getTable();
    }

    protected RefdNode getNode(RefImpl ref) {
        NodeR node = null;
        try {
            node = table.getObject(ref);
        } catch (UnknownObjectException e) {
            throw new Error(e);
        }
        if (node == null) {
            return null;
        }
        return new RefdNode(ref, node);
    }

    protected RefdNode getRoot() {
        try {
            RootSentinel rs = (RootSentinel)table.getObject(tree.getSentinel());
            RefImpl rootRef = rs.getRoot();
            if (rootRef == null) {
                return null;
            }
            return getNode(rootRef);
        } catch (UnknownObjectException e) {
            throw new Error(e);
        }
    }

    protected void setRoot(RefImpl ref) {
        try {
            RootSentinel rs = new RootSentinel(ref);
            table.update(tree.getSentinel(), rs);
        } catch (UnknownObjectException e) {
            throw new Error(e);
        }
    }

    protected LeafNodeW getWritable(RefImpl ref, LeafNodeR ln) {
        NodeW n = writeBehind.get(ref);
        if (n != null) {
            return (LeafNodeW) n;
        }
        LeafNodeW cloned = ln.clone();
        writeBehind.put(ref, cloned);
        return cloned;
    }

    protected BranchNodeW getWritable(RefImpl ref, BranchNodeR bn) {
        NodeW n = writeBehind.get(ref);
        if (n != null) {
            return (BranchNodeW) n;
        }
        BranchNodeW cloned = bn.clone();
        writeBehind.put(ref, cloned);
        return cloned;
    }

    protected LeafNodeW newLeafNode() {
        return NodeFactory.newLeafNode();
    }

    protected RefImpl createNear(RefImpl near, BranchNodeR parentNode, NodeW newNode) {
        RefImpl ref = null;
        if (near != null) {
            ref = table.allocOneRefNear(near, parentNode==null?null:parentNode.getChildOids());
        } else {
            ref = table.allocOneRef();
        }
        writeBehind.put(ref, newNode);
        return ref;
    }

    protected void update(RefImpl ref, NodeW updatedNode) {
        writeBehind.put(ref, updatedNode);
    }

    void flush() {
        for (Entry<RefImpl, NodeW> entry : writeBehind.entrySet()) {
            RefImpl ref = entry.getKey();
            NodeW node = entry.getValue();
            try {
                table.createUpdate(ref, node);
            } catch (UnknownObjectException e) {
                throw new Error("Error in Index Flush", e);
            }
        }
    }
}
