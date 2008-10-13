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
package com.wwm.db.dao;


/**
 * DAO that defaults to a store "com.wwm.stats"
 */
public class StatsStoreDao extends Db2ObjectDAO {

	private static final String storeUrl = "wwmdb:/com.wwm.stats";	// Database store name for access counter

	public StatsStoreDao() {
		super( storeUrl );
	}
	
	
}
