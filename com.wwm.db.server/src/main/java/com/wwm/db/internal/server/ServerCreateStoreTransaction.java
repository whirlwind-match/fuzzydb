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
import com.wwm.db.exceptions.StoreExistsException;
import com.wwm.db.exceptions.UnknownStoreException;
import com.wwm.db.internal.comms.messages.CreateStoreCmd;
import com.wwm.db.internal.comms.messages.CreateStoreRsp;
import com.wwm.io.core.MessageSink;
import com.wwm.io.core.messages.Command;

public class ServerCreateStoreTransaction extends ServerTransaction {

	private int storeId;
	
	public ServerCreateStoreTransaction(ServerTransactionCoordinator stc, MessageSink source) {
		super(stc, source);
	}

	@Override
	public void setWriteCommand(Command command, ByteBuffer packet) {
		assert(command instanceof CreateStoreCmd);
		super.setWriteCommand(command, packet);
	}

	@Override
	protected void doCommitChecks() throws ArchException {
		String storeName = getStoreName();
		
		// Pre checks
		try {
			stc.getRepository().getStore(storeName);
		} catch (UnknownStoreException e) {
			return;
		}
		throw new StoreExistsException(storeName);
	}
	
	@Override
	protected void doCommit() {
		storeId = stc.getRepository().createStore(getStoreName());
	}
	
	@Override
	protected void sendCommitOk() {
		String storeName = getStoreName();
		CreateStoreRsp ok = new CreateStoreRsp(command.getCommandId(), storeName, storeId);
		try {
			source.send(ok);
		} catch (IOException e) {
			source.close();
		}
	}
	
	private String getStoreName() {
		CreateStoreCmd cmd = (CreateStoreCmd)command;
		return cmd.getStoreName();
	}
}
