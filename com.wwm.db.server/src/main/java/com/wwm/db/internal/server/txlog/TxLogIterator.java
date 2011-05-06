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
package com.wwm.db.internal.server.txlog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Class responsible for allowing a directory of files to be iterated over
 */
public class TxLogIterator implements Iterable<File> {

	private File txDir;
	private long startVersion;
	private boolean allowNonExact;

	private class IteratorImpl implements Iterator<File> {
		ArrayList<File> candidates;

		/** Version at which the tx log is starting playback */
		private long highestDbVer = -1;

		public IteratorImpl() {
			candidates = new ArrayList<File>();
			Collections.addAll(candidates, txDir.listFiles());
		}

		
		/**
		 * Note: May say has next when it's not exactly sure.  next() may return null in those circumstances
		 */
		public boolean hasNext() {
			return candidates.size() > 0;
		}

		public File next() {
			if (candidates.size() == 0) return null;
			
			File file = removeBestCandidate(candidates);

			return file;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		
		/**
		 * On txLog playback, we want to apply transaction logs that include the transactions from the
		 * version of the currently loaded repository, forwards.
		 * 
		 * Algo: 
		 *  - keep track of highest version that is lower than current db version (as that might be the one)
		 *  - if version == dbversion, return it, it's exact match (what we normally expect)
		 *  - if version < dbversion but not greater than highestDbVer, 
		 *  		then remove it (as all it's transactions are in repos or have been applied)
		 *  - if version < dbversion and > highestDbVer, update highestDbVer, and remember what file it is
		 *  After have been through all, return the best found
		 */
		private File removeBestCandidate(ArrayList<File> candidates) {
			File best = null;
			Iterator<File> i = candidates.iterator();
			while (i.hasNext()) {
				File file = i.next();
				long version = getDbVerFromFilename(file);
				if (version == -1) {
					continue; // skip dirs and files we don't care about
				}
				
				if (version == startVersion) {
					i.remove();
					highestDbVer = version;
					return file;
				} else if (version < startVersion) {
					// Mod by neale: original didn't return anything if there wasn't one matching the repos.  This way we do find a version
					// but then rely on playback to skip transactions until at the correct point
					if (version > highestDbVer && allowNonExact){
						highestDbVer = version;
						best = file;
					} 
				} else {
					i.remove(); // remove anything not high enough
				}
			}
			return best;
		}
	}

	/**
	 * Abstraction of a collection of Files where we're interested in iterating over those with a version 
	 * from currentVersion onwards.
	 * NOTE: When iterating, do check for iterator.next() == null, as the algo does allow that to happen.
	 * @param txDir
	 * @param startVersion
	 * @param allowNonExactFirstFile true if we can start with a version that is < startVersion, if we can't find an exact match
	 */
	public TxLogIterator(File txDir, long startVersion, boolean allowNonExactFirstFile) {
		this.txDir = txDir;
		this.startVersion = startVersion;
		this.allowNonExact = allowNonExactFirstFile;
	}

	public Iterator<File> iterator() {
		return new IteratorImpl();
	}

	/**
	 * Extract first digits from between initial 't' and '_'. e.g. 123 from filename "t123_dontcare".
	 * @return long 
	 */
	static private long getDbVerFromFilename(File file) {
		if ( file.isDirectory() ) {
			return -1;
		}
		String filename = file.getName();
		int index = filename.indexOf('_');
		if (index < 2 || !filename.startsWith("t")) {
			return -1; // some random file we don't want
		}

		try {
			return Long.parseLong(filename.substring(1, index));
		} catch (NumberFormatException e) { 
			return -1; // do nothing: we return -1 below.
		}
	}
	
}
