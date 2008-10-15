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
package com.wwm.db.internal.index;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;

import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.internal.MetaObject;
import com.wwm.db.internal.server.Namespace;
import com.wwm.db.internal.server.TransactionControl;
import com.wwm.db.internal.table.UserTable;
import com.wwm.expressions.ExprContext;
import com.wwm.expressions.LogicExpr;

public class Query implements ExprContext {

    //private ServerTransaction transaction;
    //private Namespace namespace;
    //private Class<T> forClass;
    private LogicExpr index;
    private LogicExpr expr;
    private int fetchSize;

    private Iterator<MetaObject<?>> iterator;

    private Object lastResult = null;
    private MetaObject<?> candidate;

    @SuppressWarnings("unchecked")
	public <T> Query(TransactionControl transaction, Namespace namespace, Class<T> forClass, LogicExpr index, LogicExpr expr, int fetchSize) throws ArchException {
        //this.transaction = transaction;
        //this.namespace = namespace;
        //this.forClass = forClass;
        this.index = index;
        this.expr = expr;
        this.fetchSize = fetchSize;

        UserTable<T> ut = null;

        if (namespace != null) {
            ut = namespace.getTable(forClass);
        }

        if (ut == null)
        {
            ArrayList<MetaObject<T>> dummy = new ArrayList<MetaObject<T>>();
            iterator = (Iterator)dummy.iterator();
        } else {
            if (index == null) {
                iterator = (Iterator)ut.iterator();
            } else {
                iterator = (Iterator)ut.iterator();
                //iterator = namespace.getIndexIterator(forClass, index);
            }
        }
        lastResult = getNext();
    }

    public ArrayList<Object> fetch() throws ArchException {

        ArrayList<Object> results = new ArrayList<Object>();

        while (results.size() < fetchSize && lastResult != null) {

            results.add(lastResult);
            lastResult = getNext();
        }

        return results;
    }

    public boolean isMoreResults() {
        return lastResult != null;
    }

    /**
     * @return the next object, or null if there are no more
     * @throws ArchException
     */
    private Object getNext() throws ArchException {
        boolean test1;
        boolean test2;
        do
        {
            if (!iterator.hasNext()) {
                return null;
            }

            candidate = iterator.next();

            test1 = expr==null || expr.evaluate(this);
            test2 = index==null || index.evaluate(this);

        } while ((!test1) || (!test2));	// TODO: Remove index eval when index iterator is done

        return candidate;
    }

    public Comparable<?> getField(String fieldName) throws ArchException {
        try {
            Class<?> c = candidate.getObject().getClass();
            final Field backdoor = c.getDeclaredField(fieldName);

            if (backdoor == null) {
                return null;
            }

            if (!backdoor.isAccessible()) {
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        backdoor.setAccessible(true);
                        return null;
                    }
                });
            }

            Object o = backdoor.get(candidate.getObject());
            if (o == null) {
                return null;
            }
            if (o instanceof Comparable) {
                return (Comparable<?>)o;
            }

        } catch (Exception e) {
            throw new ArchException(e); 	// Problem getting the field, does not exist or no access permission
        }
        assert(false); 	// Someone did a search on a field that wasn't a supported type!	TODO: handle this
        return null;
    }

}
