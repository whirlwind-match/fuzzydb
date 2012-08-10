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

import org.fuzzydb.attrs.internal.Attribute;
import org.fuzzydb.attrs.util.Range;


/**
 * @author Neale
 */
public abstract class GenericRangePreference extends Attribute implements
		IRange {

	protected Comparable<Object> min;
	protected Comparable<Object> max;

    /**
	 * @return Returns the max.
	 */
	public final Comparable<Object> getMax() {
		return max;
	}
	/**
	 * @return Returns the min.
	 */
	public final Comparable<Object> getMin() {
		return min;
	}

	/**
	 * @param min
	 * @param max
	 */
	public GenericRangePreference( int attrId, Comparable<Object> min,
			Comparable<Object> max) {
	    super( attrId );
		this.min = min;
		this.max = max;
	}
	
	
	public final boolean contains (Comparable<Object> val) {
		return Range.contains( min, val, max);
	}
}
