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
import com.wwm.db.exceptions.UnknownStoreException;
import com.wwm.db.internal.comms.messages.AllocNewIdsCmd;
import com.wwm.db.internal.comms.messages.AllocNewIdsRsp;
import com.wwm.db.internal.table.UserTable;
import com.wwm.io.core.MessageSink;
import com.wwm.io.core.messages.Command;

public class ServerAllocNewIdsTransaction extends ServerTransaction {

	private long nextOid;
	private int tableId;
	
	public ServerAllocNewIdsTransaction(ServerTransactionCoordinator stc, MessageSink source) {
		super(stc, source);
	}
	@Override
	public void setWriteCommand(Command command, ByteBuffer packet) {
		assert(command instanceof AllocNewIdsCmd);
		super.setWriteCommand(command, packet);
	}

	@Override
	protected void doCommitChecks() throws ArchException {
		// The store must exist
		stc.getRepository().getStore(command.getStoreId());	// This throws if the store does not exist
	}
	
	@Override
	protected void doCommit() {
		AllocNewIdsCmd cmd = (AllocNewIdsCmd)command;
		String nameSpace = cmd.getNamespace();
		Class<?> clazz = cmd.getClazz();
		ServerStore store;
		try {
			store = stc.getRepository().getStore(command.getStoreId());
		} catch (UnknownStoreException e) {
			throw new RuntimeException(e);
		}
		Namespace namespace = store.getCreateNamespace(nameSpace);
		UserTable<?> table = namespace.getCreateTable(clazz);
		nextOid = table.allocNewIds(cmd.getCount());
		tableId = table.getTableId();
	}
	
	@Override
	protected void sendCommitOk() {
		int storeId = command.getStoreId();
		int commandId = command.getCommandId();
		int slice = Database.getSliceId();
		
		AllocNewIdsRsp rsp = new AllocNewIdsRsp(storeId, commandId, slice, tableId, nextOid, ((AllocNewIdsCmd)command).getCount());
		
		try {
			source.send(rsp);
		} catch (IOException e) {
			source.close();
		}
	}
	
}
