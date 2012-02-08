package com.wwm.db.internal.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wwm.db.internal.pager.PagePersister;
import com.wwm.db.internal.server.txlog.TxLogSink;

@Singleton
public class FileRepositoryStorageManager implements RepositoryStorageManager {

	private final long syncPeriod = 5*60*1000L; // every 5 mins

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final PagePersister pager;
	
	private final ServerSetupProvider setup;
	
	private final TxLogSink txLog;
	
	private volatile Repository repository;
	
	private long latestDiskVersion;
	
    private long lastSync = 0;


	
	@Inject
	public FileRepositoryStorageManager(ServerSetupProvider setup, PagePersister pager, TxLogSink txLog) {
		this.setup = setup;
		this.pager = pager;
		this.txLog = txLog;
	}
	
	public void doMaintenance() {
        // delete stores
        repository.purgeDeletedStores();
        pager.doMaintenance();
        doSync();
	}
	
	public Repository getRepository() {
		return repository;
	}
	
	public void loadOrCreateRepositoryAsNeeded() {
		// See if we have a repository to load, and use it if found
		repository = Repository.load(setup.getReposDiskRoot());
		if (repository == null) {
			log.info("No repository found. Saving a blank & retrying");
			new Repository().save(setup.getReposDiskRoot());
			repository = Repository.load(setup.getReposDiskRoot());
		}

		latestDiskVersion = repository.getVersion();
		log.info("Loaded repository, version = " + latestDiskVersion);
	}

	public void shutdown() {
        pager.saveAll();

        repository.purgeDeletedStores();

        if (latestDiskVersion != repository.getVersion()) {
            repository.save(setup.getReposDiskRoot());
        }
	}
	
    /**
     * Sync to disk on a periodic basis.
     * NOTE: Initial sync is done on first maintenance call, as this would be just after having read transaction logs.
     * 
     * (Different) Sync procedure:
     * 
     * Bail out early if loadedVersion == current db version (ie disk is clean)
     * Aquire exclusive sync lock
     * Aquire write lock (beginning 'quick' part of sync) - this stops the db version getting incremented
     * get db version number, this is the number we will sync to
     * force tx log writers to start a new file, so the log start is aligned with the repos we will write
     * deep clone the repos to avoid seeing schema changes (or just serialize the main one to a buffer? easier! But don't put it on disk)
     * set the pager to 'retro' mode and supply the sync version. This makes sure pages purged under memory load also get saved for the sync version, otherwise will will miss them out.
     * Release write lock (beginning 'slow' part of sync) - the db version can start to move forward again
     * Make pager sync all dirty pages to disk at the specified version. Use suffixes in case of disk name collision. Must always avoid overwriting a page as this could damage a previously synced version if we crash while writing.
     * Take the pager out of 'retro' mode.
     * Write the cloned/buffered repos
     * Update loadedVersion to synced version (maybe change the name of this field.)
     * Release exclusive sync lock
     * 
     * NEALE's NOTES 2008
     * - Simple first attempt (prob similar to above but I haven't looked yet):  
     * NOTE dbVersion. 
     * Sync all pages (must be atomic write of unwritten pages, and modified pages) 
     * which will all be dbVersion
     * Save a 'repos' object with noted version
     * Rollover to a new txLog, so we've always got a new one ready
     */
    private void doSync() {

        long now = System.currentTimeMillis();
		
		if (now - lastSync > syncPeriod) {
		
			// FIXME: (nu->ac) - This probably needs synchronising, or doing in a blocking thread...?
			// Can you take a look.
			log.trace("Syncing...");
			pager.saveAll();
			log.trace(".. saved pages.. ");
		
			long dbVersion = repository.getVersion();
			if (latestDiskVersion != dbVersion) {
				repository.save(setup.getReposDiskRoot());
				txLog.rolloverToNewLog(dbVersion); // TODO: Not sure if this is fully robust. May need some review to check the dbVersion is always correct
				latestDiskVersion = dbVersion;
			}
			log.trace(".. completed sync.");
		
			lastSync = System.currentTimeMillis(); // Ensure we get gap between syncs
		}
    }


}
