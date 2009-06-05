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

import com.wwm.db.internal.pager.Pager;

/**
 * Holder for data that is passed through the initialisation tree to 
 * allow components to extract the information they need
 */
public class InitialisationContext {
	// NOTE: All must be final
	public final Database database;
	public final Pager pager;
	
	public InitialisationContext(Database database) {
		this.database = database;
		this.pager = database.getPager();
	}
}
