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
package com.wwm.db.marker;

import com.wwm.db.whirlwind.internal.AttributeCache;

/**
 * An object that can reduce it's memory footprint by replacing contained objects
 * with identical objects referenced in {@link AttributeCache} 
 */
public interface MergeableContainer {

	/**
	 * Reduce memory footprint by replacing references to immutable elements in this container 
	 * with a reference to the instance contained in the AttributeCache 
	 * @param cache - cache of the primary references
	 */
	public void mergeDuplicates(AttributeCache cache);

}
