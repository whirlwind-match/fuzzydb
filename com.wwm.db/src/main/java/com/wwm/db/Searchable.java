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

import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultSet;
import com.wwm.db.whirlwind.SearchSpec;

public interface Searchable {

	// Searching - whirlwind index, iterating
	<E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search)
			throws ArchException;

	<E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search, int fetchSize)
			throws ArchException;

	<E extends Object> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search);

	<E extends Object> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search, int fetchSize)
			throws ArchException;

}