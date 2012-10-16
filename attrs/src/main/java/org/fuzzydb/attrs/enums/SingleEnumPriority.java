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
package org.fuzzydb.attrs.enums;

import org.fuzzydb.attrs.AttributePriority;

public class SingleEnumPriority extends AttributePriority {

	private static final long serialVersionUID = 1L;
	private float priority;
	private int size;

    /** Default ctor for serialization libraries */
    private SingleEnumPriority() {
        super(0);
    }

	public SingleEnumPriority(int id, int size, float priority) {
		super(id);
		this.size = size;
		this.priority = priority;
	}

	public int getSize() {
		return size;
	}

	public float getPriority() {
		return priority;
	}
}
