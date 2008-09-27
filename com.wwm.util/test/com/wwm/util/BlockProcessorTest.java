/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BlockProcessorTest {

	int blocksDone, itemsDone; 

	/**
	 * Test class for measuring what happpened
	 */
	class TestBlockProcessor extends BlockProcessor {
		public TestBlockProcessor(int blockSize) {
			super(blockSize);
		}

		@Override
		public void everyBlock(int count) {
			blocksDone++;
		}
		
		@Override
		public void everyTime(int count) {
		itemsDone++;				
		}
	}; 

	/**
	 * Clear counters each time.
	 */
	@Before
	public void setUp() throws Exception {
		blocksDone = 0;
		itemsDone = 0;
	}
	
	
	@Test public void testProcessItemsLessThanBlockSize() throws Exception {
		TestBlockProcessor bp100 = new TestBlockProcessor( 100 );
		bp100.process( 1 );
		assertTrue( blocksDone == 1 );
		assertTrue( itemsDone == 1 );
	}

	@Test public void testProcessItemsEqualsBlockSize() throws Exception {
		
		TestBlockProcessor bp100 = new TestBlockProcessor( 100 );
		bp100.process( 100 );
		assertTrue( blocksDone == 1 );
		assertTrue( itemsDone == 100 );
	}

	@Test public void testProcessItemsExactMultipleOfBlockSize() throws Exception {
				
		TestBlockProcessor bp100 = new TestBlockProcessor( 100 );
		bp100.process( 100 * 5 );
		assertTrue( blocksDone == 5 );
		assertTrue( itemsDone == 100 * 5 );
	}
	
	@Test public void testProcessItemsInBetweenMultiplesOfBlockSize() throws Exception {
		
		TestBlockProcessor bp100 = new TestBlockProcessor( 100 );
		bp100.process( 443 );
		assertTrue( blocksDone == 5 );
		assertTrue( itemsDone == 443 );
	}

}
