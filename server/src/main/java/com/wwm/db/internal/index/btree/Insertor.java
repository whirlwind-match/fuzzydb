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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.wwm.db.Ref;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.index.btree.BranchNodeW.SplitOut;
import com.wwm.db.internal.index.btree.node.NodeFactory;

/**
 * 
 * @param <T>
 */
class Insertor<T> extends Operator<T> {

    private static class SplitResult {

        final Comparable<Object> key;	// max key of newLeft
        final RefImpl<NodeW> newLeft;

        SplitResult(final Comparable<Object> key, final Ref<NodeW> newLeft) {
            super();
            this.key = key;
            this.newLeft = (RefImpl<NodeW>) newLeft;
        }
    }

    public Insertor(final BTree<T> tree) {
        super(tree);
    }


    private SplitResult insertIntoNode(BranchNodeR parentNode, Ref<BranchNodeW> parentRef, RefdNode node, PendingOperations ops) {

        SplitResult splitResult = null;
        NodeR n = node.node;
        Ref<? extends NodeW> ref = node.ref;


        // Insert into Leaf Node
        if (n instanceof LeafNodeR) {
            LeafNodeW leaf = getWritable(ref, (LeafNodeR) n);
            leaf.insertData(style, ops);
            if (leaf.getCount() > tree.getSplitThreshold() && leaf.getKeyCount() > 1) {
                // Splitting a leaf node. This node becomes new right node.
                LeafNodeW leftLeaf = NodeFactory.newLeafNode();
                leftLeaf.insertPeerData(leaf.splitOutLeft());
                Ref<? extends NodeW> nearestRef = (parentRef == null) ? ref : parentRef;
                Ref<NodeW> leftLeafRef = createNear(nearestRef, parentNode, leftLeaf);
                splitResult = new SplitResult(leftLeaf.getMaxKey(), leftLeafRef);
            }
            update(ref, leaf);
            return splitResult;
        }

        // Insert into Branch Node
        BranchNodeR bnr = (BranchNodeR) n;
        int nodeOpCount = bnr.getPendingOpCount();

        PendingOperations nodeOps = null;

        if (nodeOpCount > 0 && ops.getPendingOpCount() + nodeOpCount >= tree.getLazyInserts()) {
            // The number of outstanding ops on this node, plus the new ops, exceeds the lazy insert threshold.
            // The number of ops on the node is > 0, so we need to remove the ops from this node.
            BranchNodeW bnw = getWritable(ref, bnr);
            nodeOps = bnw.removePendingOps();
            nodeOps.addPendingOps(ops);
            update(ref, bnw);
        } else if (nodeOpCount + ops.getPendingOpCount() < tree.getLazyInserts()) {
            // The number of ops on this node, plus the new ones, is less than the treshold.
            // So we drop the new ops here
            BranchNodeW bnw = getWritable(ref, bnr);
            bnw.addPendingOps(ops);
            update(ref, bnw);
            return null;
        } else {
            nodeOps = ops;
        }

        // If we get to this point, we need to spread the ops amongst the children.
        // There should be no outstanding ops on this node. This is important because we may split this node.
        assert(getWritable(ref, bnr).getPendingOpCount() == 0);

        BranchNodeW bnw = null;
        TreeMap<Comparable<Object>, RefImpl> clonedChildren = new TreeMap<Comparable<Object>, RefImpl>();
        clonedChildren.putAll(bnr.getChildren());

        for (Entry<Comparable<Object>, RefImpl> entry : clonedChildren.entrySet()) {
            Comparable<Object> key = entry.getKey();
            PendingOperations po = nodeOps.extractLeft(key);
            if (po.getPendingOpCount() > 0) {
                RefdNode child = getNode(entry.getValue());
                SplitResult sr = insertIntoNode(bnw == null ? bnr : bnw, (Ref<BranchNodeW>) ref, child, po);
                if (sr != null) {
                    if (bnw == null) {
                        bnw = getWritable(ref, bnr);
                    }
                    bnw.addLeft(sr.key, sr.newLeft);
                }
            }

            if (nodeOps.getPendingOpCount() < tree.getLazyInserts()) {
                // We have flushed enough, so drop the lazies back here
                if (bnw == null) {
                    bnw = getWritable(ref, bnr);
                }
                bnw.addPendingOps(nodeOps);
                nodeOps = null;
                break;
            }


        }
        // right child?
        if (nodeOps != null && nodeOps.getPendingOpCount() > 0) {
            RefdNode child = getNode(bnr.getRightChild());
            SplitResult sr = insertIntoNode(bnw == null ? bnr : bnw, (Ref<BranchNodeW>) ref, child, nodeOps);
            if (sr != null) {
                if (bnw == null) {
                    bnw = getWritable(ref, bnr);
                }
                bnw.addLeft(sr.key, sr.newLeft);
            }
        }

        boolean split = false;
        if (bnr.getChildCount() > tree.getSplitThreshold()) {
            split = true;
        }
        if (bnw != null && bnw.getChildCount() > tree.getSplitThreshold()) {
            split = true;
        }

        if (split) {
            if (bnw == null) {
                // We havn't modified this node. That means everything fell through and there were no splits.
                // But the child count exceeds the split thresh. This is becuase we only split once at a time
                // so you can end up with giant nodes on big flushes.
                bnw = getWritable(ref, bnr);
            }
            // Splitting a branch node. This node becomes new right child
            SplitOut so = bnw.splitOutLeft();
            Ref<NodeW> newRef = createNear(parentRef, parentNode, so.node);
            splitResult = new SplitResult(so.key, newRef);
        }


        if (bnw != null) {
            update(ref, bnw);
        }

        return splitResult;
    }

