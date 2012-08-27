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

import org.fuzzydb.server.internal.table.UserTable;



/**
 * Not used
 */
@Deprecated
public class LockedPages {

	static private class LockedSet {
		private final UserTable<?> table;

		private final HashSet<Long> elements;

		public LockedSet(final UserTable<?> table, final HashSet<Long> elements) {
			super();
			this.table = table;
			this.elements = elements;
		}

		public HashSet<Long> getElements() {
			return elements;
		}

		public UserTable<?> getTable() {
			return table;
		}

	}

	private HashMap<Thread, LockedSet> readLocks = new HashMap<Thread, LockedSet>();

	private Thread writeLockedThread = null;

	private LockedSet writeLockedSet = null;

	public synchronized void lockForRead(Thread thread, UserTable<?> table, HashSet<Long> elements) {
		assert (!readLocks.containsKey(thread));
		assert (writeLockedThread != thread);
		readLocks.put(thread, new LockedSet(table, elements));
	}

	public synchronized void unlockForRead(Thread thread, UserTable<?> table, HashSet<Long> elements) {
		assert (readLocks.containsKey(thread));
		assert (writeLockedThread != thread);
		LockedSet set = readLocks.remove(thread);
		assert (set.getTable() == table);
		assert (set.getElements().equals(elements));
	}

	public synchronized void lockForWrite(Thread thread, UserTable<?> table, HashSet<Long> elements) {
		assert (!readLocks.containsKey(thread));
		assert (writeLockedThread == null);
		assert (writeLockedSet == null);
		writeLockedThread = thread;
		writeLockedSet = new LockedSet(table, elements);
	}

	public synchronized void unlockForWrite(Thread thread, UserTable<?> table, HashSet<Long> elements) {
		assert (!readLocks.containsKey(thread));
		assert (writeLockedThread == thread);
		assert (writeLockedSet.getElements().equals(elements));
		assert (writeLockedSet.getTable() == table);
		writeLockedThread = null;
		writeLockedSet = null;
	}

}
