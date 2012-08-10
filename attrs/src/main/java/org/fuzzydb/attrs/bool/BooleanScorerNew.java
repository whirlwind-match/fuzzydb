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
package org.fuzzydb.attrs.bool;


import org.fuzzydb.attrs.Score;
import org.fuzzydb.attrs.internal.IConstraintMap;
import org.fuzzydb.attrs.internal.TwoAttrScorer;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;



/**
 * @author Neale
 */
public class BooleanScorerNew extends TwoAttrScorer {
    
    private static final long serialVersionUID = 7508377312739017363L;
    
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private BooleanScorerNew() {
        this(1, 1);
    }

    /**
     * @param wantAttr
     * @param haveAttr
     */
    public BooleanScorerNew(int wantAttr, int haveAttr) {
        super(wantAttr, haveAttr);
    }
    
    @Override
    public void scoreSearchToNode(Score score, Score.Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

		IBooleanValue bAttr = (IBooleanValue) attr;
        IAttributeConstraint bOtherNa = c.findAttr(otherAttrId);
        if (bAttr == null || bOtherNa == null) {
            return;
        }
        
        scoreAttributeConstraint(bOtherNa, bAttr, score, d);
    }
    
    @Override
    public void scoreNodeToSearch(Score score, Score.Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
    	IAttributeConstraint na = c.findAttr(scorerAttrId);
		if (na == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}
        IAttributeConstraint bNa = na;
        IBooleanValue otherAttr = (IBooleanValue) searchAttrs.findAttr(otherAttrId);
        if (bNa == null || otherAttr == null) {
            return;
        }
        
        scoreAttributeConstraint(bNa, otherAttr, score, d);
    }
    
    @Override
    public void scoreItemToItem(Score score, Score.Direction d, IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}
        IBooleanValue bAttr = (IBooleanValue) attr;
        IBooleanValue bOtherAttr = (IBooleanValue) c.findAttr( otherAttrId );
        if (bAttr == null || bOtherAttr == null) {
            return;
        }
        score.add(this, calcScore(bAttr, bOtherAttr), d);
    }
    
    private float calcScore(IBooleanValue thisAttr, IBooleanValue otherAttr) {
        return (thisAttr.isTrue() == otherAttr.isTrue()) ? maxScore : minScore;
    }
    
    private float calcScore(BooleanConstraint bc, IBooleanValue attr) {
        switch (bc.getState()) {
        case hasTrue:
            return attr.isTrue() ? maxScore : minScore;
        case hasFalse:
            return attr.isTrue() ? minScore : maxScore;
        case hasBoth:
            return maxScore;
        default: 
            assert false; // Should handle all cases
        return minScore;
        }
    }
    
    private  void scoreAttributeConstraint(IAttributeConstraint na, IBooleanValue attr, Score score, Score.Direction d) {
        if (na.isIncludesNotSpecified()) {
            score.add(this, 1.0f, d);
            return;
        }
        assert false : " FIXME: This is clearly wrong, as na has already been dereferenced"; //FIXME
        if (na == null) { // was !na.hasValue() I think this is the equiv
            return;
        }
        
        BooleanConstraint bc = (BooleanConstraint)na;
        if (bc != null) {
            score.add(this, calcScore(bc, attr), d);
        }        
    }
}
