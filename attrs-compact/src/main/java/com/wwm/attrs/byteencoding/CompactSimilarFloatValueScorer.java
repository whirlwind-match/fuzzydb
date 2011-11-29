/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.attrs.byteencoding;

import org.slf4j.Logger;

import com.wwm.attrs.Score;
import com.wwm.attrs.Score.Direction;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.attrs.internal.TwoAttrScorer;
import com.wwm.attrs.simple.FloatConstraint;
import com.wwm.attrs.simple.FloatValue;
import com.wwm.db.core.LogFactory;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.ByteArray;



/**
 * A scorer to score 100% if two float values are identical, and less if not.
 * TODO: Make less crude!
 * @author Neale
 */
public class CompactSimilarFloatValueScorer extends TwoAttrScorer {

    private static final long serialVersionUID = 1L;

    private static Logger log = LogFactory.getLogger(CompactSimilarFloatValueScorer.class);
	
	private float expectedRange;
	
	public CompactSimilarFloatValueScorer( int scoreAttrId, int otherAttrId, float expectedRange ) {
		super(scoreAttrId, otherAttrId);
		assert( expectedRange > 0f);
		this.expectedRange = expectedRange;
	}

	
	/**
	 * Calculate the score
	 */
	private float scoreFloats( float scoreVal, float otherVal ) {
		// this may be crude: score as the ratio of the smallest vs the highest -> 0.0-1.0 range
		
		float diff = Math.abs( scoreVal - otherVal );
		
		if ( diff >= expectedRange ) return 0f;
		
		float result = 1.0f - (diff / expectedRange);
		assert( result <= 1.0f );
		return result;
	}

    // FIXME Extract relevant bits from score()
    @Override
	public void scoreSearchToNode(Score score, Score.Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        assert( d == Direction.forwards ); // Always the case
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}
       	score(score, d, attr, c);
    }
    
    @Override
	public void scoreNodeToSearch(Score score, Score.Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
        assert( d == Direction.reverse ); // Always the case
    	IAttributeConstraint constraint = c.findAttr(scorerAttrId);
		if (constraint == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

		if (constraint.isIncludesNotSpecified()) {
			return;
    	}

		// This is the same attribute in the other container
		FloatValue other = (FloatValue)searchAttrs.findAttr( scorerAttrId );
		if (other == null) return; // If no matching have, we score 1.0f
	
		FloatConstraint fc = (FloatConstraint)constraint;
		scoreConstraint(score, d, other, fc);
    }
    
	/**
	 * Implementation that avoids creating any objects.  Directly inspects the byte array
	 */
    @Override
    public void scoreItemToItem(Score score, Direction d, IAttributeMap<IAttribute> otherAttrs, IAttributeMap<IAttribute> scoreAttrs) {
    	// We assume that both are CompactAttrMap
    	ByteArray scoreBytes = ((CompactAttrMap<?>)scoreAttrs).getByteArray();
    	ByteArray otherBytes = ((CompactAttrMap<?>)otherAttrs).getByteArray();
    	
    	int scoreIndex = CompactAttrCodec.findAttrInBuf(scoreBytes, scorerAttrId);
		if (scoreIndex == CompactAttrCodec.NOT_FOUND) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

    	int otherIndex = CompactAttrCodec.findAttrInBuf(otherBytes, otherAttrId);

        if (otherIndex == CompactAttrCodec.NOT_FOUND) {
            score.addNull(this, d);
            return;
        }

        score.add(this, calcScore(scoreBytes, scoreIndex, otherBytes, otherIndex), d);
    }

	
	private float calcScore(ByteArray scoreBytes, int scoreIndex, ByteArray otherBytes, int otherIndex) {
		float scoreVal = FloatCodec.getValue(scoreBytes, scoreIndex);
		float otherVal = FloatCodec.getValue(otherBytes, otherIndex);
		return scoreFloats( scoreVal, otherVal );
	}


    
    /**
     * For a node, give the highest possible score.
     * If we're within the bounds of the constraint for the node, then the max poss score is 1.0
     * else, it's the ration of the distance to the nearest value of the range.
     * @see com.wwm.attrs.Scorer#score(com.wwm.db.whirlwind.internal.IAttribute, likemynds.db.indextree.NodeAttributeContainer)
     */
	private void score(Score score, Score.Direction d, IAttribute scoreAttr, IConstraintMap c) {
    	
    	IAttributeConstraint na = c.findAttr(scorerAttrId);
    	
		if (na == null) return;
		if (na.isIncludesNotSpecified()) { // na.hasValue() && 
			score.add(this, 1.0f, d);
			return;
		}
//		if (!na.hasValue()) {
//			return;
//		}
    	
		assert( scoreAttr.getAttrId() == scorerAttrId );

		FloatValue scoreVal = (FloatValue) scoreAttr;
		FloatConstraint bc = (FloatConstraint)na;

		scoreConstraint(score, d, scoreVal, bc);
    }


	private void scoreConstraint(Score score, Score.Direction d, FloatValue scoreVal, FloatConstraint bc) {
		float s;
		if (bc == null) {
			return;
		}
		if ( bc.consistent(scoreVal) ) {
			s = 1.0f;
		}
		else {
			float branch_min = bc.getMin();
			float branch_max = bc.getMax();
			float myVal = scoreVal.getValue();
			if (myVal < branch_min ) {
				s = scoreFloats(myVal, branch_min);
			} 
			else if (myVal > branch_max ) {
				s = scoreFloats(myVal, branch_max);
			}
			else {
				s = 1.0f;
			}
		}
		score.add(this, s, d);
	}

	public float getExpectedRange() {
		return expectedRange;
	}

	public void setExpectedRange(float expectedRange) {
		this.expectedRange = expectedRange;
	}
}
