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


import com.wwm.attrs.Score;
import com.wwm.attrs.Score.Direction;
import com.wwm.attrs.dimensions.DimensionsRangeConstraint;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.attrs.internal.TwoAttrScorer;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.ScoreMapper;


/**
 * scoreAttrId = FloatRangePreference
 * otherAttrId = FloatHave
 * 
 * @author Neale
 */
public class FloatRangePreferenceScorer extends TwoAttrScorer {

    private static final long serialVersionUID = -2314631712003854132L;

    protected ScoreMapper scoreMapper;
    
    
    public FloatRangePreferenceScorer(int scoreAttrId, int otherAttrId, ScoreMapper scoreMapper) {
		super(scoreAttrId, otherAttrId);
		this.scoreMapper = scoreMapper;
	}

    /**
     * Triggered on a FloatRangePreference (in profile/search) scoring against a BranchConstraint
     * in a Node.  The BC is a range of float values (min -> max).
     * Here we establish the highest possible score that a value in the range min->max
     * can score against the given FRP.
     * @see likemynds.db.indextree.Scorer#scoreSearchToNode(likemynds.db.indextree.Score, likemynds.db.indextree.Score.Direction, likemynds.db.indextree.attributes.Attribute, likemynds.db.indextree.NodeAnnotationContainer, IAttributeMap<IAttribute>)
     */
    @Override
    public void scoreSearchToNode(Score score, Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

		FloatRangePreference want = (FloatRangePreference) attr;
        IAttributeConstraint na = c.findAttr(otherAttrId);
        
        // If there is no Node Data then we only score null 
        if (na == null) { // || !na.hasValue()
            score.addNull(this, d);
            return;
        }
        
        float result = 0.0f;

        // If there are nulls underneath this 
        // node include a null score
        if (na.isIncludesNotSpecified() ) {
            if (isScoreNull()) {
                result = getScoreOnNull();
            }
        }
        
        FloatConstraint bc = (FloatConstraint)na;

        float branchMin = bc.getMin();
        float branchMax = bc.getMax();
//        float myMin = want.getMin();
//        float myMax = want.getMax();
        float myPref = want.getPreferred();
        
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
        if ( branchMin <= myPref && myPref <= branchMax ) {
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
        else if ( myPref > branchMax ) {
            s = scoreGap(want, branchMax);
        }
        else { // BC must be other side, so use lower end of BC 
            s = scoreGap(want, branchMin);
        }
        result = Math.max(result, s);
        score.add(this, result, d);
    }
    
    /**
     * Score a range of FRPs against one value to find the max score of that value
     * against possible FRPs in the node, specified by the DRC
     */
    @Override
    public void scoreNodeToSearch(Score score, Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {

    	IAttributeConstraint bNa = c.findAttr(scorerAttrId);
    	// 1) Do we have the scorer attribute: If not, return
		if (bNa == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

//        IAttributeConstraint bNa = na;
        FloatHave otherAttr = (FloatHave) searchAttrs.findAttr(otherAttrId);

        // If there is no Attr Data then we only score null 
        if (otherAttr == null) {
            score.addNull(this, d);
            return;
        }

        // If some nulls under this node then Score 1 so 
        // as not to push this node down in score 
        if (bNa.isIncludesNotSpecified() ) {
            score.add(this, maxScore, d);
            return;
        }
        

        
        
        DimensionsRangeConstraint want = (DimensionsRangeConstraint)bNa;

        float scoreFactor = getNodeScoreFactor( want, otherAttr.getValue() );
        float scoreVal = scoreMapper.getScore(scoreFactor);
        score.add(this, scoreVal, d);
    }
    
    
	/**
	 * Score this RangePreference against the specified container.
	 * @param valueAttrId to look for for value to score against (for DbTreeItem)
	 * @param constraintAttrId to look for for constraint to score against (for Node)
	 * @return
     */
    @Override
	public void scoreItemToItem(Score score, Score.Direction d, IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {
        IAttribute wantAttr = scoreAttrs.findAttr(scorerAttrId);
		if (wantAttr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}
    	
		assert( wantAttr.getAttrId() == scorerAttrId );

		// This is the have that we're going to score our want against.
		FloatHave have = (FloatHave)c.findAttr( otherAttrId );
        // Ignore if not scoring null
        if (have == null) {
            score.addNull(this, d);
            return;
        }
		
		if (wantAttr instanceof FloatRangePreference) { // Scoring a single want against the Have
			IFloatRangePreference want = (IFloatRangePreference)wantAttr;
			float scoreVal = scoreGap( want, have.getValue() );
            score.add(this, scoreVal, d);
		}
		else if (wantAttr instanceof DimensionsRangeConstraint) { // Scoring a node against the Have attribute
		    DimensionsRangeConstraint want = (DimensionsRangeConstraint)wantAttr;
			float nodeScoreFactor = getNodeScoreFactor( want, have.getValue() );
            score.add(this, scoreMapper.getScore(nodeScoreFactor), d);
		}
		else {
		    throw new RuntimeException( "FloatRangePreferenceScorer doesn't support " + wantAttr.getClass().getName() );
		}		
	}

    /**
     * Returns score for 'theirs' based on a simple FloatRangePref (for Item->Item),
     * and returns a result mapped through scoreMapper;
     * @param want
     * @param theirs
     * @return
     */
	protected float scoreGap(IFloatRangePreference want, float theirs)
	{
		float low = want.getMin();
		float hi = want.getMax();
		float pref = want.getPreferred();
		
        return scoreGap(theirs, low, hi, pref);
	}

	// so can be used by subclasses
	protected float scoreGap(float theirs, float low, float hi, float pref) {
		float scoreFactor = getScoreFactor(low, pref, hi, theirs);
        return scoreMapper.getScore(scoreFactor);
	}


    /**
     * Function to generate a scoreFactor ( -inf < scoreFactor <= 1.0f ) from 
     * a preference.
     * @param low
     * @param pref
     * @param hi
     * @param x
     * @return float less than or equal to 1.0
     */
    private float getScoreFactor(float low, float pref, float hi, float x) {

        float scoreFactor;
		
        float prefToVal;
        float prefToEdge;

        if (x <= pref) {
			prefToVal = pref - x; // how far from preference
			prefToEdge = pref - low; // range from pref to limit
		}
        else {
            prefToVal = x - pref; // how far from preference
            prefToEdge = hi - pref; // range from pref to limit
		}
        
        if (prefToEdge == 0) {
            return -1e6f; // DivByZero result = Minus 1 million (pounds, baby)
        }
        
        scoreFactor = 1.0f - prefToVal / prefToEdge;
        return scoreFactor;
    }

     
    protected float getNodeScoreFactor(DimensionsRangeConstraint want, float value) {
        // Max possible score is 1.0 if within the preference range.
        float minPref = want.getMin().getDimension(IFloatRangePreference.PREF);
        float maxPref = want.getMax().getDimension(IFloatRangePreference.PREF);
        if ( minPref <= value && value <= maxPref ) {
            return 1.0f;
        }
        
        // If below range then map onto line extending from 1.0 at minPref, 
        // through 0.0 at minLow.
        // minPref is point at which 1.0 will be scored
        // minLow is our guess at the lowest possible Lo value, by subtracting the highest 
        // LoToPrefDiff  from the lowest preference.
        // at minLow the score is 0.0.  Below minLow it is negative
        // minLow = minPref - range  => range = minPref - minLow (implied in code below)
        float diff, range;
        if ( value < minPref  ) {
            diff = minPref - value;
            range = want.getMax().getDimension(IFloatRangePreference.LOW_TO_PREF_DIFF);
        }
        else { // must be > minPref, and as we excluded minPref->maxPref, it's > maxPref
        // As above, but for maxHigh
            diff = value - maxPref;
            range = want.getMax().getDimension(IFloatRangePreference.PREF_TO_HIGH_DIFF);
        }
        
        if (range == 0f) return -1e6f; // DivZero -> minus 1 million 

        float scoreFactor = 1.0f - diff / range;
        return scoreFactor;
    }

    public ScoreMapper getScoreMapper() {
        return scoreMapper;
    }

    public void setScoreMapper(ScoreMapper scoreMapper) {
        this.scoreMapper = scoreMapper;
    }
}
