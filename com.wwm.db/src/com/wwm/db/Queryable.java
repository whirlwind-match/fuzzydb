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
package com.wwm.db;

import java.util.Collection;
import java.util.Map;

import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.query.ResultSet;
import com.wwm.db.query.RetrieveSpec;
import com.wwm.db.query.RetrieveSpecResult;
import com.wwm.expressions.LogicExpr;

public interface Queryable extends Searchable {

	// Retrieve api
	public abstract Object retrieve(Ref ref) throws ArchException;

	public abstract Map<Ref, Object> retrieve(Collection<Ref> refs) throws ArchException;

	// Querying - standard index, retrieve all
	public abstract RetrieveSpecResult retrieve(RetrieveSpec spec) throws ArchException;

	public abstract <E> E retrieve(Class<E> clazz, String keyfield, Object keyval) throws ArchException;

	public abstract <E> Collection<E> retrieveAll(Class<E> clazz, String keyfield, Object keyval) throws ArchException;

	// Querying - standard index, iterating
	public abstract <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr) throws ArchException;

	public abstract <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr, int fetchSize)
			throws ArchException;

	public abstract <E> long queryCount(Class<E> clazz, LogicExpr index, LogicExpr expr) throws ArchException;

	public abstract <E> long count(Class<E> clazz) throws ArchException;

}