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
package org.fuzzydb.attrs;

import java.io.Serializable;

public abstract class AttributePriority implements Serializable {

	private static final long serialVersionUID = 1572490919190087518L;
	private final int id;
	
	protected AttributePriority(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
