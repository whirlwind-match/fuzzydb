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

import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.db.core.UncaughtExceptionLogger;
import com.wwm.db.internal.pager.NullPersister;
import com.wwm.db.internal.pager.FileSerializingPagePersister;
import com.wwm.db.internal.pager.PagePersister;
import com.wwm.db.internal.server.txlog.NullTxLogWriter;
import com.wwm.db.internal.server.txlog.TxLogPlayback;
import com.wwm.db.internal.server.txlog.TxLogSink;
import com.wwm.db.internal.server.txlog.TxLogWriter;
import com.wwm.db.services.IndexImplementationsService;
import com.wwm.io.core.ClassDefinitionRepositoryAware;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.MessageSource;
import com.wwm.io.core.impl.DummyCli;

/**
 * TODO: (nu, 8Mar08) I believe Pager should be replaced with a an abstraction of the backing-store, as this should
 * be able to be the Db1 style store (read into memory) or a distributed in memory paged store etc.
 * 
 * 
 */
public final class Database implements DatabaseVersionState {

    static private Logger log = LogFactory.getLogger(Database.class);

    // Ensure we log all uncaught exceptions for whole server.
    static { UncaughtExceptionLogger.initialise(); }
    
    private final MessageSource messageSource;
    
    private final DummyCli cli = new DummyCli();
    private ServerTransactionCoordinator transactionCoordinator;
    private CommandProcessingPool commandProcessor;
    private Repository repository;
    private final ServerSetupProvider setup = new ServerSetupProvider();
    private final PagePersister pager;
    private long latestDiskVersion = -1;
    private final TxLogSink txLog;
    private final Semaphore shutdownFlag = new Semaphore(0);
    private boolean closed = false;
    
    /**
     * True if changes should be written to disk (if false, it is still allowable to read, 
     * such that start is always from a given state)
     */
    private final boolean persistChanges; 

    private MaintThread maintThread;

    private long lastSync = 0;
    private final long syncPeriod = 5*60*1000L; // every 5 mins
	private IndexImplementationsService indexImplsService;

    private class MaintThread extends WorkerThread {
        private boolean closing = false;

        // set up to work with this Db instance as our thread manager
        MaintThread( Database manager ){
        	super("Maint Thread", commandProcessor);
        }
        
        @Override
        public void run() {
            for (;;) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                	if (!closing) {
                		e.printStackTrace(); // interrupt is unexpected if not closing.
                	}
            	}
                if (closing) {
                    return;
                }
                
