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

import com.wwm.attrs.SplitConfiguration;

public class FloatSplitConfiguration extends SplitConfiguration {

	private static final long serialVersionUID = 3697244522653626113L;

	private final float expected;
	private final float priority;

    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private FloatSplitConfiguration() {
        this(0, 1f, 1f);
    }

	public FloatSplitConfiguration(int id, float expected, float priority) {
		super(id);
		this.expected = expected;
		this.priority = priority;
	}

	public float getExpected() {
		return expected;
	}

	public float getPriority() {
		return priority;
	}

}
