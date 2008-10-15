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
package com.wwm.db.internal.pager;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.internal.pager.Page.PagePurgedException;
import com.wwm.db.internal.server.CurrentTransactionHolder;
import com.wwm.db.internal.server.DatabaseVersionState;
import com.wwm.db.internal.server.ServerSetupProvider;
import com.wwm.db.internal.server.ServerStore;
import com.wwm.db.internal.server.TransactionState;

import static org.junit.Assert.*;

/**
 * Defines test for Page of Elements.  Should also support other paging mechanisms via common interface.
 */
public class TestPageWriteRead {

	protected long currentDbVer;
	protected long oldestTxVer;
	protected ServerStore store;
	
	private DatabaseVersionState vp = new DatabaseVersionState(){
		public long getCurrentDbVersion() {
			return currentDbVer;
		}
		public long getOldestTransactionVersion() {
			return oldestTxVer;
		}
		public void upissue() {
			currentDbVer++;
		}
	};

	protected Long transactionDbVersion = 0L;

	
	private PagerContext pc = new PagerContext(){
		public ServerStore getStore() {
			return store;
		}
	};
	
	@Before
	public void setUp() throws Exception {
		store = new ServerStore( new ServerSetupProvider().getReposDiskRoot() + File.separator + "pages", "TestStore", 1);
		store.deletePersistentData(); // clear out any old data.
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testWriteReadback() throws Exception {
		
		long startingOid = 0;
		long oid1 = 0;
		long oid2 = 1;

		{
			startWriteTx();
			Page page = newPage(startingOid);
			checkDoesntExist(oid1, page);
			setElementData(oid1, "First", page);
			setElementData(oid2, "Second", page);
			savePage(page);
			endWriteTx();
		}		
		
		{
			startReadTx();
			Page page = newPage(startingOid);
			page.load();
			checkElementData(oid1, "First", page);
			checkElementData(oid2, "Second", page);
		}
		
		// Scenario 2: First modified, Second element not modified but tx moves forwards
		{
			startWriteTx();
			Page page = newPage(startingOid);
			page.load();
			updateElementData(oid1, "FirstModified", page);
			savePage(page);
			endWriteTx();
		}

		{
			startReadTx();
			Page page = newPage(startingOid);
			page.load();
			checkElementData(oid1, "FirstModified", page);
			checkElementData(oid2, "Second", page);
		}

		// Scenario 3: Second modified, First not modified but tx moves forwards
		{
			startWriteTx();
			Page page = newPage(startingOid);
			page.load();
			updateElementData(oid2, "SecondModified", page);
			savePage(page);
			endWriteTx();
		}

		{
			startReadTx();
			Page page = newPage(startingOid);
			page.load();
			checkElementData(oid1, "FirstModified", page);
			checkElementData(oid2, "SecondModified", page);
		}
	}

	
	
	/**
	 * Test to ensure that multiple save() operations without subsequent load, do 
	 * end up creating the correct sequence of versions on disk
	 */
	@Test
	public void testPageDataReflectsDiskState() throws Exception {
		long startingOid = 0;
		long oid1 = 0;
		long oid2 = 1;

		// Write initial and save it.
		{
			startWriteTx();
			Page page = newPage(startingOid);
			checkDoesntExist(oid1, page);
			setElementData(oid1, "First", page);
			setElementData(oid2, "Second", page);
			savePage(page);
			endWriteTx();
		}		
		
		// Load it back in - internally, page now has pageData set
		{
			startReadTx();
			Page page = newPage(startingOid);
			page.load();
			checkElementData(oid1, "First", page);
			checkElementData(oid2, "Second", page);
		
			// Write using loaded page
			startWriteTx();
			updateElementData(oid1, "FirstModified", page);
			savePage(page);
			endWriteTx();

			// Another write using loaded page, after another save()
			startWriteTx();
			updateElementData(oid2, "SecondModified", page);
			savePage(page);
			endWriteTx();
		}

		{
			startReadTx();
			Page page = newPage(startingOid);
			page.load();
			checkElementData(oid1, "FirstModified", page);
			checkElementData(oid2, "SecondModified", page);
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	private Page newPage(long startingOid) {
		return Page.blankPage(5, store.getPath() + File.separator + TestPageWriteRead.class.getName(), pc, vp, startingOid);
	}

	private void savePage(Page page) throws PagePurgedException, IOException {
		page.acquireWrite();
		page.save(false);
		page.releaseWrite();
	}

	private void checkDoesntExist(long oid1, Page page) throws PagePurgedException, IOException,
			ClassNotFoundException {
		page.acquireWrite();
		Element e = page.getElementForWrite(oid1);
		assertNull("Element shouldn't yet exist", e);
		page.releaseWrite();
	}

	private void checkElementData(long oid, String expected, Page page) throws PagePurgedException,
			IOException, ClassNotFoundException, UnknownObjectException {
		page.acquireRead();
		ElementReadOnly e1 = page.getElementForRead(oid);
		assertEquals(expected, e1.getVersion());
		page.accessed(false);
		page.releaseRead();
	}

	private void setElementData(long oid, String data, Page page) throws PagePurgedException {
		page.acquireWrite();
		Element ew = new Element(oid, data);
		page.setElement(oid, ew);
		page.accessed(true);
		page.releaseWrite();
	}

	private void updateElementData(long oid, String data, Page page) throws PagePurgedException, IOException, ClassNotFoundException {
		page.acquireWrite();
		Element ew = page.getElementForWrite(oid);
		ew.addVersion(data);
		page.accessed(true);
		page.releaseWrite();
	}

	
	private void startReadTx() {
		TransactionState tc = new TransactionState(vp); // This is for one transaction
		// reading now tc.setCommitVersion(vp.getCurrentDbVersion() + 1); // simulate start of commit phase
		CurrentTransactionHolder.setTransaction(tc);
	}

	private void endWriteTx() {
//		oldestTxVer = currentDbVer; // marks current version as that of latest transaction i.e. expire transactions as they go 
		vp.upissue();
	}

	private void startWriteTx() {
		TransactionState tc = new TransactionState(vp); // This is for one transaction
		tc.setCommitVersion(vp.getCurrentDbVersion() + 1); // simulate start of commit phase
		CurrentTransactionHolder.setTransaction(tc);
	}

}
