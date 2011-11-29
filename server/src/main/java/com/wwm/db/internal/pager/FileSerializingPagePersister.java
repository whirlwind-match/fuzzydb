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
package com.wwm.db.internal.pager;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.wwm.db.internal.pager.Page.PagePurgedException;
import com.wwm.db.internal.server.Database;

/**
 * A backing store for {@link Page}s that manages:
 * - Persistence of pages by storing them to disk
 * - Memory availability, by allowing pages to be swapped in and out of memory
 *
 * Note: Alternative implementations of these responsibilities could use distributed cache systems, 
 * such as Terracotta, to allow the pages to be distributed between servers, rather than having to
 * be cached in and out from disk.
 * @author Adrian Clarkson
 * @author Neale Upstone (debugging and documentation reverse-engineering)
 * 
 */
public class FileSerializingPagePersister implements PagePersister, PagerMBean {

	private static final int MIN_PAGES_LOADED = 400;

	private class PurgeList {
		
		private static final int REBUILD_INTERVAL = 100;

		private volatile PageOutCandidate[] purgeList; // sorted array of pages to purge

		private long lastBuiltPurgeList = 0;

		private volatile int purgeListIndex = 0;
		private float lowestPurgeCost = 0;

		private long totalScoreTime = 0;

		void updatePurgeList() {
			// Only update if rebuild interval has passed, 
			// and we've progressed through 10% of previous built list,
			// and next item has a cost < double the lowest
			if (System.currentTimeMillis() - lastBuiltPurgeList <= REBUILD_INTERVAL
					&& purgeListIndex <= purgeList.length / 10
					&& purgeList[purgeListIndex].cost <= 2*lowestPurgeCost) {
				return;
			}
			
			if (loadedPages == 0) return;
			
			long start = System.currentTimeMillis();

			assert (Thread.holdsLock(pages));
			purgeList = new PageOutCandidate[loadedPages];
			int scoreIndex = 0;
			// TreeMap<Float, ArrayList<PageTableId>> scores = new TreeMap<Float,
			// ArrayList<PageTableId>>();
			for (Map.Entry<PersistentPagedObject, HashSet<Long>> entries : pages.entrySet()) {
				PersistentPagedObject pageTable = entries.getKey();
				HashSet<Long> pageIds = entries.getValue();
				
				// INSERTED BY NEALE: 9May08 to deal with failure to flush old versions while in memory
				// FIXME: Need to confirm that flushOldVersions works mid-transaction - it should do 
				try {
					pageTable.flushOldVersions( pageIds );
				} catch (PagePurgedException e) {
					throw new RuntimeException(e);
				}
				
				for (Long pageId : pageIds) {
					float cost = pageTable.calculatePurgeCost(pageId);
					// ArrayList<PageTableId> al = scores.get(score);
					// if (al == null) {
					// al = new ArrayList<PageTableId>();
					// scores.put(score, al);
					// }
					// al.add(new PageTableId(pageTable, pageId));
					purgeList[scoreIndex++] = new PageOutCandidate(cost, pageTable, pageId);
				}
			}

			Arrays.sort(purgeList);
			purgeListIndex = 0;
			lowestPurgeCost = purgeList[0].cost;
			lastBuiltPurgeList = System.currentTimeMillis();

			long duration = System.currentTimeMillis() - start;
			totalScoreTime += duration;
		}

		/**
		 * @return details for next page to purge, or null if there's nothing available to purge
		 */
		PageOutCandidate getNextPurgeItem() {
			if (purgeList == null || loadedPages == 0) {
				return null; // If we start up with low memory (try -Xmx=30M, this can happen. TODO. extract purgeList
				// and operations such as getNextPurgeItem to another class we can test and see!
			}
			
			// If we've reached the end of the current list, create a new one
			if (purgeListIndex == purgeList.length) {
				updatePurgeList();
			}
		
			// still need to guard against nothing to purge
			if (purgeListIndex >= purgeList.length) return null;
			
			return purgeList[purgeListIndex++]; // FIXME: AIOOBE here from lockElementForRead.  No multi-thread... seems to be ref change
			/* Above issue caused running performance.TestReadWritePerf.testCreateManyAndUpdate	on Mac */
		}

	}

	private final PurgeList purgeListObject = new PurgeList();
	
	private int minPagesPerPurge = 20;

	private int purgeInterval = 3; // millisecs;

	public final Database database;

	private long lastPurgeTime = System.currentTimeMillis();

	private final HashMap<PersistentPagedObject, HashSet<Long>> pages = new HashMap<PersistentPagedObject, HashSet<Long>>();

	private int loadedPages = 0;

