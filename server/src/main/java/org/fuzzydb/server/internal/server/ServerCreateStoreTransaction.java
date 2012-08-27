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
package org.fuzzydb.server.internal.server;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.fuzzydb.client.exceptions.StoreExistsException;
import org.fuzzydb.client.exceptions.UnknownStoreException;
import org.fuzzydb.client.internal.comms.messages.CreateStoreCmd;
import org.fuzzydb.client.internal.comms.messages.CreateStoreRsp;
import org.fuzzydb.io.core.MessageSink;
import org.fuzzydb.io.core.messages.Command;


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
	protected void doCommitChecks() {
		String storeName = getStoreName();
		
		// Pre checks
		try {
			stc.getRepository().getStore(storeName);
		} catch (UnknownStoreException e) {
			return;
		}
		throw new StoreExistsException("Store already exists: " + storeName);
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