                // Lock out other threads, and then do maintenance without risk of overlap
                acquireExclusivity(); 
                performMaintenence();
                releaseExclusivity();
            }
        }


        public void shutdown() {
            closing  = true;
            interrupt();
            while (isAlive()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) { e.printStackTrace(); } // FIXME: Document this exception
            }
        	log.info("MaintThread finished");
        }

    }

    
    
    /**
     * Create a new database ready to process requests from this message source
     * 
     * @param isPersistent true if we page to disk and write use a tx log (TODO probably ought to be a persistence strategy)
     */
    public Database(MessageSource messageSource, boolean isPersistent) {
    	this.messageSource = messageSource;
    	this.pager = isPersistent ? new FileSerializingPagePersister(this) : new NullPersister(this);
    	
    	this.persistChanges = isPersistent; // TODO: Allow read and write persistence to be separate 
    	// txLog on single core activity seems to be 10-20% (see SimpleIndex test logs when this true/false)
    	// for heavily loaded server, this may bottleneck more.
    	
        txLog = persistChanges ? new TxLogWriter(setup.getTxDiskRoot(), cli) : new NullTxLogWriter();
    }


    /**
     * Allows database to be started having been configured as a 'bean' with the serverAddress and serverPort
     * specified.
     * @throws IOException
     */
	public void startServer() throws IOException {
		
		if (messageSource instanceof ClassDefinitionRepositoryAware){
			((ClassDefinitionRepositoryAware) messageSource).setCli(cli);
		}
			
        // load latest valid repository
        log.info("========== Starting Server ==========");
        Repository loaded = Repository.load(setup.getReposDiskRoot());
        if (loaded != null) {
        	repository = loaded;
            latestDiskVersion = getCurrentDbVersion();
            log.info("Loaded repository, version = " + latestDiskVersion);
        } else {
            log.info("No repository found. Saving a blank & retrying");
        	repository = new Repository();
        	repository.save(setup.getReposDiskRoot());
        	repository = Repository.load(setup.getReposDiskRoot());
            latestDiskVersion = getCurrentDbVersion();
        }

        transactionCoordinator = new ServerTransactionCoordinator( this, repository);
        CommandExecutor commandExecutor = new CommandExecutor(transactionCoordinator, this);
        
        commandProcessor = new CommandProcessingPool(commandExecutor, messageSource); // Note: Starts listening, so may get connections while processing txlog, should be able to cope as we'll just queue messages until we start the processor
        
        // Initialise repository, tables and indexes with their transient data
        Initialiser initialiser = new Initialiser( repository, this, commandProcessor );
        initialiser.initialise();

        // Clean disk ** THIS DELETES IMPORTED PAGES
        // NO LOGNER DONE HERE.  SEE ServerStore.tryLoad(): pager.deleteNewerPages(getCurrentDbVersion());

        // FIXME: Probably want to make sure that TxLogs play back against existing stores, rather than against
        // stores that have been imported.  Repository should keep stores offline until applyImports() is called.
        // play back tx log
        TxLogPlayback txPlay = new TxLogPlayback(this, commandProcessor);
        txPlay.playback();
        
        // Apply any imported stores
        repository.applyImports();

        // start new tx log
        transactionCoordinator.useTxLog( txLog );

        commandProcessor.start();
        maintThread = new MaintThread(this);
        maintThread.start(); // note: Dangerous if Database is not final class, as thread would start before rest of ctor
        log.info("========== Server started (ver = " + repository.getVersion() + ") ===============");
	}

    
    
    private void performMaintenence() {
    	if (!persistChanges) {
    		return;
    	}
	
        // delete stores
        repository.purgeDeletedStores(transactionCoordinator.getOldestTransactionVersion());
        pager.doMaintenance();
        doSync();
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

    	// We don't sync etc if we're not recording changes
    	if (!persistChanges) {
    		return;
    	}
	
		long now = System.currentTimeMillis();

		if (now - lastSync > syncPeriod) {

			// FIXME: (nu->ac) - This probably needs synchronising, or doing in a blocking thread...?
			// Can you take a look.
			log.trace("Syncing...");
			pager.saveAll();
			log.trace(".. saved pages.. ");

			try {
				long dbVersion = getCurrentDbVersion();
				if (latestDiskVersion != dbVersion) {
					repository.save(setup.getReposDiskRoot());
					txLog.rolloverToNewLog(dbVersion); // TODO: Not sure if this is fully robust. May need some review to check the dbVersion is always correct
					latestDiskVersion = dbVersion;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			log.trace(".. completed sync.");

			lastSync = System.currentTimeMillis(); // Ensure we get gap between syncs
		}
    }


    /**
     * Close the database, blocking until the shutdown is complete. Must be called from some external thread otherwise
     * this will cause a deadlock.
     */
    public void close() {
        closeNonBlocking();
        shutdownFlag.acquireUninterruptibly();
    }

    /**
     * Close the database, without blocking. Useful for executing a remote close command without causing a deadlock.
     */
    public void closeNonBlocking() {
        Thread thread = new Thread("Background Shutdown") {
            @Override
            public void run() {
                synchronized(shutdownFlag) {
                    if (!closed) {
                    	log.info("===== Database shutdown started =====");
                        maintThread.shutdown();
                        commandProcessor.shutdown();
                        transactionCoordinator.close();
                        try {
                            Thread.sleep(500); // TODO(nu->ac): Please explain why this sleep happens, and 500ms is adequate?
                            // NOTE: We're also sleeping while holding a lock which is a deadlock risk
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        try {
                            txLog.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        pager.saveAll();

                        repository.purgeDeletedStores(getCurrentDbVersion());

                        try {
                            if (latestDiskVersion != getCurrentDbVersion()) {
                                repository.save(setup.getReposDiskRoot());
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        closed = true;
                    	log.info("===== Database shutdown complete =====");
                    }
                    shutdownFlag.release(); // TODO(nu->ac): Pls explain why this is needed, and what effect it has?
                }
            }
        };
        thread.start();
    }

    public long getCurrentDbVersion() {
        return repository.getVersion();
    }

    public long getOldestTransactionVersion() {
        return transactionCoordinator.getOldestTransactionVersion();
    }

    public void upissue() {
        repository.upissue();
    }

    public ServerTransactionCoordinator getTransactionCoordinator() {
        return transactionCoordinator;
    }

    /**
     * TODO: If we ever slice things then this will need sorting out.
     * @return 1 (Always)
     */
    public static int getSliceId() {
        return 1;
    }

    public ServerSetupProvider getSetup() {
        return setup;
    }

    public TxLogSink getTxLog() {
        return txLog;
    }

    public ClassLoaderInterface getCommsCli() {
        return cli;
    }

    public PagePersister getPager() {
        return pager;
    }

    public boolean isClosed() {
        return closed;
    }

	public IndexImplementationsService getIndexImplementationsService() {
		return indexImplsService;
	}

	public void setIndexImplsService(IndexImplementationsService indexImplsService) {
		this.indexImplsService = indexImplsService;
	}
}
