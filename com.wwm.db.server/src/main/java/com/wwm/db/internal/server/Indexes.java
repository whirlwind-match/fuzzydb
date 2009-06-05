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
package com.wwm.db.internal.server;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.wwm.db.annotations.Key;
import com.wwm.db.internal.MetaObject;
import com.wwm.db.internal.index.btree.BTree;
import com.wwm.db.internal.index.btree.IndexKeyUniqueness;
import com.wwm.db.internal.index.btree.IndexPointerStyle;
import com.wwm.db.internal.server.ServerTransaction.Mode;

/**
 * FIXME: Needs implementing as impl's of Index interface and gluing in to IndexManagerImpl
 */
public class Indexes implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Set<Class<?>> requiresIndexing = new HashSet<Class<?>>();
    private final Map<Class<?>, Map<String, BTree<?>>> indexes = new HashMap<Class<?>, Map<String, BTree<?>>>();
    private final Namespace namespace;

    Indexes(Namespace namespace) {
        this.namespace = namespace;
    }

    void createIndexes(final Class<?> forClass) {
        Class<?> superclass = forClass;
        boolean indexed = false;
        do {
            indexed |= createIndex(superclass);
            superclass = superclass.getSuperclass();
        } while (!superclass.equals(Object.class));

        if (indexed) {
            requiresIndexing.add(forClass);
        }
    }

    private <FC> boolean createIndex(Class<FC> forClass) {
        if (indexes.containsKey(forClass)) {
            return true;
        }

        boolean rval = false;

        Field[] fields = forClass.getDeclaredFields();

        for (final Field field : fields) {
            Key k = field.getAnnotation(Key.class);

            if (k!=null) {
                rval = true;
                String fieldName = field.getName();
                Map<String, BTree<?>> fieldIndexes = indexes.get(forClass);
                if (fieldIndexes == null) {
                    fieldIndexes = new HashMap<String, BTree<?>>();
                    indexes.put(forClass, fieldIndexes);
                }
                if (!fieldIndexes.containsKey(fieldName)) {
                    IndexKeyUniqueness unique = k.unique() ? IndexKeyUniqueness.UniqueKey : IndexKeyUniqueness.MultiKey;
                    IndexPointerStyle style = (k.type()==null||k.type()==Key.Mode.Value) ? IndexPointerStyle.Copy : IndexPointerStyle.Reference;

                    BTree<FC> btree = new BTree<FC>(namespace, forClass, fieldName, unique, style);
                    fieldIndexes.put(fieldName, btree);
                }
            }
        }
        return rval;
    }
    /*
	void ObjectVersion retrieve(Class<?> ofClass, String fieldName, Comparable key) {

	}

	void Iterator<ObjectVersion> retrieveAll(Class<?> ofClass, String fieldName, Comparable key) {

	}
     */

    public <E> void remove(MetaObject<E> mo) {
        Class<?> forClass = mo.getObject().getClass();
        if (!requiresIndexing.contains(forClass)) {
            return;
        }
        CurrentTransactionHolder.setTransactionMode(Mode.IndexWrite);
        do {
            remove(forClass, mo);
            forClass = forClass.getSuperclass();
        } while (!forClass.equals(Object.class));
        CurrentTransactionHolder.setTransactionMode(Mode.Normal);
    }

    @SuppressWarnings("unchecked") // for (Class<E>) cast
	public <E> void add(MetaObject<E> mo) {

        // HACK(ish): Increase version by 1 to match how VersionedObject ctors are used elsewhere
        mo = new MetaObject<E>( mo.getRef(), mo.getVersion() + 1, mo.getObject() );

        Class<? super E> forClass = (Class<E>) mo.getObject().getClass();
        if (!requiresIndexing.contains(forClass)) {
            return;
        }
        CurrentTransactionHolder.setTransactionMode(Mode.IndexWrite);
        // Add using each class in super hierarchy so is seen if superclass is indexed.  
        do {
            add(forClass, mo);
            forClass = forClass.getSuperclass();
        } while (!forClass.equals(Object.class));
        CurrentTransactionHolder.setTransactionMode(Mode.Normal);
    }

    private <FC> void remove(Class<FC> forClass, MetaObject<?> mo) {
        Map<String, BTree<?>> fieldIndexes = indexes.get(forClass);
        if (fieldIndexes != null) {
            for (BTree<?> btree : fieldIndexes.values()) {
                btree.remove(mo);
            }
        }
    }

    @SuppressWarnings("unchecked") // for (Map) cast.  Be my guest if you think you can solve it :)
	private <FC> void add(Class<?> forClass, MetaObject<FC> mo) {
        Map<String, BTree<FC>> fieldIndexes = (Map)indexes.get(forClass);
        if (fieldIndexes != null) {
            for (BTree<FC> btree : fieldIndexes.values()) {
                btree.insert(mo);
            }
        }
    }

    public <FC> MetaObject<?> lookup(Class<FC> forClass, String fieldName, Comparable<?> keyValue) {
        Map<String, BTree<?>> fieldIndexes = indexes.get(forClass);
        if (fieldIndexes == null) {
            return null;
        }
        BTree<?> btree = fieldIndexes.get(fieldName);
        if (btree == null) {
            return null;
        }
        return btree.get(keyValue);
    }
}
