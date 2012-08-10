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
package org.fuzzydb.attrs.simple;


import org.fuzzydb.attrs.internal.BranchConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttribute;


public class FloatBranchConstraint extends BranchConstraint {

	private static final long serialVersionUID = 3833744374088347700L;
	protected float min;
	protected float max;

	
	public FloatBranchConstraint(){
		super(0);
		min = -Float.MAX_VALUE;
		max = Float.MAX_VALUE;
	}

	
	
//	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//		// Select the stuff we want
//		GetField f = in.readFields();
//		values = (float[]) f.get("values", null);
//	}

	
	Object readResolve() {
		return new FloatConstraint(attrId,min, max, isIncludesNotSpecified() );
	}
	


	@Override
	public BranchConstraint clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean consistent(IAttribute value) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean expandNonNull(IAttribute value) {
		throw new UnsupportedOperationException();
	}
}
