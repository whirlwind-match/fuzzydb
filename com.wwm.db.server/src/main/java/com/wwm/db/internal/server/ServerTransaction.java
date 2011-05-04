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
import java.nio.ByteBuffer;

import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.internal.comms.messages.OkRsp;
import com.wwm.io.core.MessageSink;
import com.wwm.io.core.messages.Command;
import com.wwm.io.core.messages.ErrorRsp;


public abstract class ServerTransaction implements TransactionControl {

	public static enum Mode {
		Normal,
		IndexWrite,	// Index write in progress as part of normal transaction commit
		IndexRebuild // An index rebuild background activity
	}
	
	protected Command command;
	protected final MessageSink source;
	private final TransactionState transactionState;
	protected Repository repository;
	
	protected final ServerTransactionCoordinator stc;
	
	@SuppressWarnings("unused") // used for perf testing
	private static float totalCommitTime = 0;
	@SuppressWarnings("unused") // used for perf testing
	private static int commitCount = 0;
	
	private boolean completed = false;
//	private ByteBuffer packet;
	

	public ServerTransaction(ServerTransactionCoordinator stc, MessageSink source) {
		this.stc = stc;
		this.source = source;
		this.transactionState = new TransactionState(stc.getDatabaseVersionState());
		this.repository = stc.getRepository();
		CurrentTransactionHolder.setTransaction(this);
	}
	
	/* (non-Javadoc)
	 * @see com.wwm.db.internal.server.TransactionControl#setMode(com.wwm.db.internal.server.ServerTransaction.Mode)
	 */
	public void setMode(Mode mode) {
		this.transactionState.setMode(mode);
	}
	
	/* (non-Javadoc)
	 * @see com.wwm.db.internal.server.TransactionControl#getVisibleVersion()
	 */
	public long getVisibleVersion() {
		return transactionState.getVisibleVersion();
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.internal.server.TransactionControl#getCommitVersion()
	 */
	public Long getCommitVersion() {
		return transactionState.getCommitVersion();
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.internal.server.TransactionControl#isInCommitPhase()
	 */
	public boolean isInCommitPhase() {
		return transactionState.isInCommitPhase();
	}
	
	public void setWriteCommand(Command command, ByteBuffer packet) {
		this.command = command;
//		this.packet = packet;
	}
	
	public void commit() {
		
		beginCommit();
		try {
			synchronized (ServerTransaction.class) {
//				long start = System.currentTimeMillis();

				doCommitChecks();

				stc.writeToTransactionLog(command);
				
				doCommit();
				
				
//				long duration = System.currentTimeMillis() - start;
//				totalCommitTime += duration;
//				commitCount++;
//				if (commitCount % 10 == 0) {
//					System.out.println("Mean commit time: " + totalCommitTime/commitCount + "ms, Total commit time: " + totalCommitTime + "ms");
//				}
			}
			endCommit();
			sendCommitOk();
		} catch (ArchException e) {
			abortCommit();
			sendCommitFailed(e);
		} finally {
			markComplete();
		}
	}

	
	protected void markComplete() {
		completed = true;
	}
	
	protected void beginCommit() {
		transactionState.setCommitVersion(stc.acquireWritePrivilege());
	}
	
	protected abstract void doCommitChecks();
	
	protected abstract void doCommit();
	
	protected void sendCommitOk() {
		OkRsp ok = new OkRsp(command.getStoreId(), command.getCommandId());
		try {
			source.send(ok);
		} catch (IOException e) {
			source.close();
		}
	}
	
	protected void sendCommitFailed(ArchException e) {
		ErrorRsp er = new ErrorRsp(command.getStoreId(), command.getCommandId(), e);
		try {
			source.send(er);
		} catch (IOException e1) {
			source.close();
		}
	}
	
	protected void endCommit() {
		stc.releaseWritePrivilege();
	}

	protected void abortCommit() {
		stc.abortWritePrivilege();
	}

	public boolean isCompleted() {
		return completed;
	}
	
}
