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

import java.nio.ByteBuffer;

import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.internal.comms.messages.DeleteStoreCmd;
import com.wwm.io.packet.layer2.MessageInterface;
import com.wwm.io.packet.messages.Command;

public class ServerDeleteStoreTransaction extends ServerTransaction {
	
	public ServerDeleteStoreTransaction(ServerTransactionCoordinator stc, MessageInterface source) {
		super(stc, source);
	}

	@Override
	public void setWriteCommand(Command command, ByteBuffer packet) {
		assert(command instanceof DeleteStoreCmd);
		super.setWriteCommand(command, packet);
	}

	@Override
	protected void doCommitChecks() throws ArchException {
		String storeName = getStoreName();
		
		// Pre checks
		stc.getRepository().getStore(storeName);	// this throws if the store does not exist
	}
	
	@Override
	protected void doCommit() {
		stc.getRepository().deleteStore(getStoreName());
	}
	
	private String getStoreName() {
		DeleteStoreCmd cmd = (DeleteStoreCmd)command;
		return cmd.getStoreName();
	}

}
