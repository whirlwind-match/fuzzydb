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
package org.fuzzydb.client.internal.comms.messages;

import org.fuzzydb.core.exceptions.ArchException;

import com.wwm.expressions.LogicExpr;

public class QueryCmd extends TransactionCommand {
	
	private static final long serialVersionUID = 1L;

	private final String namespace;
	private final String forClass;
	private final LogicExpr index;
	private final LogicExpr expr;
	private final int fetchSize;
	private int qid;
	
    /** Default ctor for serialization libraries */
    private QueryCmd() {
        super(-1, -1, -1);
        this.namespace = null;
        this.forClass = null;
        this.index = null;
        this.expr = null;
        this.fetchSize = 0;
    }

    public QueryCmd(int storeId, String namespace, int cid, int tid, int qid, Class<?> forClass, LogicExpr index, LogicExpr expr, int fetchSize) {
		super(storeId, cid, tid);
		this.namespace = namespace;
		this.qid = qid;
		this.forClass = forClass.getCanonicalName();
		this.index = index;
		this.expr = expr;
		this.fetchSize = fetchSize;
	}

	public String getNamespace() {
		return namespace;
	}

	public Class<?> getForClass() {
        try {
            return Class.forName(forClass);
        }
        catch (ClassNotFoundException e) {
            throw new ArchException("Class " + forClass + " not not on (probably server) classpath", e);
        }
	}

	public LogicExpr getIndex() {
		return index;
	}

	public LogicExpr getExpr() {
		return expr;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public int getQid() {
		return qid;
	}

}
