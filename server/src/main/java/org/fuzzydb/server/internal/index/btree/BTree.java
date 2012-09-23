/******************************************************************************
 * Copyright (c) 2005-2012 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.server.internal.index.btree;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.fuzzydb.client.IndexPointerStyle;
import org.fuzzydb.client.Ref;
import org.fuzzydb.client.SingleFieldIndexDefinition;
import org.fuzzydb.client.internal.MetaObject;
import org.fuzzydb.server.internal.common.MetaObjectSource;
import org.fuzzydb.server.internal.common.YoofRepository;
import org.fuzzydb.server.internal.index.btree.node.RootSentinel;
import org.fuzzydb.server.internal.server.Namespace;
import org.fuzzydb.server.internal.table.Table;


/**
 * A BTree indexing a field within an object of class T.
 *
 * @param <T>
 */
public class BTree<T> implements /*Index<Object>,*/ Serializable {

    private static final long serialVersionUID = 1L;

    private final IndexPointerStyle style;
    private final IndexKeyUniqueness unique;

    private final YoofRepository<NodeW, NodeW> table;

    private final Ref<NodeW> sentinel;

    private static final int splitThreshold = 4;
    private static final int mergeThreshold = 2;
    private static final int lazyInserts = 20;
    private transient Field field;
    private final Class<T> forClass;

    private final String fieldName;
    private final MetaObjectSource namespace;


    public BTree(Table<NodeW, NodeW> table, Namespace namespace, SingleFieldIndexDefinition<T> indexDefinition) {
    	this.table = table;
    	this.style = indexDefinition.style;
        this.unique = indexDefinition.unique ? IndexKeyUniqueness.UniqueKey : IndexKeyUniqueness.MultiKey;
        this.sentinel = table.allocOneRef();
        table.create(sentinel, new RootSentinel(null));
        forClass = indexDefinition.forClass;
        this.fieldName = indexDefinition.fieldName;
        this.namespace = namespace;
    }

    @SuppressWarnings("unchecked")
    private Comparable<Object> getFieldValue(Object object) {
        if (field == null) {
            try {
                field = forClass.getDeclaredField(fieldName);
            } catch (SecurityException e) {
                throw new RuntimeException("Unable to access key field", e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Unable to access key field", e);
            }
            if (!field.isAccessible()) {
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
					public Object run() {
                        field.setAccessible(true);
                        return null;
                    }
                });
            }
        }
        Object fieldValue = null;
        try {
            fieldValue = field.get(object);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to access key field", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to access key field", e);
        }
        if (fieldValue == null) {
            return null;
        }
        if (fieldValue instanceof Comparable) {
            return (Comparable<Object>) fieldValue;
        }
        return null; // ?
    }

    public void insert(MetaObject<T> mo) {
        Ref<T> ref = mo.getRef();
        Object object = mo.getObject();
        Insertor<T> insertor = new Insertor<T>(this);
        Comparable<Object> key = getFieldValue(object);
        if (key == null) {
            return;
        }

        insertor.insert(key, ref, mo);
        insertor.flush();
    }
    
    public boolean contains(MetaObject<T> mo) {
    	Comparable<Object> value = getFieldValue(mo.getObject());
    	return get(value) != null;
    }

    public MetaObject<T> get(Comparable<?> key) {
        BtreeLookup<T> lookup = new BtreeLookup<T>(this);
        return lookup.get(key);
    }

    public void remove(MetaObject<T> mo) {
        Ref<T> ref = mo.getRef();
        Object object = mo.getObject();
        Insertor<T> insertor = new Insertor<T>(this);
        Comparable<Object> key = getFieldValue(object);
        if (key == null) {
            return;
        }

        insertor.remove(key, ref);
        insertor.flush();
    }

    public IndexPointerStyle getStyle() {
        return style;
    }

    YoofRepository<NodeW, NodeW> getTable() {
        return table;
    }


    public Ref<NodeW> getSentinel() {
        return sentinel;
    }


    public int getLazyInserts() {
        return lazyInserts;
    }


    public int getMergeThreshold() {
        return mergeThreshold;
    }


    public int getSplitThreshold() {
        return splitThreshold;
    }


    public IndexKeyUniqueness getUnique() {
        return unique;
    }

    MetaObjectSource getMetaObjectSource() {
        return namespace;
    }

}
