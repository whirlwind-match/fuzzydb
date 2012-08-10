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
package org.fuzzydb.attrs.unused;

/**
 * Note: Interface currently not used by itself.
 */
public interface IRange {
	public boolean contains (Comparable<Object> val);
	public Comparable<Object> getMax();
	public Comparable<Object> getMin();
	
}
