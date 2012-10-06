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
package org.fuzzydb.attrs.dimensions;


import org.fuzzydb.attrs.AttributePriority;
import org.fuzzydb.dto.dimensions.IDimensions;


public class DimensionPriority extends AttributePriority {

	private static final long serialVersionUID = 4886507767190021729L;

	private final IDimensions expected;
	private final IDimensions priority;

    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private DimensionPriority() {
        this(0, null, null);
    }

	public DimensionPriority(int id, IDimensions expected, IDimensions priority) {
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
