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


/**
 * A generalized class for doing one action repeatedly, but every x times, doing something else.
 * @author Neale
 *
 */
public abstract class BlockProcessor {

	int blockSize;

	
	public BlockProcessor( int blockSize ) {
		this.blockSize = blockSize;
	}
	
	
	public void process( int number ) {
		/* The same as above  */ 
		for ( int i = 0; i < number; i++) {
			everyTime( i );
			if ( i % blockSize == blockSize - 1 || i == number - 1 ) {
				everyBlock( i );
			}
		}
        blocksComplete();
	}

	/**
	 * Action to be performed for each iteration
	 * @throws Exception 
	 */
	abstract public void everyTime( int count );
	
	/**
	 * Action to be performed after each <code>blockSize</code> calls to <code>everyTime()</code>
	 * @throws Exception 
	 */
	abstract public void everyBlock( int count );

    public void blocksComplete() {
    }
	
	
	
}
