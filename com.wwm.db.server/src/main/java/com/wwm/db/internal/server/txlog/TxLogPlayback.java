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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.db.internal.comms.messages.BeginAndCommitCmd;
import com.wwm.db.internal.comms.messages.CommitCmd;
import com.wwm.db.internal.comms.messages.OkRsp;
import com.wwm.db.internal.server.CommandProcessingPool;
import com.wwm.db.internal.server.Database;
import com.wwm.db.internal.server.WorkerThread;
import com.wwm.io.packet.layer2.MessageInterface;
import com.wwm.io.packet.layer2.PacketCodec;
import com.wwm.io.packet.layer2.SourcedMessageImpl;
import com.wwm.io.packet.messages.ErrorRsp;
import com.wwm.io.packet.messages.Message;
import com.wwm.io.packet.messages.PacketMessage;

public class TxLogPlayback extends WorkerThread implements MessageInterface {

	static private Logger log = LogFactory.getLogger(TxLogPlayback.class);

	private Database database;
	private Semaphore finished = new Semaphore(0);
	private Semaphore executing = new Semaphore(0);
	private CommandProcessingPool commandProcessor;

	
	public TxLogPlayback(Database database, CommandProcessingPool commandProcessor) {
		super("TxLogPlayback", commandProcessor);
		this.database = database;
		this.commandProcessor = commandProcessor;
	}
	
	public void playback() {
		super.start();
		finished.acquireUninterruptibly(); // we wait here until released() by run().
	}
	

	private void play(File file) throws FileNotFoundException {
		int playbackErrors = 0; // count the number of errors we get, and report it
		
		TxLogReader reader = new TxLogReader(file);
		PacketCodec pc = new PacketCodec(reader, database.getCommsCli());
		try {
			for (;;) {
				Collection<PacketMessage> messages = pc.read();
				if (messages == null) return;
				for (PacketMessage message : messages) {
					
					Message m = message.getMessage();
					int storeId = m.getStoreId();
					int cid = m.getCommandId();

					if (m instanceof CommitCmd) {
						CommitCmd cc = (CommitCmd)m;
						int tid = cc.getTid();
						m = new BeginAndCommitCmd(storeId, cid, tid, cc);
					}
					
					try {
						commandProcessor.execute(new SourcedMessageImpl(storeId, this, m, message.getPacket()));
					} catch (Error e) {
						playbackErrors++;
					}
					executing.acquireUninterruptibly();
				}
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error playing back TxLog", e);
			return;
		} finally {
			if (playbackErrors > 0){
				log.log(Level.SEVERE, "Errors found during playback: " + playbackErrors + " commands failed.");
			}
			pc.close();
		}
	}
	
	@Override
	public void run() {
		log.info("== Replaying Transaction Logs ==");
		try {
			File txDir = new File(database.getSetup().getTxDiskRoot());
			
			if (!txDir.exists()) return; 

			TxLogIterator txLogs = new TxLogIterator( txDir, database.getCurrentDbVersion(), false );
			
			for (File file : txLogs) {
				if (file == null) continue; // algo does cause some

				// Delete any empty tx logs, as we don't want them around confusing things - there should be
				// only one for a given start version.
				if (file.length() == 0) { 
					file.delete();
					continue;
				}

				log.info("    == Replaying Transaction log: " + file.getName() + " ==" );
				play(file);
			}
		} catch (Throwable e){
			log.log( Level.SEVERE, "Unexpected Exception", e );
			throw new RuntimeException(e);
		} finally {
			finished.release();
		}
		log.info("== Finished transaction log playback ==");
	}

	public void close() {
		// do nothing 
	}

	public Collection<PacketMessage> read() {
		throw new UnsupportedOperationException();
	}

	public void requestClassData(int storeId, String className) {
		throw new UnsupportedOperationException();
	}

	public void send(Message m) {
		executing.release();
		// always release, otherwise error will deadlock.
		if (m instanceof ErrorRsp) {
			ErrorRsp er = (ErrorRsp)m;
			throw new RuntimeException(er.getError());
		}
		if (!(m instanceof OkRsp)) {
			throw new RuntimeException();
		}
	}

	public void send(Message[] m) {
		throw new UnsupportedOperationException();
	}

	public void send(Collection<Message> m) {
		throw new UnsupportedOperationException();
	}
	
}