    public void insert(Comparable<Object> key, Ref ref, Object object) {
        HashMap<Comparable<Object>, ArrayList<Object>> inserts = new HashMap<Comparable<Object>, ArrayList<Object>>();
        ArrayList<Object> al = new ArrayList<Object>();
        if (style == IndexPointerStyle.Copy) {
            al.add(new RefdObject((RefImpl) ref, object));
        } else if (style == IndexPointerStyle.Reference) {
            al.add(ref);
        } else {
            throw new RuntimeException();
        }
        inserts.put(key, al);

        PendingOperations ops = new PendingOperations();
        ops.addPendingInserts(inserts);

        RefdNode root = getRoot();
        if (root == null) {
            // Special case - no root. Create single leafnode
            LeafNodeW ln = NodeFactory.newLeafNode();
            ln.insertData(style, ops);
            Ref<NodeW> rootRef = table.allocOneRef();
            table.create(rootRef, ln);
            setRoot(rootRef);
            return;
        }

        SplitResult sr = insertIntoNode(null, null, root, ops);

        if (sr != null) {
            // The root node split, we need a new root
            BranchNodeW newRoot = NodeFactory.newBranchNode();
            newRoot.setRight(root.ref); // old root becomes the new right node
            newRoot.addLeft(sr.key, sr.newLeft);
            Ref<NodeW> newRootRef = createNear(sr.newLeft, null, newRoot);
            setRoot(newRootRef);
        }
    }

    public void remove(Comparable<Object> key, Ref ref) {
        HashMap<Comparable<Object>, ArrayList<Ref>> removes = new HashMap<Comparable<Object>, ArrayList<Ref>>();
        ArrayList<Ref> al = new ArrayList<Ref>();
        al.add(ref);
        removes.put(key, al);

        PendingOperations ops = new PendingOperations();
        ops.addPendingRemovals(removes);

        RefdNode root = getRoot();
        if (root == null) {
            // Special case - no root. Create single leafnode
            LeafNodeW ln = NodeFactory.newLeafNode();
            ln.insertData(style, ops);
            Ref<NodeW> rootRef = table.allocOneRef();
            table.create(rootRef, ln);
            setRoot(rootRef);
            return;
        }

        SplitResult sr = insertIntoNode(null, null, root, ops);

        if (sr != null) {
            // The root node split, we need a new root
            BranchNodeW newRoot = NodeFactory.newBranchNode();
            newRoot.setRight(root.ref); // old root becomes the new right node
            newRoot.addLeft(sr.key, sr.newLeft);
            Ref<NodeW> newRootRef = createNear(sr.newLeft, null, newRoot);
            setRoot(newRootRef);
        }
    }

}
