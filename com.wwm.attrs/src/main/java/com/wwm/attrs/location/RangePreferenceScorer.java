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
import com.wwm.attrs.dimensions.DimensionsRangeConstraint;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.attrs.internal.TwoAttrScorer;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.ScoreMapper;


/**
 * @author Neale
 */
public class RangePreferenceScorer extends TwoAttrScorer {

    private static final long serialVersionUID = 8753919170439779617L;

    protected ScoreMapper scoreMapper;


    public RangePreferenceScorer(int scoreAttrId, int otherAttrId, ScoreMapper scoreMapper) {
        super(scoreAttrId, otherAttrId);
        this.scoreMapper = scoreMapper;
    }

    /**
     * Scores an RangePreference (i.e. LocationPreference: x,y,z,range,preferClose) against a range of EcefVectors
     * 
     */
    @Override
    public void scoreSearchToNode(Score score, Score.Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        assert( d == Direction.forwards ); // Always the case
        IAttribute scoreAttr = scoreAttrs.findAttr(scorerAttrId);

        // Don't score if we don't have the scoreAttr
        if (scoreAttr == null) {
            return;
        }

        // Find constraint in node
        IAttributeConstraint nodeConstraint = c.findAttr(otherAttrId);

        // If no constraint is found, then we return without having scored
        if (nodeConstraint == null) {
            return;
        }

        // If constraint is found, but within it, there are some unspecified, then we score 1.  TODO: Review whether this should be "scoreOnNull" etc
        if (nodeConstraint.isIncludesNotSpecified()) {
            score.add(this, 1.0f, d);
            return;
        }

        // sanity check and cast to what we expect
        assert( scoreAttr.getAttrId() == scorerAttrId );
        RangePreference want = (RangePreference)scoreAttr;

        DimensionsRangeConstraint vectorConstraint = (DimensionsRangeConstraint) nodeConstraint;

        // if location is in box, there could be an exact match, so return 1.0f
        if (vectorConstraint.consistent(want.centre)) {
            score.add(this, 1.0f, d);
            return;
        }

        // Else score based on distance from closest point of box, to the centre of this RangePreference
        float distance = vectorConstraint.getDistance( want.centre ); // dist from

        assert(distance >= 0); // not consistent means that it's outside the box (>= as could round to 0)

        scoreDistance(score, d, want, distance);
        return;
    }


    /* (non-Javadoc)
     * @see likemynds.db.indextree.Scorer#scoreNodeToSearch(likemynds.db.indextree.Score, likemynds.db.indextree.Score.Direction, com.wwm.db.core.whirlwind.internal.IAttributeMap, com.wwm.db.core.whirlwind.internal.IAttributeMap)
     */
    @Override
    public void scoreNodeToSearch(Score score, Score.Direction d, IAttributeMap<IAttributeConstraint> constraints, IAttributeMap<IAttribute> searchAttrs) {
        assert( d == Direction.reverse ); // Always the case
        DimensionsRangeConstraint want = (DimensionsRangeConstraint)constraints.findAttr(scorerAttrId); // min/man of a 4D (x,y,z,range) want
        if (want == null) {
            return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
        }
        if (want.isIncludesNotSpecified()) {
            return; // at least one item has a null, so would return without scoring in scoreItemToItem()
        }
        assert( want.getAttrId() == scorerAttrId );

        // scoring from a node to a search
        // Node attr is min/max of RangePreferences (x, y, z, range)
        // Attr is single point
        // Result is either: point is within possible ranges or not.
        // As range preferences may or may not be 'preferCloser' then
        // this falls back to false, so scoreFactor is either 1.0 or 0.0 within
        // the range.
        // Outside of the range, something better is possible, based on the
        // largest value of 'range' within the node, and the closest point.

        EcefVector location = (EcefVector) searchAttrs.findAttr(otherAttrId);
        if (location == null) {
            return;
        }

        //		Dimensions low = new Dimensions( want.getMin() );
        //        Dimensions hi = new Dimensions( want.getMax() );

        float scoreFactor;
        //    	DimensionsRangeConstraint box = new DimensionsRangeConstraint(0, low, hi);
        if (/*box dunno why it was box*/want.consistent(location)) {
            // location is within range of x,y,z's of want, so could be zero miles
            scoreFactor = 1.0f;
        }
        else {
            // see if it is within max want.range of closest point of box

            float distance = /*was box*/want.getDistance( location ); // dist from
            float maxWantRange = EcefVector.ecefToMiles(want.getMax().getDimension(RangePreference.RANGE)); // largest range in this node

            if (distance < maxWantRange) {
                scoreFactor = 1.0f;
            }
            else { // This did just return zero (and will for linear ScoreMapper)
                // but now returns highest score factor based on distance outside
                scoreFactor = 1.0f - distance / maxWantRange;
                assert( scoreFactor <= 0.0f );
            }
        }
        float convertedScore = scoreMapper.getScore(scoreFactor);
        // scale down for larger ranges.  Highest score will be for smallest range
        float minWantRange = EcefVector.ecefToMiles(want.getMin().getDimension(RangePreference.RANGE));
        convertedScore *= rangePenaltyFactor(minWantRange);
        score.add(this, convertedScore, d);
    }

    @Override
    public void scoreItemToItem(Score score, Score.Direction d, IAttributeMap<IAttribute> otherAttrs, IAttributeMap<IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
        if (attr == null) {
            return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
        }
        assert( attr.getAttrId() == scorerAttrId );
        assert (attr instanceof RangePreference);

        // We must be scoring a search to an item or an Item to a search
        RangePreference want = (RangePreference)attr;
        EcefVector location = (EcefVector) otherAttrs.findAttr(otherAttrId);
        if (location == null) {
            return;
        }

        float distance = location.distance(want.centre);	// actual distance in miles

        scoreDistance(score, d, want, distance);
        score.setScorerAttribute("Distance", distance );
    }

    /**
     * Calculate score for supplied distance for the given location preference.
     * @param score
     * @param d
     * @param locationPref
     * @param distance
     */
    private void scoreDistance(Score score, Score.Direction d, RangePreference locationPref, float distance) {
        // Find out how far inside or outside the preferred range location is
        // 1 down to 0 is within range, and negative is outside
        float scoreFactor = 1f - (distance / locationPref.range);

        // If preferClose is false, score any value within range as 1
        if (!locationPref.preferClose && scoreFactor >= 0f ) {
            scoreFactor = 1f;
        }

        float convertedScore = scoreMapper.getScore( scoreFactor );
        // scale down for larger ranges.  Highest score will be for smallest range
        convertedScore *= rangePenaltyFactor(locationPref.range);
        score.add(this, convertedScore, d);
    }

    /**
     * Penalise higher ranges to give more selective low range nodes a chance.
     * @param range
     * @return
     */
    private float rangePenaltyFactor(float range) {
        return 1f / ( 1f + range / 1000f);
    }


    public ScoreMapper getScoreMapper() {
        return scoreMapper;
    }

    public void setScoreMapper(ScoreMapper scoreMapper) {
        this.scoreMapper = scoreMapper;
    }



}
