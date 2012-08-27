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
package org.fuzzydb.server.internal.whirlwind;

/**
 * Interface to allow 'commit' type operations to flag up if an object is
 * the original from a persistent store, or is one that has been
 * written/updated to a persistent store.
 * Once written or updated, a clone must be made before doing any writes
 * to an Immutable object.
 * This can be implemented by 
 * <pre>
 * 		private transient boolean mutable;
 * </pre>
 * and
 * 		setting mutable=true in the clone() or copy constructor methods.
 * 
 * In methods that modify the object, add the line:
 * 		assert(mutable);
 * 
 * And to ensure that the modified object got committed to storage, add:
 * <pre>
 * 	public void finalize(){
 * 		assert(!mutable);
 * 	}
 * </pre>
 * which may (assuming finalize gets called) ensure that mutable got reset 
 * by create() or update()
 * 
 * @author Neale
 *
 */
public interface Immutable {

	/**
	 * If an object implements Immutable, this must be called to
	 * set it immutable when committed to a persistent store.
	 */
	public void setImmutable();
	
	
}
