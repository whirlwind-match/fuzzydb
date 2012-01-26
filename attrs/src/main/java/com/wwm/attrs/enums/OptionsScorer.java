/******************************************************************************
 * Copyright (c) 2004-2012 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.attrs.enums;



import com.wwm.attrs.Score;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.attrs.internal.TwoAttrScorer;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.BitSet64;

/**
 * Scores such that the set of options presented (which may be null or an empty set), 
 * will match against a candidate as follows: <p>
 * matches = searchOptions INTERSECT candidateOptions<br>
 * numMatches = count(matches)<br>
 * numWanted = count(searchOptions)<br>
 * score = numWanted == 0 ? 1.0 : numMatches / numWanted<br>
 * 
 */
public class OptionsScorer extends TwoAttrScorer { 
    
    private static final long serialVersionUID = 1L;

    
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private OptionsScorer() {
        this(1, 1);
    }

    /**
     * @param attrId
     * @param otherAttrId
     */
    public OptionsScorer(int attrId, int otherAttrId) {
        super( attrId, otherAttrId );
    }

    
    @Override
    public void scoreSearchToNode(Score score, Score.Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        // na == null All Items under this node have null for this attribute
        // na.hasValue() do any Items under this node have this attribute
        // na.isIncludesNotSpecified Some Items under this node have null for this attribute

        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

        EnumMultipleValue bAttr = (EnumMultipleValue) attr;
        IAttributeConstraint na = c.findAttr(otherAttrId);

        // Deal with where we're looking for 'null'
        if (bAttr.isWantNull() ){
        	float scoreVal = na == null || na.isIncludesNotSpecified() // || !na.hasValue() 
        					? maxScore : minScore;
        	score.add(this, scoreVal, d);
        	return;
        }
        
        
        // If there is no Node Data then we only score null 
        if (na == null) { // || !na.hasValue()) {
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
            
        result = Math.max(result, calcScoreSearchToNode(bAttr, (EnumMultipleConstraint)na));
        score.add(this, result, d);
    }
    
    @Override
    public void scoreNodeToSearch(Score score, Score.Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
    	IAttributeConstraint bNa = c.findAttr(scorerAttrId);
		if (bNa == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

		EnumMultipleValue otherAttr = (EnumMultipleValue) searchAttrs.findAttr(otherAttrId);

        // If some nulls under this node then Score 1 so 
        // as not to push this node down in score 
        if (bNa.isIncludesNotSpecified() ) {
            score.add(this, maxScore, d);
            return;
        }
        
//        // This should never happen
//        if (!bNa.hasValue()) {
//            throw new RuntimeException(e);
//            return;
//        }

        // If there is no Attr Data then we only score null 
        if (otherAttr == null) {
            score.addNull(this, d);
            return;
        }

        float result = calcScoreNodeToSearch((EnumMultipleConstraint)bNa, otherAttr);
        score.add(this, result, d);    
    }
    
    @Override
    public void scoreItemToItem(Score score, Score.Direction d, IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

		EnumMultipleValue searchAttr = (EnumMultipleValue) attr;
		EnumMultipleValue otherAttr = (EnumMultipleValue) c.findAttr( otherAttrId );
        
        // Deal with where we're looking for 'null'
        if (searchAttr.isWantNull() ){
        	float scoreVal = (otherAttr == null) ? maxScore : minScore;
        	score.add(this, scoreVal, d);
        	return;
        }

        // Ignore if not scoring null
        if (otherAttr == null) {
            score.addNull(this, d);
            return;
        }

        float result = calcScoreItemToItem(searchAttr, otherAttr);
        score.add(this, result, d);
    }
    

    
    /**
     * Score Multi -> Multi
     * Uses a linear scale from minScore to maxScore(1.0) Based on the requested number of matches
     *  Score
     *   1.0 |                    *
     *       |                *
     *       |            *
     *       |        *
     *   0.5 |    *  
     *       |* <- minScore (e.g. 0.4)
     *       |
     *       |
     *       |
     *     0 |_____________________________
     *        0   1   2   3   4   5 <-Max Matches(example)
     *                   Matches
     *  Max matches == requested number of values to match in order to get highest score
     *  (e.g. you might say that people having 5 leisure activites in common are a good match for each other)
     */
     private float getMultiEnumScore(int numMatches, int matchesForTopScore) {
    	 if (matchesForTopScore == numMatches) {
    		 return 1f;
    	 }
         if (numMatches == 0) return minScore;
         if (numMatches >= matchesForTopScore) return maxScore; // Check before == 1 so when maxMatches == 1 we return maxScore
         
         float interval = (maxScore - minScore) / matchesForTopScore;
         float result = minScore + numMatches * interval;
         return result;
     }
    
     private float calcScoreItemToItem(EnumMultipleValue thisAttr, EnumMultipleValue otherAttr) {
        
        BitSet64 thisBits = thisAttr.getBitSet();
        BitSet64 otherBits = otherAttr.getBitSet();
        return calcScoreUsingNumMatchingBits(thisBits, otherBits);
    }

	private float calcScoreUsingNumMatchingBits(BitSet64 thisBits, BitSet64 otherBits) {
		BitSet64 matches = (BitSet64) thisBits.clone(); // Must clone(), as and() is destructive
        matches.and(otherBits);
        
        int matchCount = matches.cardinality();
        int thisCount = thisBits.cardinality();

        return getMultiEnumScore(matchCount, thisCount);
	}


    /**
     * See {@link MultiEnumScorer} for thoughts
     */
    private float calcScoreNodeToSearch(EnumMultipleConstraint bc, EnumMultipleValue attr) {

    	return 1f;
    	
    	// TODO: Implement with solid tests (index will be slower until then on bidirectional search
    	
//    	BitSet64 constraintBits = bc.getBitSet();
//    	BitSet64 attrBits = attr.getBitSet();
//
//		BitSet64 matches = (BitSet64) attrBits.clone(); // Must clone(), as and() is destructive
//        matches.and(constraintBits);
//        
//        if (matches.isEmpty()){
//        	return minScore;
//        }
//        int minCount = bc.lowestCount; // lowest count of enums in this bitset
//        if (minCount <= 1){
//        	return maxScore; // i.e. it's 1 or more out of 1 in effect
//        }
//        int countMatched = matches.cardinality();
//        return getMultiEnumScore(countMatched, minCount);
	        
    }    

    
    // Score Multi Against MultiNode
    private float calcScoreSearchToNode(EnumMultipleValue attr, EnumMultipleConstraint bc) {
    	// Old algo: For each value in the constraint, score against the EMV.
    	// This required store of all variations of EMV in the constraint, rather inefficient.
    	// Instead, maintain a BitSet of all values that are set.  This will give less optimal score but save a lot of memory
    	BitSet64 constraintBits = bc.getBitSet();
    	BitSet64 attrBits = attr.getBitSet();
    	return calcScoreUsingNumMatchingBits(attrBits, constraintBits);
    }    
}
