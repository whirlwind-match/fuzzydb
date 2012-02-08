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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.exceptions.UnknownStoreException;
import com.wwm.db.internal.common.ServiceRegistry;

/**
 * A repository is a collection of ServerStores, for which there is the notion of a version.
 * 
 * The repository can be saved to disk and loaded back.  Repository makes no assumptions as to how or where
 * ServerStores persist their data, it just ensures that the ServerStore instance is written.
 *  
 * 
 * FIXME: 
 * - Ensure that deleted stores get deleted.  When a store is deleted, we should place
 *   a file in it's directory to indicate so.  That seems a sensible move!
 * 
 * *** DELETION OF STORES IS NOT TRANSACTION SAFE.  OVERLAPPING TRANSACTIONS WOULD EXPERIENCE PROBLEMS ***
 * For future ability to delete, a dbVersionWhenDeleted has been added to each store.
 * 
 * THINKING ALOUD:
 * Q: When a repository is synchronised, do we need to serialise the list of deleted stores?
 * A: No. We shouldn't.  Two clients have access to a store via a transaction started 
 * at db version 10222, and client A deletes the store, and commits it.  The store is 
 * not visible at version 10223.  Client B's transaction can still see the store and read data from it.
 * However, if client B tries to modify any data on the deleted store, it will fail
 * a consistency check.  The transaction will therefore not get written to the transaction log.
 * So.  When playing back transactions after a restart, the list of deleted stores should
 * always start empty.
 * However!!  We should never remove a store from deletedStoresByVersion until we have removed its'
 * persistent data.  Otherwise, the persistent data will remain on disk.
 * So.  The answer is YES.  We at least need a list of the paths of stores to be deleted.
 *
 */
public final class Repository implements Serializable {
	private static final long serialVersionUID = 1L;

	static private final Logger log = LogFactory.getLogger(Repository.class);
	
	/**
	 * StoreName to id map for current stores only 
	 */
	private final Map<String, Integer> currentStores = new HashMap<String, Integer>();

	/**
	 * Stores that have been taken offline.  The process of taking a store offline is
	 * similar to deleting a store, except the store is not deleted.  It just
	 * disappears from visibility.  It's a way of preventing the database from re-importing that store
	 * on restart.  Offline stores can then be copied and moved.
	 * TODO: This is here so that we can add this at a later date.
	 */
	@SuppressWarnings("unused") // to be implemented one day...
	private final Map<String, Integer> offlineStores = new HashMap<String, Integer>();

	
	/**
	 * Id to Store map for all stores (current and deleted - but not yet expired)
	 * For each store:
	 * - Check if in currentStores - report "found current store: x"
	 * - Check if in deletedStoresByVersion - report "found store pending delete: x"
	 * - If neither of the above - report "Importing newly discovered store: x"
	 * - Scan through currentStores to check all have been found.  Report any that have gone missing.
	 * - Record a dbVersion as the latest version across all stores (imported ones may be later).
	 * - This version should be max()'ed with eventual dbVersion, after TxLogs have all been applied!
	 * 
	 */
	private transient Map<Integer, ServerStore> idStoreMap = new HashMap<Integer, ServerStore>();

	/**
	 * deletedStoresByVersion maintains a map containing a list of ids for stores deleted at a given version.  Periodically
	 * stores older than the oldest live transaction are permanently removed.
	 * After a restart we do at least need a record of what stores have been 'deleted' but not purged, 
	 * so we do need some knowledge here.
	 */
	private final Map<Long, ArrayList<Integer>> deletedStoresByVersion = new TreeMap<Long, ArrayList<Integer>>();
	/**
	 * Also keep list of stores deleted for when loading (this could probably be done a bit better).
	 * deletedStoresByVersion is for tracking what stores are visible to current live transactions (for read
	 * operations only).  deletedStores is for knowing if a store discovered on the file system is one
	 * waiting to be deleted.
	 */
	private final Map<Integer, String> deletedStores = new HashMap<Integer, String>();
	
	private final AtomicLong version = new AtomicLong(0);
	private final AtomicInteger nextStoreId = new AtomicInteger(1);

