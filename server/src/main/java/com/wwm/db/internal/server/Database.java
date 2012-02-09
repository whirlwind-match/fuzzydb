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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.wwm.db.core.LogFactory;
import com.wwm.db.core.UncaughtExceptionLogger;
import com.wwm.db.internal.server.txlog.TxLogPlayback;
import com.wwm.db.internal.server.txlog.TxLogSink;
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
@Singleton
public final class Database {

    static private Logger log = LogFactory.getLogger(Database.class);

    // Ensure we log all uncaught exceptions for whole server.
    static { UncaughtExceptionLogger.initialise(); }

    private final MessageSource messageSource;
    
    @Inject
    private ServerSetupProvider setup;
    
    @Inject
    private DummyCli cli;
    
    private ServerTransactionCoordinator transactionCoordinator;
    
    private CommandProcessingPool commandProcessor;
    
    @Inject
    private RepositoryStorageManager repositoryStorageManager;
    
    private Repository repository;

    @Inject
    private TxLogSink txLog;
    
    private volatile boolean closed = false;

    /**
     * True if disk should be used for persistence.  False will not attempt to 
     * read from disk even if a repository may exist.
     */
    @Inject
    @Named("isPersistent")
    private Boolean isPersistent; 

    /**
     * Executor for async tasks
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private MaintThread maintThread;
    
    @Inject
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
                repositoryStorageManager.doMaintenance();
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
     */
    @Inject
    Database(MessageSource messageSource) {
    	this.messageSource = messageSource;
    }
    
    /**
     * Allows database to be started having been configured with a source of client messages for processing
     * @throws IOException
     */
	public void startServer() throws IOException {
		
		if (messageSource instanceof ClassDefinitionRepositoryAware){
			((ClassDefinitionRepositoryAware) messageSource).setCli(cli);
		}
			
        log.info("========== Starting Server ==========");
        loadAndReplayAnyRecoveredTransactions();
        repository.applyImports();
        goOnline();
        log.info("========== Server started (ver = " + repository.getVersion() + ") ===============");
	}

	/**
	 * Enable new transactions to be started and run
	 */
	private void goOnline() {
		// Enable writing to txLog only once we've finished replay
        transactionCoordinator.useTxLog( txLog );

        commandProcessor.start();
        maintThread = new MaintThread(this);
        maintThread.start(); // note: Dangerous if Database is not final class, as thread would start before rest of ctor
	}

	/**
	 * Load repository and recover to the last persisted state
	 * (this may require replaying transactions from logs on multiple nodes :)
	 */
	private void loadAndReplayAnyRecoveredTransactions() {
		repositoryStorageManager.loadOrCreateRepositoryAsNeeded();
        repository = repositoryStorageManager.getRepository();

        transactionCoordinator = new ServerTransactionCoordinator(repository);
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
        if (isPersistent) {
	        TxLogPlayback txPlay = new TxLogPlayback(this, commandProcessor);
	        txPlay.playback();
        }
	}


    /**
     * Close the database, blocking until the shutdown is complete. Must be called from some external thread otherwise
     * this will cause a deadlock.
     */
    public void close() {
        try {
			closeNonBlocking().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Close the database, without blocking. Useful for executing a remote close command without causing a deadlock.
     */
    public Future<?> closeNonBlocking() {
    	return executor.submit(new Runnable() {
			@Override
			public void run() {
                if (!closed) {
                	log.info("===== Database shutdown started =====");
                    maintThread.shutdown();
                    commandProcessor.shutdown();
                    transactionCoordinator.close();
                    repositoryStorageManager.shutdown();
                    closed = true;
                	log.info("===== Database shutdown complete =====");
                }
            }
		});
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

    public ClassLoaderInterface getCommsCli() {
        return cli;
    }

    public boolean isClosed() {
        return closed;
    }

	public IndexImplementationsService getIndexImplementationsService() {
		return indexImplsService;
	}
}
