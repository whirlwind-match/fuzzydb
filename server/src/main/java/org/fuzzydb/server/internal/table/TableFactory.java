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
package org.fuzzydb.server.internal.table;

import org.fuzzydb.server.internal.index.IndexManager;
import org.fuzzydb.server.internal.index.IndexManagerImpl;
import org.fuzzydb.server.internal.index.IndexedTable;
import org.fuzzydb.server.internal.pager.RawPagedTableImpl;
import org.fuzzydb.server.internal.server.Namespace;
import org.fuzzydb.server.internal.server.VersionedObject;

/**
 * Abstraction for the creation of Tables, so we can have different ones for different purposes (as we did in Db1)
 */
public class TableFactory {

    public static <T> UserTable<T> createPagedUserTable(Namespace namespace, Class<T> clazz, int id) {

        // Create a paged underlying table with a user table on top of the stack
        // 3 entity stack
        RawTable<VersionedObject<T>> rawTable = new RawPagedTableImpl<VersionedObject<T>>(namespace, clazz);
        Table<T, VersionedObject<T>> table = new TableImpl<T, VersionedObject<T>>(rawTable, id);
        UserTable<T> userTable = new UserTableImpl<T>(table);

        // User table has an index manager built in?
        IndexManager<T> indexManager = new IndexManagerImpl<T>(userTable, clazz);

        IndexedTable<T> indexed = new IndexedTable<T>(userTable, indexManager);
		indexManager.detectNewIndexes(); // Recent change 9/9/08

        return indexed;
    }

    public static <E> Table<E, E> createPagedIndexTable(Class<E> forNode, Namespace namespace, Class<?> clazz, String fieldName) {

        // Create a paged underlying table with a user table on top of the stack
        // 2 entity stack
        RawTable<E> rawTable = new RawPagedTableImpl<E>(namespace, clazz, '@' + fieldName); // class@field is used as disk name
        Table<E, E> table = new TableImpl<E, E>(rawTable, -1); // table id not required for index tables

        return table;
    }
}
