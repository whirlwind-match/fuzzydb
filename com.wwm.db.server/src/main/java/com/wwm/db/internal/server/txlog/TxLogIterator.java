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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Class responsible for allowing a directory of files to be iterated over
 */
public class TxLogIterator implements Iterable<File> {

	private final File txDir;
	private final long startVersion;
	private final boolean allowNonExact;

	
	private class IteratorImpl implements Iterator<File> {
		private final ArrayList<File> ascendingCandidates = new ArrayList<File>();

		/** Version at which the tx log is starting playback */
		private long highestVersionApplied = -1;

		public IteratorImpl() {
			FileFilter fileFilter = new FileFilter() {
				
				public boolean accept(File file) {
					if (file.isDirectory()){
						return false;
					}
					
					String filename = file.getName();
					int index = filename.indexOf('_');
					return (index >= 2 && filename.startsWith("t"));
				}
			};
			Collections.addAll(ascendingCandidates, txDir.listFiles(fileFilter));
			Collections.sort(ascendingCandidates, TransactionFileNameComparator.getInstance());
		}

		
		/**
		 * Note: May say has next when it's not exactly sure.  next() may return null in those circumstances
		 */
		public boolean hasNext() {
			return ascendingCandidates.size() > 0;
		}

		public File next() {
			if (ascendingCandidates.size() == 0) return null;
			
			File file = removeBestCandidate(ascendingCandidates);

			return file;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		
		/**
		 * On txLog playback, we want to apply transaction logs that include the transactions from the
		 * version of the currently loaded repository, forwards.
		 * 
		 * OLD! Algo: 
		 *  - keep track of highest version that is lower than current db version (as that might be the one)
		 *  - if version == dbversion, return it, it's exact match (what we normally expect)
		 *  - if version < dbversion but not greater than highestDbVer, 
		 *  		then remove it (as all it's transactions are in repos or have been applied)
		 *  - if version < dbversion and > highestDbVer, update highestDbVer, and remember what file it is
		 *  After have been through all, return the best found
		 *  
		 *  NEW: If we find startVersion, then remove it and return it
		 */
		private File removeBestCandidate(ArrayList<File> candidates) {
			
			for (Iterator<File> iterator = candidates.iterator(); iterator.hasNext();) {
				File file = iterator.next();
				
				long version = TransactionFileNameComparator.getDbVerFromFilename(file);
				if (version == -1 || version < highestVersionApplied) {
					iterator.remove();
					continue; // skip if uninteresting or not high enough 
				}
				
				// IF we have a match for our start point, then apply this transaction log
				// and record 
				if (version == startVersion) {
					iterator.remove();
					highestVersionApplied = version;
					return file;
				}
				
				// If we've applied something, then next one higher is our next
				if (highestVersionApplied >= 0 && version > highestVersionApplied) {
					iterator.remove();
					highestVersionApplied = version;
					return file;
				}
				iterator.remove();
			}
			// If all transaction logs are old, then we can just start
			return null;
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
	
}
