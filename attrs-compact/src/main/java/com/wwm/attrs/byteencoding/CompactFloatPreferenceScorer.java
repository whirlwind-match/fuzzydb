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

import com.wwm.attrs.Score;
import com.wwm.attrs.Score.Direction;
import com.wwm.attrs.dimensions.DimensionsRangeConstraint;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.ByteArray;
import com.wwm.util.ScoreMapper;



/**
 * @author Neale
 */
public class CompactFloatPreferenceScorer extends com.wwm.attrs.simple.FloatRangePreferenceScorer {

    private static final long serialVersionUID = 1L;

    
    public CompactFloatPreferenceScorer(int scoreAttrId, int otherAttrId, ScoreMapper scoreMapper) {
		super(scoreAttrId, otherAttrId, scoreMapper);
	}

	/**
	 * Score this RangePreference against the specified container.
	 * @param valueAttrId to look for for value to score against (for DbTreeItem)
	 * @param constraintAttrId to look for for constraint to score against (for Node)
	 * @return
     */
//	public void scoreItemToItemOld(Score score, Score.Direction d, IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {
//        IAttribute wantAttr = scoreAttrs.findAttr(scorerAttrId);
//		if (wantAttr == null) {
//			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
//		}
//    	
//		assert( wantAttr.getAttrId() == scorerAttrId );
//
//		// This is the have that we're going to score our want against.
//		FloatValue have = (FloatValue)c.findAttr( otherAttrId );
//        // Ignore if not scoring null
//        if (have == null) {
//            score.addNull(this, d);
//            return;
//        }
//		
//		if (wantAttr instanceof FloatRangePreference) { // Scoring a single want against the Have
//			IFloatRangePreference want = (IFloatRangePreference)wantAttr;
//			float scoreVal = scoreGap( want, have.getValue() );
//            score.add(this, scoreVal, d);
//		}
//	}

    
    /**
     * Triggered on a FloatRangePreference (in profile/search) scoring against a BranchConstraint
     * in a Node.  The BC is a range of float values (min -> max).
     * Here we establish the highest possible score that a value in the range min->max
     * can score against the given FRP.
     * @see likemynds.db.indextree.Scorer#scoreSearchToNode(likemynds.db.indextree.Score, likemynds.db.indextree.Score.Direction, likemynds.db.indextree.attributes.Attribute, likemynds.db.indextree.NodeAnnotationContainer, IAttributeMap<IAttribute>)
     */
    @Override
    public void scoreSearchToNode(Score score, Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {

      	ByteArray scoreBytes = ((CompactAttrMap<?>)scoreAttrs).getByteArray();
    	ByteArray otherBytes = ((CompactAttrMap<?>)c).getByteArray();
    	
    	int scoreIndex = CompactAttrCodec.findAttrInBuf(scoreBytes, scorerAttrId);
		if (scoreIndex == CompactAttrCodec.NOT_FOUND) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

    	int otherIndex = CompactAttrCodec.findAttrInBuf(otherBytes, otherAttrId);

        if (otherIndex == CompactAttrCodec.NOT_FOUND) {
            score.addNull(this, d);
            return;
        }


        float result = 0.0f;

        // If there are nulls underneath this 
        // node include a null score
        if (CompactConstraintCodec.getIncludesNotSpecified(otherBytes, otherIndex) ) {
            if (isScoreNull()) {
                result = getScoreOnNull();
            }
        }
        
        float branchMin = FloatConstraintCodec.getMin(otherBytes, otherIndex);
        float branchMax = FloatConstraintCodec.getMax(otherBytes, otherIndex);
//        float myMin = want.getMin();
//        float myMax = want.getMax();
        float pref = FloatRangePreferenceCodec.getPref(scoreBytes, scoreIndex);
        
        /* if preferred on the FRP is within range of float values for the node, then
         * it is possible for there to be an item that scores maxScore
         * 
         * 
         *     Pref
         *    | /\ |
         *    |/  \|
         *    /    \   verticals are branchMin & branchMax
         *   /|    |\
         *  Lo      Hi
         */
        float s;
        if ( branchMin <= pref && pref <= branchMax ) {
            s = maxScore;
        }
        // myPref is outside the range, branchMin or Max are somewhere on the slope
        // of the FRP.  Find out which one to score, and return that score
        /* 
         *        Pref
         *  |    | /\
         *  |    |/  \
         *  |    /    \   verticals are branchMin & branchMax 
         *  |   /|     \      NOTE: branchMax (in this case) is allowed to be below Lo
         *     Lo      Hi
         */
        else {
    		float low = FloatRangePreferenceCodec.getMin(scoreBytes, scoreIndex);
    		float hi = FloatRangePreferenceCodec.getMax(scoreBytes, scoreIndex);
    		if ( pref > branchMax ) {
    			s = scoreGap(branchMax, low, hi, pref);
	        }
	        else { // BC must be other side, so use lower end of BC 
    			s = scoreGap(branchMin, low, hi, pref);
	        }
        }
        result = Math.max(result, s);
        score.add(this, result, d);
    }
    
    
    /**
     * Score a range of FRPs against one value to find the max score of that value
     * against possible FRPs in the node, specified by the DRC
     * SCORE FROM constraints TO searchAttrs
     */
    @Override
    public void scoreNodeToSearch(Score score, Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {

    	// temp. for back to back testing while in debugger
    	if (false) {
    		super.scoreNodeToSearch(score, d, c, searchAttrs);
    		return;
    	}
    	
    	//================ BEGIN STANDARD BLOCK (to refactor) ====================
    	// We assume that both are CompactAttrMap
    	ByteArray scoreBytes = ((CompactAttrMap<?>)c).getByteArray();
    	ByteArray otherBytes = ((CompactAttrMap<?>)searchAttrs).getByteArray();
    	
    	// 1) Do we have the scorer attribute: If not, return
    	int scoreIndex = CompactAttrCodec.findAttrInBuf(scoreBytes, scorerAttrId);
		if (scoreIndex == CompactAttrCodec.NOT_FOUND) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

    	int otherIndex = CompactAttrCodec.findAttrInBuf(otherBytes, otherAttrId);
        if (otherIndex == CompactAttrCodec.NOT_FOUND) {
            score.addNull(this, d);
            return;
        }

        // If there are nulls underneath this 
        // node include a null score
        if (CompactConstraintCodec.getIncludesNotSpecified(scoreBytes, scoreIndex) ) {
            score.add(this, maxScore, d);
            return;
        }
        //================ END STANDARD BLOCK ====================
        

        float otherValue = FloatCodec.getValue(otherBytes, otherIndex);
        
        
        // FIXME: Convert to use codec
        DimensionsRangeConstraint want = (DimensionsRangeConstraint)c.findAttr(scorerAttrId);

        float scoreFactor = getNodeScoreFactor( want, otherValue );
        float scoreVal = scoreMapper.getScore(scoreFactor);
        score.add(this, scoreVal, d);
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
		float low = FloatRangePreferenceCodec.getMin(scoreBytes, scoreIndex);
		float hi = FloatRangePreferenceCodec.getMax(scoreBytes, scoreIndex);
		float pref = FloatRangePreferenceCodec.getPref(scoreBytes, scoreIndex);
		float otherVal = FloatCodec.getValue(otherBytes, otherIndex);
		
		return scoreGap(otherVal, low, hi, pref);
	}

}
