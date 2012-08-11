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
package org.fuzzydb.attrs.enums;



import org.fuzzydb.attrs.Score;
import org.fuzzydb.attrs.internal.IConstraintMap;
import org.fuzzydb.attrs.internal.TwoAttrScorer;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;
import org.fuzzydb.util.BitSet64;


/**
 * Scores such that the values must match EXACTLY for a score of 1.
 * Anything else scores 0.
 * @author jc
 *
 */
public class EnumExclusiveScorerExclusive extends TwoAttrScorer { 
    
    private static final long serialVersionUID = 1L;

    // Only used for MultiEnum matching... 
    // Specifies the Max number of matches to use in calculating the score
    private Integer maxMatches;
    
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private EnumExclusiveScorerExclusive() {
        this(1, 1);
    }

    /**
     * @param attrId
     * @param otherAttrId
     */
    public EnumExclusiveScorerExclusive(int attrId, int otherAttrId) {
        super( attrId, otherAttrId );
    }

    public EnumExclusiveScorerExclusive(int attrId, int otherAttrId, Integer maxMatches) {
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

        EnumValue bAttr = (EnumValue) attr;
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
            
        result = Math.max(result, calcScoreSearchToNode(bAttr, na));
        score.add(this, result, d);
    }
    
    @Override
    public void scoreNodeToSearch(Score score, Score.Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
    	IAttributeConstraint bNa = c.findAttr(scorerAttrId);
		if (bNa == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

        EnumValue otherAttr = (EnumValue) searchAttrs.findAttr(otherAttrId);

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

        float result = calcScoreNodeToSearch(bNa, otherAttr);
        score.add(this, result, d);    
    }
    
    @Override
    public void scoreItemToItem(Score score, Score.Direction d, IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

        EnumValue thisAttr = (EnumValue) attr;
        EnumValue otherAttr = (EnumValue) c.findAttr( otherAttrId );
        
        // Deal with where we're looking for 'null'
        if (thisAttr.isWantNull() ){
        	float scoreVal = (otherAttr == null) ? maxScore : minScore;
        	score.add(this, scoreVal, d);
        	return;
        }

        // Ignore if not scoring null
        if (otherAttr == null) {
            score.addNull(this, d);
            return;
        }

        float result = calcScoreItemToItem(thisAttr, otherAttr);
        score.add(this, result, d);
    }
    
    protected float calcScoreNodeToSearch(IAttributeConstraint thisBc, EnumValue otherAttr) {
        if (thisBc instanceof EnumExclusiveConstraint && otherAttr instanceof EnumExclusiveValue) {
            return calcScore((EnumExclusiveConstraint)thisBc, (EnumExclusiveValue)otherAttr);
        } else if (thisBc instanceof EnumExclusiveConstraint && otherAttr instanceof EnumMultipleValue) {
            return calcScore((EnumExclusiveConstraint)thisBc, (EnumMultipleValue)otherAttr );
        } else if (thisBc instanceof EnumMultipleConstraint && otherAttr instanceof EnumExclusiveValue) {
            return calcScore((EnumMultipleConstraint)thisBc, (EnumExclusiveValue)otherAttr);
        } else if (thisBc instanceof EnumMultipleConstraint && otherAttr instanceof EnumMultipleValue) {
            return calcScoreNodeToSearch((EnumMultipleConstraint)thisBc, (EnumMultipleValue)otherAttr);
        }
        assert(false);
        return minScore;
    }    

    protected float calcScoreSearchToNode(EnumValue thisAttr, IAttributeConstraint otherBc) {
        if (otherBc instanceof EnumExclusiveConstraint && thisAttr instanceof EnumExclusiveValue) {
            return calcScore((EnumExclusiveConstraint)otherBc, (EnumExclusiveValue)thisAttr);
        } else if (otherBc instanceof EnumExclusiveConstraint && thisAttr instanceof EnumMultipleValue) {
            return calcScore((EnumExclusiveConstraint)otherBc, (EnumMultipleValue)thisAttr );
        } else if (otherBc instanceof EnumMultipleConstraint && thisAttr instanceof EnumExclusiveValue) {
            return calcScore((EnumMultipleConstraint)otherBc, (EnumExclusiveValue)thisAttr);
        } else if (otherBc instanceof EnumMultipleConstraint && thisAttr instanceof EnumMultipleValue) {
            // NOTE: This is calling the second function as want attribute MUST be known by the score function  
            return calcScore((EnumMultipleValue)thisAttr, (EnumMultipleConstraint)otherBc);
        }
        assert(false);
        return minScore;
    }    
    
    
    protected float calcScoreItemToItem(EnumValue thisAttr, EnumValue otherAttr) {
        if (thisAttr instanceof EnumExclusiveValue && otherAttr instanceof EnumExclusiveValue) {
            return calcScore((EnumExclusiveValue)thisAttr, (EnumExclusiveValue)otherAttr);
        } else if (thisAttr instanceof EnumExclusiveValue && otherAttr instanceof EnumMultipleValue) {
            return calcScore((EnumMultipleValue)otherAttr, (EnumExclusiveValue)thisAttr );
        } else if (thisAttr instanceof EnumMultipleValue && otherAttr instanceof EnumExclusiveValue) {
            return calcScore((EnumMultipleValue)thisAttr, (EnumExclusiveValue)otherAttr);
        } else if (thisAttr instanceof EnumMultipleValue && otherAttr instanceof EnumMultipleValue) {
            return calcScore((EnumMultipleValue)thisAttr, (EnumMultipleValue)otherAttr);
        }
        return minScore;
    }

    // Score Excl -> Excl
    protected float calcScore(EnumExclusiveValue thisAttr, EnumExclusiveValue otherAttr) {
        return (thisAttr.getEnumIndex() == otherAttr.getEnumIndex())? maxScore : minScore;
    }
    
    // Score Multi -> Excl
    protected float calcScore(EnumMultipleValue mAttr, EnumExclusiveValue attr) {
        return (mAttr.contains(attr.getEnumIndex()))? maxScore : minScore;
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
     float getMultiEnumScore(int numMatches, int matchesForTopScore) {
         if (numMatches == 0) return minScore;
         if (numMatches >= matchesForTopScore) return maxScore; // Check before == 1 so when maxMatches == 1 we return maxScore
         
         float interval = (maxScore - minScore) / matchesForTopScore;
         float result = minScore + numMatches * interval;
         return result;
     }
    
    protected float calcScore(EnumMultipleValue thisAttr, EnumMultipleValue otherAttr) {
        
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

	// Score ExclNode Against Excl
    protected float calcScore(EnumExclusiveConstraint bc, EnumExclusiveValue attr) {
        BitSet64 bits = bc.getBitSet();
        // If constraint set for this enum value, then return maxScore
        if (bits.get(attr.getEnumIndex())){
            return maxScore;
        }
        return minScore;
    }    

    // Score ExclNode Against Multi
    protected float calcScore(EnumExclusiveConstraint bc, EnumMultipleValue attr) {
        BitSet64 bits = bc.getBitSet();
        for (short valIndex = 0; valIndex < bits.length(); valIndex++) {
            // If a set value in the constraint is in the multiValue then return maxScore
            if (bits.get(valIndex) && attr.contains(valIndex) ) {
                return maxScore;
            }
        }
        return minScore;
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
    protected float calcScoreNodeToSearch(EnumMultipleConstraint bc, EnumMultipleValue attr) {
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
    protected float calcScore(EnumMultipleValue attr, EnumMultipleConstraint bc) {
    	// Old algo: For each value in the constraint, score against the EMV.
    	// This required store of all variations of EMV in the constraint, rather inefficient.
    	// Instead, maintain a BitSet of all values that are set.  This will give less optimal score but save a lot of memory
    	BitSet64 constraintBits = bc.getBitSet();
    	BitSet64 attrBits = attr.getBitSet();
    	return calcScoreUsingNumMatchingBits(attrBits, constraintBits);
    }    

    
    // Score MultiNode Against Excl
    protected float calcScore(EnumMultipleConstraint bc, EnumExclusiveValue attr) {
    	// return maxScore if attr is present in in any EMV in this constraint 
//    	Collection<EnumMultipleValue> prefs = bc.getValues();
//        for (EnumMultipleValue pref : prefs) {
//            if (pref.contains(attr.getEnumIndex())) {
//                return maxScore;
//            }
//        }
    	BitSet64 bits = bc.getBitSet();
        if (bits.get(attr.getEnumIndex())){
        	return maxScore;
        }
        return minScore;
    }
}
