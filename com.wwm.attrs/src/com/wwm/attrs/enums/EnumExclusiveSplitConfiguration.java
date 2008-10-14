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
package com.wwm.attrs.enums;

import com.wwm.attrs.SplitConfiguration;

public class EnumExclusiveSplitConfiguration extends SplitConfiguration {

	private static final long serialVersionUID = 1L;
	private float priority;
	private int size;

	public EnumExclusiveSplitConfiguration(int id, int size, float priority) {
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
	
	// Migrate from old version that used to contain an EnumDefinition
	// This can be removed when all is migrated.
	// FIXME: Remove this when migrated
//	private void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException {
//		GetField fields = in.readFields();
//		priority = fields.get("priority", 1f );
//		
//		try {
//				EnumDefinition def = (EnumDefinition) fields.get("def", null);
//			size = def.size();
//		} catch (IllegalArgumentException e) {
//			size = fields.get( "size", 32);
//		}
//	}
}
