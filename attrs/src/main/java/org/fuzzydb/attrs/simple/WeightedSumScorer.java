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
package org.fuzzydb.attrs.simple;

import org.fuzzydb.attrs.Score;
import org.fuzzydb.attrs.Score.Direction;
import org.fuzzydb.attrs.internal.IConstraintMap;
import org.fuzzydb.attrs.internal.TwoAttrScorer;
import org.fuzzydb.core.LogFactory;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;
import org.slf4j.Logger;





public class WeightedSumScorer extends TwoAttrScorer {

    private static final long serialVersionUID = -2777603745660080618L;

    @SuppressWarnings("unused") // might use it
	private static Logger log = LogFactory.getLogger(WeightedSumScorer.class);
	
    private final int scoreAttr3Id;
    
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private WeightedSumScorer() {
        this(1, 1, 1);
    }

	public WeightedSumScorer( int scoreAttr1Id, int scoreAttr2Id, int scoreAttr3Id) {
		super(scoreAttr1Id, scoreAttr2Id);
        this.scoreAttr3Id = scoreAttr3Id;
	}

    @Override
    public void scoreItemToItem(Score score, Direction d, IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {

		if (d == Direction.reverse) {
            return;
        }
        
        FloatValue weight1 = (FloatValue) scoreAttrs.findAttr(getScorerAttrId());
        FloatValue weight2 = (FloatValue) scoreAttrs.findAttr(getOtherAttrId());
        FloatValue weight3 = (FloatValue) scoreAttrs.findAttr(scoreAttr3Id);
        
        FloatValue val1 = (FloatValue) c.findAttr(getScorerAttrId());
        FloatValue val2 = (FloatValue) c.findAttr(getOtherAttrId());
        FloatValue val3 = (FloatValue) c.findAttr(scoreAttr3Id);
        
        float scoreVal = weight1.value * val1.value + weight2.value * val2.value + weight3.value * val3.value;
        
        score.add(this, scoreVal, d);
    }
    
    @Override
    public void scoreNodeToSearch(Score score, Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
        return;
    }

    @Override
    public void scoreSearchToNode(Score score, Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {

        FloatValue weight1 = (FloatValue) scoreAttrs.findAttr(getScorerAttrId());
        FloatValue weight2 = (FloatValue) scoreAttrs.findAttr(getOtherAttrId());
        FloatValue weight3 = (FloatValue) scoreAttrs.findAttr(scoreAttr3Id);
        
        IAttributeConstraint annotation1 = c.findAttr(getScorerAttrId());
        IAttributeConstraint annotation2 = c.findAttr(getOtherAttrId());
        IAttributeConstraint annotation3 = c.findAttr(scoreAttr3Id);

        if(annotation1 == null || annotation2 == null || annotation3 == null) {
            return;
        }
        
        FloatConstraint bc1 = (FloatConstraint) annotation1;
        FloatConstraint bc2 = (FloatConstraint) annotation2;
        FloatConstraint bc3 = (FloatConstraint) annotation3;
        
        float scoreVal = weight1.value * bc1.getMax() + weight2.value * bc2.getMax() + weight3.value * bc3.getMax();

        score.add(this, scoreVal, d);
    }
}
