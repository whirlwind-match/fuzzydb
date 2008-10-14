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

import java.util.logging.Logger;


import com.archopolis.internal.util.LogFactory;
import com.wwm.attrs.Score;
import com.wwm.attrs.Score.Direction;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.attrs.internal.TwoAttrScorer;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;



public class WeightedSumScorer extends TwoAttrScorer {

    private static final long serialVersionUID = -2777603745660080618L;

    @SuppressWarnings("unused") // might use it
	private static Logger log = LogFactory.getLogger(WeightedSumScorer.class);
	
    private final int scoreAttr3Id;
    
	public WeightedSumScorer( int scoreAttr1Id, int scoreAttr2Id, int scoreAttr3Id) {
		super(scoreAttr1Id, scoreAttr2Id);
        this.scoreAttr3Id = scoreAttr3Id;
	}

    @Override
    public void scoreItemToItem(Score score, Direction d, IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {

		if (d == Direction.reverse) {
            return;
        }
        
        FloatHave weight1 = (FloatHave) scoreAttrs.findAttr(getScorerAttrId());
        FloatHave weight2 = (FloatHave) scoreAttrs.findAttr(getOtherAttrId());
        FloatHave weight3 = (FloatHave) scoreAttrs.findAttr(scoreAttr3Id);
        
        FloatHave val1 = (FloatHave) c.findAttr(getScorerAttrId());
        FloatHave val2 = (FloatHave) c.findAttr(getOtherAttrId());
        FloatHave val3 = (FloatHave) c.findAttr(scoreAttr3Id);
        
        float scoreVal = weight1.value * val1.value + weight2.value * val2.value + weight3.value * val3.value;
        
        score.add(this, scoreVal, d);
    }
    
    @Override
    public void scoreNodeToSearch(Score score, Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
        return;
    }

    @Override
    public void scoreSearchToNode(Score score, Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {

        FloatHave weight1 = (FloatHave) scoreAttrs.findAttr(getScorerAttrId());
        FloatHave weight2 = (FloatHave) scoreAttrs.findAttr(getOtherAttrId());
        FloatHave weight3 = (FloatHave) scoreAttrs.findAttr(scoreAttr3Id);
        
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
