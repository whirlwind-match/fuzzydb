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
import java.net.InetSocketAddress;
import java.util.Collection;

import com.wwm.io.packet.exceptions.NotListeningException;
import com.wwm.io.packet.impl.DummyCli;
import com.wwm.io.packet.layer1.Server;
import com.wwm.io.packet.layer1.ServerImpl;
import com.wwm.io.packet.layer2.SourcedMessage;

/**
 * A multi-threaded executor which processes commands retrieved from a server instance
 */
public class CommandProcessingPool extends WorkerThreadManager {

	private final CommandExecutor commandExecutor;

	private final Server server;


	public CommandProcessingPool(CommandExecutor commandExecutor, DummyCli cli, InetSocketAddress address) throws IOException {
		this.commandExecutor = commandExecutor;
		this.server = new ServerImpl(cli);
		server.listen(address);
	}

    /**
     * This is what gets run when a Worker is 'released'.  On release,  
     */
    @Override
    public void runWorker() {
        Collection<SourcedMessage> messages = null;
        try {
            messages = server.waitForMessage(1000);
        } catch (NotListeningException e) {
            //super.stop();
        } finally {
            endWait(); // FIXME: why do we try releasing other threads here?  Seems to me that this is a mechanism for ensuring that only 1 thread
            			// waits for a message, but then any number of them can be executing, the extra threads having been
        }
        if (messages != null) {
            for (SourcedMessage message : messages) {
                execute(message);
            }
        }
    }

    /**
     * Directly execute a message.  This is usually called from runWorker(),
     * but is also called externally to execute a message for TxLogPlayback
     */
    public void execute(SourcedMessage message) {
        commandExecutor.execute(message);
    }

    @Override
    public synchronized void start() {
    	super.start();
    }
    
    @Override
    public void shutdown() {
    	super.shutdown();
    	server.close();
    }
}
