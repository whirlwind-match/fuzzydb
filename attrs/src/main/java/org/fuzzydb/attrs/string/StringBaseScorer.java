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
package org.fuzzydb.attrs.string;


import org.fuzzydb.attrs.Score;
import org.fuzzydb.attrs.internal.IConstraintMap;
import org.fuzzydb.attrs.internal.TwoAttrScorer;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;




public abstract class StringBaseScorer extends TwoAttrScorer { 
    
    private static final long serialVersionUID = 6358946451731357494L;
    
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private StringBaseScorer() {
        this(1, 1);
    }

    public StringBaseScorer(int attrId, int otherAttrId) {
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

        StringValue bAttr = (StringValue) attr;
        IAttributeConstraint bOtherNa = c.findAttr(otherAttrId);

        // If there is no Node Data then we only score null 
        if (bOtherNa == null){ // || !bOtherNa.hasValue()) {
            score.addNull(this, d);
            return;
        }
        
        float result = 0.0f;

        // If there are nulls underneath this 
        // node include a null score
        if (bOtherNa.isIncludesNotSpecified() && isScoreNull()) {
            result = getScoreOnNull();
        }        
            
        StringConstraint bc = (StringConstraint)bOtherNa;
        result = Math.max(result, calcScore(bAttr, bc));
        score.add(this, result, d);
    }
    
    @Override
    public void scoreNodeToSearch(Score score, Score.Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
    	IAttributeConstraint bNa = c.findAttr(scorerAttrId);
		if (bNa == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

        // na == null All Items under this node have null for this attribute
        // na.hasValue() do any Items under this node have this attribute
        // na.isIncludesNotSpecified Some Items under this node have null for this attribute

        StringValue otherAttr = null;
        StringMultiValue otherMultiAttr = null;
        IAttribute sAttr = searchAttrs.findAttr(otherAttrId);
		if (c.findAttr(otherAttrId) instanceof StringValue) { // FIXME: THis may be a bug
            otherAttr = (StringValue) sAttr;
        } else {
            otherMultiAttr = (StringMultiValue) sAttr;
        }

        // If some nulls under this node then Score 1 so 
        // as not to push this node down in score 
        if (bNa.isIncludesNotSpecified()) {
            score.add(this, 1.0f, d);
            return;
        }

//        if (!bNa.hasValue()) {
//            return;
//        }

        // If there is no Attr Data then we only score null 
        if (otherAttr == null && otherMultiAttr == null) { 
            score.addNull(this, d);
            return;
        }
        
        StringConstraint bc = (StringConstraint)bNa;
        if (bc != null) {
            if (otherAttr != null) {
                score.add(this, calcScore(bc, otherAttr), d);
            } else {
                score.add(this, calcScore(bc, otherMultiAttr), d);
            }
        }        
    }
    
    @Override
    public void scoreItemToItem(Score score, Score.Direction d, IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

        StringValue bAttr = (StringValue) attr;
        StringValue otherAttr = null;
        StringMultiValue otherMultiAttr = null;
        IAttribute sAttr = c.findAttr(otherAttrId);
		if (sAttr instanceof StringValue) {
            otherAttr = (StringValue) sAttr;
        } else {
            otherMultiAttr = (StringMultiValue) sAttr;
        }
        
        // Ignore if not scoring null
        if (otherAttr == null && otherMultiAttr == null) {
            score.addNull(this, d);
            return;
        }
        
        if (otherAttr != null) {
            score.add(this, calcScore(bAttr, otherAttr), d);
        } else {
            score.add(this, calcScore(bAttr, otherMultiAttr), d);
        }
    }
    
    /**
     * Simple String Match
     * @param thisAttr
     * @param otherAttr
     * @return
     */
    protected abstract float calcScore(StringValue thisAttr, StringValue otherAttr);

    /**
     * Multi Value Match
     * @param thisAttr
     * @param otherAttr
     * @return
     */
    protected abstract float calcScore(StringValue thisAttr, StringMultiValue otherAttr);
    
    protected float calcScore(StringConstraint bc, StringValue attr) {
        return maxScore;
    }    
    
    protected float calcScore(StringValue attr, StringConstraint bc) {
        return maxScore;
    }
    
    protected float calcScore(StringConstraint bc, StringMultiValue attr) {
        return maxScore;
    }
    
}