	/** Min version that database must be at due to importing stores from a later database */
	private long minVersionAtStartup;

	
	/**
	 * Save the repository and all stores
	 */
	public void save(String dir) {
		Object o = this;
		String prefix = "r";
		
		long ver = getVersion();
		try {
			FileUtil.writeVersionedObject(o, dir, prefix, ver);

			for (ServerStore store : idStoreMap.values()) {
				store.save(ver);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to save repository to path: " + dir, e);
		}
		log.info("  saved repos at version: " + ver);
	}

	/**
	 * load the best repository found in the supplied dir
	 */
	public static Repository load(String dir)  {
		File dirFile = new File(dir);
		if (!dirFile.exists()) return null;
		
		ArrayList<File> candidates = new ArrayList<File>();
		Collections.addAll(candidates, dirFile.listFiles());

		for (;;) {
			if (candidates.size() == 0) {
				return null;
			}
		
			File file = FileUtil.removeBestCandidate(candidates);
			if (file == null) {
				return null;
			}
			
			Repository r = tryLoad(file);
			if (r != null) {
				return r;
			}
		}	
	}
	
	private static Repository tryLoad(File file) {
		Repository r = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			try {
				r = (Repository) ois.readObject();
			} finally {
				ois.close();
				fis.close();
			}
			r.tryLoadStores(file.getParentFile());
		} catch (Exception t) {
			log.error("Unexpected error loading file, " + file.getName() + " :" + t.getMessage(), t );
		}
		return r;
	}
	
	private void tryLoadStores(File reposDir) {

		idStoreMap = new HashMap<Integer, ServerStore>();
		for (File fileOrDir : reposDir.listFiles()) {
			if (!fileOrDir.isDirectory()) {
				continue;
			}
			ServerStore store = ServerStore.tryLoad(fileOrDir, version.get());
			if (store != null) {
				addFoundStore(store);
			} else {
				log.warn("Ignored store at {}, as it failed to load (see previous exceptions)", fileOrDir);
			}
		}
		
	}

	/** 
	 * Re-add existing stores.
	 * NOTE: Must re-map storeId's for new stores.
	 * We assume that a storeId/storeName combination that matches that in currentStores, is
	 * an existing local store.
	 */
	private void addFoundStore(ServerStore store) {
		Integer id = store.getStoreId();
		String storeName = store.getStoreName();
		
		log.info("Checking candidate store: {} with id: {} found at path: {}", new Object[]{storeName, id, store.getPath()});
		
		// current store if entry in for this storeName matches current id. But then should check for duplicates.
		if (currentStores.get(storeName) != null && currentStores.get(storeName).equals(id)) {
			if (idStoreMap.containsKey(id)) {
				log.warn("Skipping store {}. It is duplicate (name,id match) of a current store that's already been loaded.", store);
				return;
			}
			// otherwise add it
			log.info("Store is current and active, putting it online: " + store.toString() );
			assert( id < nextStoreId.get() );
			idStoreMap.put(id, store);
			return;
		}

		// deleted store if entry in for this storeId matches name. But then should check for duplicates.
		if (deletedStores.get(id) != null && deletedStores.get(id).equals(storeName)) {
			if (idStoreMap.containsKey(id)) {
				log.warn("Skipped duplicate (deleted) store (name and id clash with existing):" + storeName 
						+ " at path " + store.getPath() );
				return;
			}
			// otherwise add it
			log.info("Loaded current store: " + storeName + " (id = " + id + ")" );
			assert( id < nextStoreId.get() );
			idStoreMap.put(id, store);
			return;
		}
		
		// If we get here, we've got a newly discovered store.  
		// - Assign the store a NEW storeId
		// - Deal with latest dbVersion issues on this store.
		if (!currentStores.containsKey(storeName)) {
			log.warn("Ignored store: " + store + ".  Importing is currently not supported.");
//			log.info("Found new store: " + store + ".  Importing with new id: " + nextStoreId);
//			id = nextStoreId.getAndIncrement();
//			store.setStoreId(id);
//			idStoreMap.put(id, store);
//			currentStores.put(storeName, id);
//			if (store.getSavedDbVersion() > version.get()) { // record if we need to advance version after txLogs...
//				minVersionAtStartup = Math.max(minVersionAtStartup, store.getSavedDbVersion());
//			}
		}

	}

	/**
	 * Completely initialise transient data throughout repository
	 */
	public void initTransientData() {
		
		for (ServerStore store : idStoreMap.values()) {
			store.initTransientData();
		}
		synchronized (deletedStoresByVersion) {
			for (ArrayList<Integer> als : deletedStoresByVersion.values()) {
				for (Integer storeId : als) {
					ServerStore store = idStoreMap.get(storeId);
					store.initTransientData();
				}
			}
		}
	}

	public void upissue() {
		version.incrementAndGet();
	}

	public  long getVersion() {
		return version.get();
	}
	