	private int outstandingPurges = 0;

	private final MemoryAdvisor memoryAdvisor = new MemoryAdvisor( 10f, 12.5f, 15f );
	
	private final Semaphore purgeLock = new Semaphore(1);



	
	public FileSerializingPagePersister(Database database) {
		this.database = database;
		
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
        
        ObjectName name;
		try {
			name = new ObjectName("com.wwm.db.core:type=Pager");
			mbs.registerMBean(this, name); 
		} catch (Throwable e) {
			System.out.println( "Exception registering Pager MBean.  Continuing without JMX support" );
		}

	}

	public Database getDatabase() {
		return database;
	}

	public String getPath() {
		return database.getSetup().getReposDiskRoot() + File.separator;
	}

	public void addPurgeablePage(PersistentPagedObject pageTable, long pageId) {
		synchronized (pages) {
			HashSet<Long> set = pages.get(pageTable);
			if (set == null) {
				set = new HashSet<Long>();
				pages.put(pageTable, set);
			}
			assert(!set.contains(pageId));
			set.add(pageId);
			notifyPageLoaded();
		}
	}

	public void saveAll() {
		purgeLock.acquireUninterruptibly(); // 
		try {
			synchronized (pages) {
				for (Map.Entry<PersistentPagedObject, HashSet<Long>> entry : pages.entrySet()) {
					HashSet<Long> set = entry.getValue();
					for (Long pageId : set) {
							entry.getKey().savePage(pageId);
					}
				}
			}
		} catch (PagePurgedException e) {
			throw new RuntimeException("Didn't expect: " + e.getMessage(), e);
		} finally {
			purgeLock.release();
		}
	}

	/**
	 * NOTE: This should be called on a Store by Store basis within Store.load()
	 * Clean up flushed pages to remove all that are newer than the specified repository
	 * revision.  This gives us a consistent state at dbversion that we can then
	 * roll-forward transaction logs against.
	 * Should delete all files prefixed 'p' that are newer than the specified database revision.
	 * @param dbversion
	 */
	public void deleteNewerPages(long dbversion) {
		File root = new File(getPath());
		recursiveDeleteNewerPages(root, dbversion);
	}

	static public void recursiveDeleteNewerPages(File dir, long dbVersion) {
		if (!dir.exists())
			return;
		assert (dir.isDirectory());
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					recursiveDeleteNewerPages(file, dbVersion);
				} else {
					deletePageFile(dbVersion, file);
				}
			}
		}
		// Never delete directories. This seems to be dangerously asynchronous - the deletion can actually happen sometime later
		// and anything created new under the directory also vanishes!
