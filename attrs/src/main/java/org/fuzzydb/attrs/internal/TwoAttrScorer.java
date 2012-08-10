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
package org.fuzzydb.attrs.internal;

import org.fuzzydb.attrs.Scorer;
import org.springframework.util.Assert;



/**
 * Attribute scorer for scoring two attributes against each other
 */
public abstract class TwoAttrScorer extends Scorer {

	private static final long serialVersionUID = -2774223535588569867L;

	protected int otherAttrId;

    public TwoAttrScorer( int scoreAttrId, int otherAttrId ) {
        super( scoreAttrId );
		Assert.state(otherAttrId != 0, "otherAttrId must be defined");
        this.otherAttrId = otherAttrId;
    }

    public int getOtherAttrId() {
        return otherAttrId;
    }

	@Override
	protected void assertValidInternal() {
		Assert.state(otherAttrId != 0, "otherAttrId must be defined");
	}

}
