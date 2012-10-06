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
package org.fuzzydb.attrs.bool;

import org.fuzzydb.attrs.AttributePriority;

public class BooleanPriority extends AttributePriority {

	private static final long serialVersionUID = -6366036733104491988L;
	private final float priority;
	
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private BooleanPriority() {
        this(0, 1f);
    }

	public BooleanPriority(int id, float priority) {
		super(id);
		this.priority = priority;
	}

	public float getPriority() {
		return priority;
	}	
}