//		files = dir.listFiles();
//		if (files == null || files.length == 0) {
//			dir.delete();
//		}
	}

	static private void deletePageFile(long dbVersion, File file) throws Error {
		String fileName = file.getName();
		if (fileName.startsWith("p")) {
			int index = fileName.lastIndexOf('_');
			if (index > 0) {
				String versionString = fileName.substring(index + 1);
				try {
					int version = Integer.parseInt(versionString);
					boolean deleted = false;
					SecurityException deleteFailReason = null;
					if (version > dbVersion) {
						try {
							deleted = file.delete();
						} catch (SecurityException e) {
							deleteFailReason = e;
						}
						if (!deleted) {
							throw new RuntimeException("Unable to delete page file " + fileName, deleteFailReason);
						}
					}
				} catch (NumberFormatException e) { 
					throw new RuntimeException("Failure extracting version from filename:" + fileName, e);
				}
			}
		}
	}

	public boolean deleteFromDisk(PersistentPagedObject table) {
		synchronized (pages) {
			HashSet<Long> ids = pages.remove(table);
			if (ids != null) {
				loadedPages -= ids.size();
			}
		}
		return table.deleteFromStorage();
	}

	private void notifyPageLoaded() {
		assert (Thread.holdsLock(pages));
		loadedPages++;
	}

	
	/**
	 * Done on maintenance basis
	 */
	public void doMaintenance(){
			// REVIEW: flushOldVersions(); // Probably no need as this now gets done when database does sync and on updatePurge list
	}
	
	
	@SuppressWarnings("unused") // this is now done during updatePurgeList()
	private void flushOldVersions() {

		synchronized (pages) {

			assert (Thread.holdsLock(pages));

			for (Map.Entry<PersistentPagedObject, HashSet<Long>> entries : pages.entrySet()) {
				PersistentPagedObject pageTable = entries.getKey();
				HashSet<Long> pageIds = entries.getValue();
				
				try {
					pageTable.flushOldVersions( pageIds );
				} catch (PagePurgedException e) {
					throw new RuntimeException(e);
				}
			}		
		}
	}

	

	/**
	 * Make an effort to create room for the number of requested pages.
	 * @param pagesNeeded
	 * 
	 * Algorithm:
	 * - Assume we've enough room, whenever free memory is above high water mark
	 * - 
	 */
	public void ensureCapacity(int pagesNeeded) {
		purgeLock.acquireUninterruptibly(); // ensure nothing else is in here
		
		try {
			memoryAdvisor.update();
			if ( memoryAdvisor.isAboveHigh() ){
				outstandingPurges = 0;
				return; // if freeMem is above high water mark, we don't need to purge any
			}
			
			// If we do need to purge
			outstandingPurges += pagesNeeded;
			
			if ( isPurgeTooRecent() ) {
				return;  // keep collecting them up if we did one recently.
			}
			System.err.println("Need memory: " + memoryAdvisor.toString());
			lastPurgeTime = System.currentTimeMillis();

			@SuppressWarnings("unused")
			boolean forceGc = memoryAdvisor.isMemoryLow();
			
			if (outstandingPurges < minPagesPerPurge){
				outstandingPurges = minPagesPerPurge;
			}
			
			// FIXME: This is crude. We need to replace this with an adaptive
			// loadedPagesTarget
			if (memoryAdvisor.isMemoryLow()){
				outstandingPurges += 10;
			}
			
			synchronized (pages) {
				purgeListObject.updatePurgeList();

				if ( !purgeOutstandingPages()) {
					forceGc = true;
				}
			}
//			if (forceGc) {
//				System.out.println("Pager forcing GC");
//				System.gc();
//			}
		} finally {
			purgeLock.release();
		}
	}

	/**
	 * 
	 * @param outstandingPurges
	 * @return succeeded in purging the required number of pages
	 */
	private boolean purgeOutstandingPages() {
		// assume GC is needed if this happened
		if (loadedPages < MIN_PAGES_LOADED){
			outstandingPurges = 0;
			return false; 
		}
		
		System.out.println("Purging " + outstandingPurges + " pages. " + loadedPages + " pages current loaded. (" + pages.size() + ")");
		StringBuilder purgeScores = new StringBuilder("Scores: ");
		for (; outstandingPurges > 0; outstandingPurges--) {
			PageOutCandidate candidate = purgeListObject.getNextPurgeItem();
			if (candidate == null){
				outstandingPurges--;// = 0; // EXPERIMENT: Outstanding otherwise keeps growing
				return false;
			}
			PersistentPagedObject pageTable = candidate.pageTable;
			long pageId = candidate.pageId;
			try {
				HashSet<Long> ids = pages.get(pageTable);
				if (ids != null && ids.contains(pageId)) {	// make sure it's enqueued, deleted stores can cause pages to hang around
					if (pageTable.tryPurgePage(pageId)) {
						purgeScores.append( candidate.cost ).append(", ");
						boolean success = ids.remove(pageId);
						assert(success);
						if (ids.size() == 0) {
							pages.remove(pageTable);
						}
						loadedPages--;
					} else {
						assert false : "We expect the page to be in memory if it is queued to be paged";
					}
				}
			} catch (PagePurgedException e) { 
				throw new RuntimeException(e); // FIXME: Document this exception / establish if this is possible or recoverable
			} 
		}
		System.out.println(purgeScores);
		if (outstandingPurges > 0) {
			System.err.println("** Still had " + outstandingPurges + "page(s) still to purge. What do we do");
			return false;
		}
		return true;
	}

	/**
	 * Checks if we've purged too recently to want to do it again.
	 * If a purge is now due, update the last purge time, i.e. we assume that a purge now takes place.
	 * @return
	 */
	private boolean isPurgeTooRecent() {
		// Purge max once per purgeInterval msecs
		if (System.currentTimeMillis() - lastPurgeTime < purgeInterval) {
			return true;
		}
		return false;
	}

	public Date getLastPurgeTime() {
		return new Date(lastPurgeTime);
	}
	
	public long getTotalScoreTime() {
		return purgeListObject.totalScoreTime;
	}

	public int getMinPagesPerPurge() {
		return minPagesPerPurge;
	}

	public int getOutstandingPurges() {
		return outstandingPurges;
	}
	public int getLoadedPages() {
		return loadedPages;
	}
	public int getPurgeInterval() {
		return purgeInterval;
	}
	public void setMinPagesPerPurge(int pages) {
		minPagesPerPurge = pages;
	}
	
	public void setPurgeInterval(int millisecs) {
		purgeInterval = millisecs;
	}
}
