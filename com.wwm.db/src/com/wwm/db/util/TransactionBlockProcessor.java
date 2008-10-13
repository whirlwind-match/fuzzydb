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
package com.wwm.db.util;

import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.util.BlockProcessor;


/**
 * A block processor that creates a transaction on the supplied store,
 * and when process(num) is called iterates through 'num' iterations,
 * and at each blockSize interval, commits the transaction and 
 * creates a new one.
 * Each iteration, everyTime( Transaction t, int count ) is called.
 * 
 * @author Neale
 */
public abstract class TransactionBlockProcessor extends BlockProcessor {

	private Store store;
	private Transaction t;

	public TransactionBlockProcessor(Store store, int blockSize) {
		super(blockSize);
		this.store = store;
		this.t = store.getAuthStore().begin();
	}

	@Override
	public void everyBlock(int count) throws Exception {
		t.commit();
		t = store.getAuthStore().begin();
	}

	@Override
	public void everyTime(int count) throws Exception {
		everyTime( t, count );
	}
	
	public abstract void everyTime(Transaction t, int count) throws Exception;

}
