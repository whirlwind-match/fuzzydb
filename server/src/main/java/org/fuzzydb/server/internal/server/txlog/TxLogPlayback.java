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
package org.fuzzydb.server.internal.server.txlog;

import java.io.File;
import java.util.concurrent.Semaphore;

import org.fuzzydb.core.LogFactory;
import org.fuzzydb.server.internal.server.CommandProcessingPool;
import org.fuzzydb.server.internal.server.Database;
import org.fuzzydb.server.internal.server.WorkerThread;
import org.slf4j.Logger;


/**
 * WorkerThread that plays back the transaction log and applies it to the database
 */
public class TxLogPlayback extends WorkerThread {

	static private Logger log = LogFactory.getLogger(TxLogPlayback.class);

	private final Database database;
	private final Semaphore finished = new Semaphore(0);
	private final CommandProcessingPool commandProcessor;

	
	public TxLogPlayback(Database database, CommandProcessingPool commandProcessor) {
		super("TxLogPlayback", commandProcessor);
		this.database = database;
		this.commandProcessor = commandProcessor;
	}
	
	public void playback() {
		super.start();
		finished.acquireUninterruptibly(); // we wait here until released() by run().
	}
	
	
	@Override
	public void run() {
		log.info("== Replaying Transaction Logs ==");
		try {
			File txDir = new File(database.getSetup().getTxDiskRoot());
			
			if (!txDir.exists()) return; 

			TxLogIterator txLogs = new TxLogIterator( txDir, database.getTransactionCoordinator().getCurrentDbVersion(), false );
			
			for (File file : txLogs) {
				if (file == null) continue; // algo does cause some

				// Delete any empty tx logs, as we don't want them around confusing things - there should be
				// only one for a given start version.
				if (file.length() == 0) { 
					file.delete();
					continue;
				}

				log.info("    == Replaying Transaction log: " + file.getName() + " ==" );
				FilePacketStreamCommandPlayer player = new FilePacketStreamCommandPlayer(file, commandProcessor, database.getCommsCli());
				player.run();
			}
		} catch (Exception e){
			log.error( "Unexpected Exception", e );
			throw new RuntimeException(e);
		} finally {
			finished.release();
		}
		log.info("== Finished transaction log playback ==");
	}
}
