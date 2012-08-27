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
package org.fuzzydb.server.internal.pager;

import java.util.HashMap;
import java.util.HashSet;

import org.fuzzydb.server.internal.table.UserTableImpl;



/**
 * Not used
 * 
 * @author ac
 * 
 */
@Deprecated
public class LockedElements {
	private HashMap<UserTableImpl<?>, HashSet<Element<?>>> locks = new HashMap<UserTableImpl<?>, HashSet<Element<?>>>();

	/*******************************************************************************************************************
	 * 
	 * @param pageTable
	 * @param element
	 * @return true if this was a new page lock
	 */
	public boolean addLock(UserTableImpl<?> pageTable, ElementReadOnly<?> element) {
		HashSet<Element<?>> set = locks.get(pageTable);
		if (set == null) {
			set = new HashSet<Element<?>>();
			locks.put(pageTable, set);
		}

		return false;
	}

}
