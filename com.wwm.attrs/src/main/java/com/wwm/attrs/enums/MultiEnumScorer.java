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



import com.wwm.attrs.Score;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.attrs.internal.TwoAttrScorer;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.BitSet64;

/**
 * Scores such that the values must match EXACTLY for a score of 1.
 * Anything else scores 0.
 * @author jc
 *
 */
public class MultiEnumScorer extends TwoAttrScorer { 
    
    private static final long serialVersionUID = 1L;

    // Only used for MultiEnum matching... 
    // Specifies the Max number of matches to use in calculating the score
    private Integer maxMatches;
    
    /**
     * @param attrId
     * @param otherAttrId
     */
    public MultiEnumScorer(int attrId, int otherAttrId) {
        super( attrId, otherAttrId );
    }

    public MultiEnumScorer(int attrId, int otherAttrId, Integer maxMatches) {
        super( attrId, otherAttrId );
        this.maxMatches = maxMatches;
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

    /**
     * Count number of matches and score against the lower of maxMatches, and the 
     * number of enum values contained in thisAttr
     * e.g. Scoring A,C,D,E against B,C,D would give 2 matches out of 4.  
     * If maxMatches were 3, then it would score 2 out of 3, and 3 or more matches would score 3 out of 3.
 	 *
     * @param thisBits
     * @param otherBits
     * @return
     */
	private float calcScoreUsingNumMatchingBits(BitSet64 thisBits, BitSet64 otherBits) {
		// FIXME: Do this as 32-bit long if it gets slow
		BitSet64 matches = (BitSet64) thisBits.clone(); // Must clone(), as and() is destructive
        matches.and(otherBits);
        
        int matchCount = matches.cardinality();
        int thisCount = thisBits.cardinality();

        int matchesForTopScore = thisCount;
        if (maxMatches != null && maxMatches < matchesForTopScore) {
        	// fewer matches are needed for top score
            matchesForTopScore = maxMatches;
        }
        
        return getMultiEnumScore(matchCount, matchesForTopScore);
	}


    /**
     *  Score MultiNode Against Multi
     *  
     *  This should give the highest possible score Node->Search.  Given that EMC has merged the bits, then
     *  it would appear that we have more 'want' enums set than there actually are.
     *  
     *  E.g. a node with A, B, and C would have ABC as the constraint, and when matched against CDE, should give a result
     *  where we know we have a match (constraintBits.and(attrBits) != 0), and max value is based on that the match might
     *  be, as in above example, due to a single match within the node (i.e. C -> CDE)
     */
    private float calcScoreNodeToSearch(EnumMultipleConstraint bc, EnumMultipleValue attr) {
    	if (maxMatches != null) throw new RuntimeException("Needs making work with maxMatches");
    	
    	BitSet64 constraintBits = bc.getBitSet();
    	BitSet64 attrBits = attr.getBitSet();

    	// FIXME: Do this as 32-bit long if it gets slow
		BitSet64 matches = (BitSet64) attrBits.clone(); // Must clone(), as and() is destructive
        matches.and(constraintBits);
        
        if (matches.isEmpty()){
        	return minScore;
        }
        int minCount = bc.lowestCount; // lowest count of enums in this bitset
        if (minCount <= 1){
        	return maxScore; // i.e. it's 1 or more out of 1 in effect
        }
        int countMatched = matches.cardinality();
        return getMultiEnumScore(countMatched, minCount);
	        
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
