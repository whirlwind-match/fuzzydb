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
package org.fuzzydb.client;

import java.util.Collection;
import java.util.Map;

import org.fuzzydb.core.exceptions.ArchException;
import org.fuzzydb.core.query.ResultSet;
import org.fuzzydb.core.query.RetrieveSpec;
import org.fuzzydb.core.query.RetrieveSpecResult;

import com.wwm.expressions.LogicExpr;

public interface Queryable extends Searchable {

	// Retrieve api
	Object retrieve(Ref ref);

	Map<Ref, Object> retrieve(Collection<Ref> refs);

	// Querying - standard index, retrieve all
	RetrieveSpecResult retrieve(RetrieveSpec spec);

	<E> E retrieve(Class<E> clazz, String keyfield, Object keyval);

	<E> Collection<E> retrieveAll(Class<E> clazz, String keyfield, Object keyval);

	// Querying - standard index, iterating
	<E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr);

	<E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr, int fetchSize)
			throws ArchException;

	<E> long queryCount(Class<E> clazz, LogicExpr index, LogicExpr expr);

	<E> long count(Class<E> clazz);

}