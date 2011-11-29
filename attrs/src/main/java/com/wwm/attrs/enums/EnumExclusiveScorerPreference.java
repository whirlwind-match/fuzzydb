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




public class EnumExclusiveScorerPreference extends TwoAttrScorer { 
    
    private static final long serialVersionUID = -9082946034037926876L;
    private final EnumPreferenceMap map;
 
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private EnumExclusiveScorerPreference() {
        this(1, 1, null);
    }

    /**
     * 
     * @param attrId
     * @param otherAttrId
     * @param map
     */
    public EnumExclusiveScorerPreference(int attrId, int otherAttrId, EnumPreferenceMap map) {
        super(attrId, otherAttrId);
        this.map = map;
    }

    @Override
    public void scoreSearchToNode(Score score, Score.Direction d, IConstraintMap otherAttrs, IAttributeMap<? extends IAttribute> scoreAttrs) {
        // na == null All Items under this node have null for this attribute
        // na.hasValue() do any Items under this node have this attribute
        // na.isIncludesNotSpecified Some Items under this node have null for this attribute

        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}
    	
        EnumExclusiveValue bAttr = (EnumExclusiveValue) attr;
        IAttributeConstraint na = otherAttrs.findAttr(otherAttrId);

        
        // If there is no Node Data then we only score null 
        if (na == null) {
            if (isScoreNull()) {
                float result = map.score(bAttr, null);
                score.add(this, result, d);
            }
            return;
        }

        float result = 0.0f;

        // If there are nulls underneath this 
        // node include a null score
        if (na.isIncludesNotSpecified() ) {
            if (isScoreNull()) {
                result = map.score(bAttr, null);
            }
        }
            
        EnumExclusiveConstraint bc = (EnumExclusiveConstraint)na;
        BitSet64 bits = bc.getBitSet();
        for (short valIndex = 0; valIndex < bits.length(); valIndex++) {
            if (result == maxScore) break; // no need to continue
            // If set, score it
            if (bits.get(valIndex)){
	            float tempScore = map.score(bAttr.getEnumIndex(), valIndex);
	            if (tempScore > result) result = tempScore;
            }
        }

        score.add(this, result, d);
    }
    
    @Override
    public void scoreNodeToSearch(Score score, Score.Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
    	IAttributeConstraint bNa = c.findAttr(scorerAttrId);
		if (bNa == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

        EnumExclusiveValue otherAttr = (EnumExclusiveValue) searchAttrs.findAttr(otherAttrId);

        // Ignore if not scoring null **** FIXME(nu->?): I don't think this isScoreNull is right.  ScoreNull is in preference map now isn't it???
        if (!isScoreNull() && otherAttr == null) {
            return;
        }

        // If some nulls under this node then Score maxScore so 
        // as not to push this node down in score 
        if (bNa.isIncludesNotSpecified() ) {
            score.add(this, maxScore, d);
            return;
        }        

        // Iterate over what is in the constraint, finding the max score against otherAttr
        float result = 0.0f;
        EnumExclusiveConstraint bc = (EnumExclusiveConstraint)bNa;
        BitSet64 bits = bc.getBitSet();
        for (short valIndex = 0; valIndex < bits.length(); valIndex++) {
            if (result == maxScore) break; // no need to continue
            // If set, score it
            if (bits.get(valIndex)){
            	float tempScore = map.score(valIndex, otherAttr.getEnumIndex());
            	if (tempScore > result) result = tempScore;
            }
        }
        score.add(this, result, d);
    }
    
    @Override
    public void scoreItemToItem(Score score, Score.Direction d, IAttributeMap<IAttribute> otherAttrs, IAttributeMap<IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

        EnumExclusiveValue thisAttr = (EnumExclusiveValue) attr;
        EnumExclusiveValue otherAttr = (EnumExclusiveValue) otherAttrs.findAttr( otherAttrId );
        
        // Ignore if not scoring null
        if (!isScoreNull() && otherAttr == null) {
            return;
        }

        float result = map.score(thisAttr, otherAttr);
        score.add(this, result, d);
    }
}
