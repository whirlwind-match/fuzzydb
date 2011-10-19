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
package com.wwm.db.internal.comms.messages;

import java.util.ArrayList;

import com.wwm.io.core.messages.Command;

@SuppressWarnings("serial")
public class DisposeCmd extends Command {

	private ArrayList<Integer> disposedTransactions;
	private ArrayList<Integer> disposedQueries;

    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private DisposeCmd() {
        super(-1, -1);
    }

	public DisposeCmd(int cid, ArrayList<Integer> disposedTransactions, ArrayList<Integer> disposedQueries) {
		super(0, cid);
		this.disposedTransactions = disposedTransactions;
		this.disposedQueries = disposedQueries;
	}

	public ArrayList<Integer> getDisposedQueries() {
		return disposedQueries;
	}

	public ArrayList<Integer> getDisposedTransactions() {
		return disposedTransactions;
	}
}
