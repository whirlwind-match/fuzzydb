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


/**
 * Attribute scorer for scoring two attributes against two other attributes
 */
public abstract class TwoByTwoAttrScorer extends TwoAttrScorer {

	protected int scoreSecondAttrId;
	protected int otherSecondAttrId;
	
	public TwoByTwoAttrScorer( int scoreAttrId, int scoreSecondAttrId, 
			int otherAttrId, int otherSecondAttrId ) {
		super( scoreAttrId, otherAttrId );
		this.scoreSecondAttrId = scoreSecondAttrId;
		this.otherSecondAttrId = otherSecondAttrId;
	}

	public int getOtherSecondAttrId() {
		return otherSecondAttrId;
	}

	public int getScoreSecondAttrId() {
		return scoreSecondAttrId;
	}
	

}
