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
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.internal.MetaObject;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.index.btree.node.RootSentinel;
import com.wwm.db.internal.table.Table;

/**
 * Implementation to perform a lookup using the BTree that indexes a field within
 * a class of type T.
 *
 * @param <T>
 */
public class BtreeLookup<T> {

    private final BTree<T> btree;
    private Table<NodeW,NodeW> table;

    private HashSet<RefImpl> removalPending = new HashSet<RefImpl>();

    BtreeLookup(BTree<T> btree) {
        this.btree = btree;
        this.table = btree.getTable();
    }

    @SuppressWarnings("unchecked")
    public MetaObject get(Comparable<?> key) {
        RootSentinel rs = null;
        try {
            rs = (RootSentinel) table.getObject(btree.getSentinel());
        } catch (UnknownObjectException e) {
            throw new RuntimeException("Missing root in index", e);
        }
        RefImpl<NodeW> rootRef = rs.getRoot();
        if (rootRef == null) {
            return null;
        }
        NodeR node = null;
        try {
            node = table.getObject(rootRef);
        } catch (UnknownObjectException e) {
            throw new RuntimeException("Missing node in index", e);
        }
        return get((Comparable<Object>)key, node);
    }

    private MetaObject<T> get(Comparable<Object> key, NodeR node) {
        if (node instanceof BranchNodeR) {
            BranchNodeR branch = (BranchNodeR) node;
            PendingOperations po = branch.getPendingOps();
            if (po != null) {
                ArrayList<Object> al = po.getInserts().get(key);
                if (al != null && al.size() > 0) {
                    Object o = al.get(0);
                    MetaObject<T> mo = convertObject(o);
                    if (!removalPending.contains(mo.getRef())) {
                        return mo;
                    } else {
                        removalPending.remove(mo.getRef());
                    }
                }
                ArrayList<RefImpl> deleted = po.getRemovals().get(key);
                if (deleted != null) {
                    removalPending.addAll(deleted);
                }
            }
            TreeMap<Comparable<Object>, RefImpl> children = branch.getChildren();
            for (Entry<Comparable<Object>, RefImpl> entry : children.entrySet()) {
                if (key.compareTo(entry.getKey()) <= 0) {
                    NodeR child;
                    try {
                        child = table.getObject(entry.getValue());
                    } catch (UnknownObjectException e) {
                        throw new RuntimeException("Missing node in index", e);
                    }
                    return get(key, child);
                }
            }
            NodeR child;
            try {
                child = table.getObject(branch.getRightChild());
            } catch (UnknownObjectException e) {
                throw new RuntimeException("Missing node in index", e);
            }
            return get(key, child);
        } else {
            LeafNodeR leaf = (LeafNodeR) node;
            ArrayList<Object> al = leaf.getChildren(key);
            if (al == null || al.size() == 0) {
                return null;
            }
            for (Object o : al) {
                MetaObject<T> mo = convertObject(o);
                if (!removalPending.contains(mo.getRef())) {
                    return mo;
                } else {
                    removalPending.remove(mo.getRef());
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	private MetaObject<T> convertObject(Object o) {
        if (btree.getStyle() == IndexPointerStyle.Copy) {
            RefdObject ro = (RefdObject)o;
            return (MetaObject<T>) ro.object;
        } else {
            RefImpl<T> ref = (RefImpl<T>)o;
            try {
                return btree.getNamespace().getObject(ref);
            } catch (UnknownObjectException e) {
                throw new RuntimeException("Missing object in index", e);
            }
        }
    }

}
