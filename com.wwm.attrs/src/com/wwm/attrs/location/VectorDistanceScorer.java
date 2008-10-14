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
package com.wwm.attrs.location;


import com.wwm.attrs.Score;
import com.wwm.attrs.Score.Direction;
import com.wwm.attrs.dimensions.Dimensions;
import com.wwm.attrs.dimensions.DimensionsRangeConstraint;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.attrs.internal.TwoAttrScorer;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.ScoreMapper;


/**
 * Scores according to distance, and adds distance to item score, when scoring
 * an item to another item.
 * @author Neale
 */
public class VectorDistanceScorer extends TwoAttrScorer {

    private static final long serialVersionUID = 8753919170439779617L;

    protected ScoreMapper scoreMapper;
    protected float range;
    protected boolean preferClose = true;

    public VectorDistanceScorer(int scoreAttrId, int otherAttrId, ScoreMapper scoreMapper, float range) {
        super(scoreAttrId, otherAttrId);
        this.scoreMapper = scoreMapper;
        this.range = range;
    }

    // FIXME Extract relevant bits from score()
    @Override
    public void scoreSearchToNode(Score score, Score.Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        assert( d == Direction.forwards ); // Always the case

        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
        if (attr == null) {
            return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
        }
        score(score, d, attr, c);
    }

    // FIXME Extract relevant bits from score()
    @Override
    public void scoreNodeToSearch(Score score, Score.Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
        assert( d == Direction.reverse ); // Always the case
        IAttributeConstraint na = c.findAttr(scorerAttrId);
        if (na == null) {
            return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
        }
        if (!na.isIncludesNotSpecified()) {
            score(score, d, na, c, searchAttrs);
        }
    }

    // FIXME Extract relevant bits from score()
    @Override
    public void scoreItemToItem(Score score, Score.Direction d, IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
        if (attr == null) {
            return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
        }
        score(score, d, attr, c, scoreAttrs);
    }


    public void score(Score score, Score.Direction d, IAttribute wantAttr, IAttributeMap<? extends IAttribute> c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        assert( wantAttr.getAttrId() == scorerAttrId );

        if (wantAttr instanceof EcefVector) {
            // We must be scoring a search to an item or an Item to a search
            EcefVector want = (EcefVector)wantAttr;
            EcefVector location = (EcefVector) c.findAttr(otherAttrId);
            if (location != null) {

                float distance = location.distance(want);	// actual distance in miles

                // Find out how far inside or outside the preferred range location is
                // 1 down to 0 is within range, and negative is outside
                float scoreFactor = 1f - (distance / range);

                // If preferClose is false, score any value within range as 1
                if (!preferClose && scoreFactor >= 0f ) {
                    scoreFactor = 1f;
                }

                float convertedScore = scoreMapper.getScore( scoreFactor );

                score.add(this, convertedScore, d);
                score.setScorerAttribute("Distance", distance );
            }
            return;
        } else {
            // scoring from a node to a search
            // Node attr is min/max of EcefVectors (x, y, z)
            // Attr is single point
            // Result is either: point is within possible ranges or not.
            // TODO: As 'preferCloser' is a function of the scorer, then can support both options.
            // 		If false, scoreFactor is either 1.0 or 0.0 within the range.
            // 			Outside of the range, something better is possible, based on the
            // 			largest value of 'range' within the node, and the closest point.
            // 		If true, ...
            // FIXME: Revise the above when brain is functioning, and then correct what is below.
            //		  Currently it may actually be correct!!

            DimensionsRangeConstraint node = (DimensionsRangeConstraint)wantAttr;	// min/man of a 3D (x,y,z) want
            EcefVector location = (EcefVector) scoreAttrs.findAttr(otherAttrId);
            if (location == null) {
                return; // No matching attribute, so no scoring to do
            }

            float scoreFactor;
            Dimensions low = new Dimensions( node.getMin() );

            Dimensions hi = new Dimensions( node.getMax() );

            DimensionsRangeConstraint box = new DimensionsRangeConstraint(0, low, hi);
            if (box.consistent(location)) {
                // location is within range of x,y,z's of want
                scoreFactor = 1.0f;
            }
            else {
                // see if it is within range of closest point of box

                float distance = box.getDistance( location ); // dist from
                // Find out how far inside or outside the preferred range location is
                // 1 down to 0 is within range, and negative is outside
                scoreFactor = 1f - (distance / range);
            }

            // If preferClose is false, score any value within range as 1
            if (!preferClose && scoreFactor >= 0f ) {
                scoreFactor = 1f;
            }

            float convertedScore = scoreMapper.getScore(scoreFactor);
            score.add(this, convertedScore, d);
        }
    }


    /* (non-Javadoc)
     * @see likemynds.db.indextree.Scorer#score(com.wwm.db.core.whirlwind.internal.IAttribute, likemynds.db.indextree.NodeAttributeContainer)
     */
    public void score(Score score, Score.Direction d, IAttribute wantAttr, IConstraintMap c) {

        IAttributeConstraint na = c.findAttr(otherAttrId);

        if (na == null) {
            return;
        }
        if (na.isIncludesNotSpecified()) { //na.hasValue() && // FIXME: Double check the logic this is sometimes !includesNotSpecified()
            score.add(this, 1.0f, d);
            return;
        }
        //		if (!na.hasValue()) {
        //			return;
        //		}


        assert( wantAttr.getAttrId() == scorerAttrId );
        // score against bounding box
        EcefVector want = (EcefVector)wantAttr;
        DimensionsRangeConstraint lbv = (DimensionsRangeConstraint) na;
        if (lbv != null) {
            // if location is in box, there could be an exact match, so return 1.0f
            if (lbv.consistent(want)) {
                score.add(this, 1.0f, d);
                return;
            }

            // Else score based on distance from closest point of box, to the centre of this RangePreference
            float distance = lbv.getDistance( want ); // dist from

            // Find out how far inside or outside the preferred range location is
            // 1 down to 0 is within range, and negative is outside
            float scoreFactor = 1f - (distance / range);

            // If preferClose is false, score any value within range as 1
            if (!preferClose && scoreFactor >= 0f ) {
                scoreFactor = 1f;
            }

            float convertedScore = scoreMapper.getScore( scoreFactor );

            score.add(this, convertedScore, d);
            return;
        }
    }

    public ScoreMapper getScoreMapper() {
        return scoreMapper;
    }

    public void setScoreMapper(ScoreMapper scoreMapper) {
        this.scoreMapper = scoreMapper;
    }



}
