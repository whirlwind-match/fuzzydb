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
package com.wwm.attrs.dimensions;


import com.wwm.attrs.SplitConfiguration;
import com.wwm.model.dimensions.IDimensions;

public class DimensionSplitConfiguration extends SplitConfiguration {

	private static final long serialVersionUID = 4886507767190021729L;

	private final IDimensions expected;
	private final IDimensions priority;

	public DimensionSplitConfiguration(int id, IDimensions expected, IDimensions priority) {
		super(id);
		this.expected = expected;
		this.priority = priority;
	}

	public IDimensions getExpected() {
		return expected;
	}

	public IDimensions getPriority() {
		return priority;
	}
}
