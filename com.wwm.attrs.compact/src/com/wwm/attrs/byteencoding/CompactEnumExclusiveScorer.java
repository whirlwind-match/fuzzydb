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
import com.wwm.attrs.enums.EnumExclusiveConstraint;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumPreferenceMap;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.attrs.internal.TwoAttrScorer;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.BitSet64;
import com.wwm.util.ByteArray;


public class CompactEnumExclusiveScorer extends TwoAttrScorer {

	private static final long serialVersionUID = 1L;
	
    private final EnumPreferenceMap map;
    
    // FIXME: Remove and implement a filters so that the Items are filtered Queries and not scored. 
    @SuppressWarnings("unused")
	private final boolean dontLinearise = false;

	
	public CompactEnumExclusiveScorer(int scoreAttrId, int otherAttrId, EnumPreferenceMap map) {
		super(scoreAttrId, otherAttrId);
        this.map = map;
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
    

    @Override
    public void scoreSearchToNode(Score score, Score.Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        // na == null All Items under this node have null for this attribute
        // na.isIncludesNotSpecified Some Items under this node have null for this attribute

    	IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

		EnumExclusiveValue bAttr = (EnumExclusiveValue) attr;

		EnumExclusiveConstraint bOtherNa = (EnumExclusiveConstraint)c.findAttr(otherAttrId);

		// If there is no Node Data then we only score null
        if (bOtherNa == null) {
            float result = map.score(bAttr, null);
            score.add(this, result, d);
            return;
        }
        
        score.add(this, calcScore(bAttr, bOtherNa ), d);
    }
    
    
    @Override
    public void scoreNodeToSearch(Score score, Score.Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
    	IAttributeConstraint bNa = c.findAttr(scorerAttrId);
		if (bNa == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

		// If we have search attribute present, but includes nulls, then we have to go for max score, as 
		// FIXME: Should this just return without adding an attribute, which is what the null would do!! mmm probably not. There would be a score so something would be added to score array
		if (bNa.isIncludesNotSpecified()) {
			score.add(this, maxScore, d);
			return;
		}

        // Aren't any nulls, but otherAttr can be null, so we iterate through what we have in node, finding best score.
		EnumExclusiveValue otherAttr = (EnumExclusiveValue) searchAttrs.findAttr(otherAttrId);
        EnumExclusiveConstraint bc = (EnumExclusiveConstraint)bNa;
    	score.add(this, calcScore(bc, otherAttr), d);
    }
    
    
    private float calcScore(ByteArray scoreBytes, int scoreIndex, ByteArray otherBytes, int otherIndex) {
		short scoreVal = EnumCodec.getValue(scoreBytes, scoreIndex);
		short otherVal = EnumCodec.getValue(otherBytes, otherIndex);
        return map.score(scoreVal, otherVal);
	}
    
    /**
     * Get best score for scoring from each enum value in constraint against attr
     * @param bc non-null
     * @param attr can be null
     * @return
     */
    private float calcScore(EnumExclusiveConstraint bc, EnumExclusiveValue attr) {
    	float result = 0.0f;
    	BitSet64 bits = bc.getBitSet();

    	for (short valIndex = 0; valIndex < bits.length(); valIndex++) {
            if (result == maxScore) break; // no need to continue
            // If set, score it
            if (bits.get(valIndex)){
            	short attrIndex = (attr == null) ? -1 : attr.getEnumIndex();
            	float tempScore = map.score(valIndex, attrIndex); // Note: constraint -> attr
            	if (tempScore > result) result = tempScore; // score against the best matching member of the constraint
            }
        }
        return result;
    }

    /**
     * Get best score for scoring from attr against each enum value in constraint 
     * @param attr non-null
     * @param bc
     * @return
     */
    private float calcScore(EnumExclusiveValue attr, EnumExclusiveConstraint bc) {
    	float result = 0.0f;

        // If there are nulls underneath this then lookup score for null, which might be higher or lower than
    	// scores for individual enums
        if (bc.isIncludesNotSpecified() ) {
            result = map.score(attr, null);
        }

        // Now find max score with individual enums in constraint
    	BitSet64 bits = bc.getBitSet();

    	for (short valIndex = 0; valIndex < bits.length(); valIndex++) {
            if (result == maxScore) break; // no need to continue
            // If set, score it
            if (bits.get(valIndex)){
            	float tempScore = map.score(attr.getEnumIndex(), valIndex); // Note: attr -> constraint
            	if (tempScore > result) result = tempScore; // score against the best matching member of the constraint
            }
        }
        return result;
    }

}
