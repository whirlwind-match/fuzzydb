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

import java.util.Collection;

import org.fuzzydb.io.core.MessageSource;
import org.fuzzydb.io.core.SourcedMessage;
import org.fuzzydb.io.core.exceptions.NotListeningException;

 
/**
 * A multi-threaded executor which processes commands retrieved from a {@link MessageSource} instance
 */
public class CommandProcessingPool extends WorkerThreadManager {

	private final CommandExecutor commandExecutor;

	private final MessageSource messageSource;


	public CommandProcessingPool(CommandExecutor commandExecutor, MessageSource messageSource) {
		this.commandExecutor = commandExecutor;
		this.messageSource = messageSource;
		messageSource.start();
	}

    /**
     * This is what gets run when a Worker is 'released'.  On release,  
     */
    @Override
    public void runWorker() {
        Collection<SourcedMessage> messages = null;
        try {
            messages = messageSource.waitForMessage(1000);
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
    	messageSource.close();
    }
}