	/**Create a new store.
	 * The store must not already exist.
	 * The transaction must be in its commit phase (and thus be holding write privilege)
	 */
	public synchronized int createStore(String storeName) {
		assert(CurrentTransactionHolder.isInCommitPhase());
		assert(!currentStores.containsKey(storeName));
		assert(!idStoreMap.containsKey(nextStoreId));
		
		String rootPath = ServiceRegistry.getService(ServerSetupProvider.class).getReposDiskRoot();
		ServerStore store = new ServerStore(rootPath, storeName, nextStoreId.get());
		currentStores.put(storeName, nextStoreId.get());
		idStoreMap.put(nextStoreId.get(), store);
		store.initTransientData();
		return nextStoreId.getAndIncrement();
	}

	/**Delete a store.
	 * The store must exist.
	 * The transaction must be in its commit phase (and thus be holding write priviledge)
	 */
	public synchronized void deleteStore(String storeName) {
		assert(CurrentTransactionHolder.isInCommitPhase());
		assert(currentStores.containsKey(storeName));
		Integer storeId = currentStores.get(storeName);
		assert(storeId != null);

		currentStores.remove(storeName);
		long commitVersion = CurrentTransactionHolder.getCommitVersion();
		
		synchronized (deletedStoresByVersion) {
			ArrayList<Integer> al = deletedStoresByVersion.get(commitVersion);
			if (al == null) {
				al = new ArrayList<Integer>();
				deletedStoresByVersion.put(commitVersion, al);
			}
			al.add(storeId);
		}
		synchronized (deletedStores) {
			deletedStores.put(storeId, storeName);
		}
	}
	
	public synchronized ServerStore getStore(String storeName) {
		Integer storeId = currentStores.get(storeName);
		if (storeId == null) {
			throw new UnknownStoreException("Unknown store: " + storeName);
		}
		ServerStore store = idStoreMap.get(storeId);
		if (store == null) {
			throw new ArchException("Inconsistency in Store maps");
		}
		return store;
	}
	
	public synchronized ServerStore getStore(int storeId) {
		ServerStore store = idStoreMap.get(storeId);
		if (store == null) {
			throw new UnknownStoreException("storeId: " + storeId + " (Store deleted?)");
		}
		return store;
	}

	/**
	 * Purges deleted stores according to whether a live transaction may still be able to access them.
	 * Any stores deleted prior to the oldest transaction are removed from the repository,
	 * and their persistent data is deleted.
	 * 
	 * Deleting an expired store only needs to block access to deletedStoresByVersion while assessing which are 'ripe'. 
	 * 
	 * @param oldestTransaction - the oldest live transaction, prior to which a deleted store is unreachable
	 */
	public void purgeDeletedStores(long oldestTransaction) {
		
		// For building a list of deleted stores that have now expired.  We build this list so that
		// we can remove them from deletedStoresByVersion in a quick blocking operation.
		ArrayList<ServerStore> expiredStores = new ArrayList<ServerStore>();
		
		synchronized (deletedStoresByVersion) {
			Set<Entry<Long, ArrayList<Integer>>> liveSet = deletedStoresByVersion.entrySet();
			Iterator<Entry<Long, ArrayList<Integer>>> i = liveSet.iterator();
			
			while (i.hasNext()) {
				Entry<Long, ArrayList<Integer>> versionEntry = i.next();
				if (versionEntry.getKey() > oldestTransaction) {
					continue; // these are still live
				}

				// These are expired so add the to our expiredList, and remove from here.
				for (Integer id : versionEntry.getValue()) {
					expiredStores.add(idStoreMap.get(id));
					idStoreMap.remove(id);
				}
				i.remove(); // removes entire array.
			}
		}

		// Now do the work that might take a little time without blocking 
		// (we're only working with thread local data now)
		for (ServerStore s : expiredStores) {
			s.initTransientData(); // have to be initialised
			boolean success = s.deletePersistentData();
			if (!success) {
				log.error("Failed to delete persistent data for store at " + s.getPath() );
			}
		}
		
		// Now we've deleted the data, we can remove the rest (could actually be done in either order
		// .. that is, once (FIXME) we've ensured a save cannot take place during this purge (they should be in
		// the same thread.
		synchronized (deletedStores) {
			for (ServerStore store : expiredStores) {
				deletedStores.remove(store.getStoreId());
			}
		}
	}
	
	/**
	 * This gets called during startup, after all transaction logs have been applied, and before
	 * the server is 'available'.
	 * 
	 * All imported databases should be 'pending' until this point.  For now, it just upissues the database to 
	 * include the db versions of those stores that were imported.
	 */
	void applyImports() {
		version.set(Math.max(version.get(), minVersionAtStartup));
		purgeDeletedStores(version.get()); // we can now do this, as all tx's have run.
	}
}
