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
package com.wwm.attrs.simple;

import com.wwm.model.dimensions.IDimensions;

public interface IFloatRangePreference extends IDimensions {

	public static final int DIMENSIONS = 3;

    public static final int LOW_TO_PREF_DIFF = 1;
    public static final int PREF_TO_HIGH_DIFF = 2;
    public static final int PREF = 0;

	/**
	 * @return Returns the max.
	 */
	public abstract float getMax();

	/**
	 * @return Returns the min.
	 */

	public abstract float getMin();

	/**
	 * @return Returns the preferred.
	 */
	public abstract float getPreferred();

}