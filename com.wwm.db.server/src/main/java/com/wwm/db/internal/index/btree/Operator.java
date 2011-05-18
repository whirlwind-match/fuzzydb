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

import com.wwm.db.GenericRef;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.internal.index.btree.node.NodeFactory;
import com.wwm.db.internal.index.btree.node.RootSentinel;
import com.wwm.db.internal.table.Table;

abstract class Operator<T> {

    protected final BTree<T> tree;
    protected final IndexPointerStyle style;
    protected final Table<NodeW, NodeW> table;

    private final HashMap<GenericRef<? extends NodeR>, NodeW> writeBehind = new HashMap<GenericRef<? extends NodeR>, NodeW>();

    protected Operator(BTree<T> tree) {
        super();
        this.tree = tree;
        this.style = tree.getStyle();
        this.table = tree.getTable();
    }

    protected RefdNode getNode(GenericRef<NodeW> ref) {
        NodeR node = table.getObject(ref);
        if (node == null) {
            return null;
        }
        return new RefdNode(ref, node);
    }

    protected RefdNode getRoot() {
        RootSentinel rs = (RootSentinel)table.getObject(tree.getSentinel());
        GenericRef<NodeW> rootRef = rs.getRoot();
        if (rootRef == null) {
            return null;
        }
        return getNode(rootRef);
    }

    protected void setRoot(GenericRef<NodeW> ref) {
        RootSentinel rs = new RootSentinel(ref);
        table.update(tree.getSentinel(), rs);
    }

    /**
     * 
     * @param ref - ref can be a read ref
     * @param ln
     * @return
     */
    protected LeafNodeW getWritable(GenericRef<? extends NodeR> ref, LeafNodeR ln) {
        NodeW n = writeBehind.get(ref);
        if (n != null) {
            return (LeafNodeW) n;
        }
        LeafNodeW cloned = ln.clone();
        writeBehind.put(ref, cloned);
        return cloned;
    }

    protected BranchNodeW getWritable(GenericRef<? extends NodeR> ref, BranchNodeR bn) {
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

    protected GenericRef<NodeW> createNear(GenericRef<? extends NodeW> nearestRef, BranchNodeR parentNode, NodeW newNode) {
        GenericRef<NodeW> ref = null;
        if (nearestRef != null) {
            ref = table.allocOneRefNear((GenericRef<NodeW>) nearestRef, parentNode==null?null:parentNode.getChildOids());
        } else {
            ref = table.allocOneRef();
        }
        writeBehind.put(ref, newNode);
        return ref;
    }

    protected void update(GenericRef<? extends NodeW> ref, NodeW updatedNode) {
        writeBehind.put(ref, updatedNode);
    }

    void flush() {
        for (Entry<GenericRef<? extends NodeR>, NodeW> entry : writeBehind.entrySet()) {
        	GenericRef<? extends NodeR> ref = entry.getKey();
            NodeW node = entry.getValue();
            try {
                table.createUpdate((GenericRef<NodeW>) ref, node);
            } catch (UnknownObjectException e) {
                throw new RuntimeException("Error in Index Flush", e);
            }
        }
    }
}
