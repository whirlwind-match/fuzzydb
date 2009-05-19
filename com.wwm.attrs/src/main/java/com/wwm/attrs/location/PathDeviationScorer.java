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
import com.wwm.attrs.internal.TwoByTwoAttrScorer;
import com.wwm.db.core.LogFactory;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.ScoreMapper;


/**
 * This scorer scores a deviation segment against a path segment.
 * For example if someone is taking a car journey, as the crow flies, the car
 * is travelling along the path.  If looking for someone for car-sharing, they
 * are the potential deviation.  The score is higher for a smaller deviation. 
 * 
 * Scenarios are typically as follows:
 * - If search(type) = offeringLift (score forwards)
 * - If search(type) = both (score forward and reverse and use best) - TODO
 * - If search(type) wantLift (score reverse)
 * 
 * At the moment, we configure scorer config to do one way only.
 * 
 * FIXME: This needs a test harness, and needs fixing. It gives non-decreasing node scores, so throws an exception on Asymptotic scores.
 * For now, don't use it for demos.
 * 
 * TODO:
 * - Provide bias for weighing start-start or end-end legs as more important match
 * - ... or ... for providing higher score for journeys where more of the journey is shared (devLength / pathLength ~ 1)
 * - Support search(type) = both - needs ability to get score as max of forward and reverse
 * - Support other(type) = both - ditto
 * 
 * 
 * @author Neale
 */
public class PathDeviationScorer extends TwoByTwoAttrScorer {

    private static final long serialVersionUID = 8753919170439779617L;

    protected ScoreMapper scoreMapper;
    protected float maxPathToDevRatio;

    
    /**
     * @param scoreAttrId
     * @param scoreSecondAttrId
     * @param otherAttrId
     * @param otherSecondAttrId
     * @param scoreMapper
     * @param maxPathToDevRatio - cut off length ratio at which return zero (filter) allows
     * optimisation, once have calc'd length of two paths, can compare if they're too
     * different, without even linking them up.
     */
    public PathDeviationScorer(int scoreAttrId, int scoreSecondAttrId, 
    		int otherAttrId, int otherSecondAttrId,
    		ScoreMapper scoreMapper, float maxPathToDevRatio) {
		super(scoreAttrId, scoreSecondAttrId, otherAttrId, otherSecondAttrId);
        this.scoreMapper = scoreMapper;
        this.maxPathToDevRatio = maxPathToDevRatio;
	}

    /**
     * Score our item against another item
	 * Score = path length / total length incl deviation
     * Forward will find deviations that match a given path (i.e. driver looking for people to give lift to)
     * Reverse will find paths that match a deviation (i.e. I'm looking for a lift)
     * TODO: When adding reverse score, check if forward got scored, and ensure only best score remains.
     * @see com.wwm.attrs.Scorer#scoreItemToItems(com.wwm.attrs.Score, com.wwm.attrs.Score.Direction, likemynds.db.indextree.attributes.Attribute, com.wwm.db.core.whirlwind.internal.IAttributeMap, com.wwm.db.core.whirlwind.internal.IAttributeMap)
     */
    @Override
    public void scoreItemToItem(Score score, Direction d, IAttributeMap<IAttribute> otherAttrs, IAttributeMap<IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

    	assert( attr.getAttrId() == scorerAttrId );
    	assert (attr instanceof EcefVector);

    	
    	// Forwards search is for deviations that match the supplied path
        // (i.e. a car driver is searching for people/things to pickup and drop off on way) 
//    	if (d == Direction.forwards){
        EcefVector pathStart = (EcefVector)attr;
        EcefVector pathEnd = (EcefVector) scoreAttrs.findAttr(scoreSecondAttrId);
        EcefVector devStart = (EcefVector) otherAttrs.findAttr(otherAttrId);
        EcefVector devEnd = (EcefVector) otherAttrs.findAttr(otherSecondAttrId);
        
        scoreDevPath(score, d, pathStart, pathEnd, devStart, devEnd);
	}

    /**
     * Score the supplied deviation against the supplied path
     * @param score
     * @param d
     * @param pathStart
     * @param pathEnd
     * @param devStart
     * @param devEnd
     */
    private void scoreDevPath(Score score, Direction d, EcefVector pathStart, EcefVector pathEnd, 
    		EcefVector devStart, EcefVector devEnd) {

        // Check we've got all we need
        if (pathStart == null) return; // Nothing to score against
        if (pathEnd == null) return; // Nothing to score against
        if (devStart == null) return; // Nothing to score against
        if (devEnd == null) return; // Need this too

        float pathLength = pathStart.distance(pathEnd);

		// Find length of other path (the one that forms part of the deviation)
		float devLength = devStart.distance(devEnd);
		
		// Bomb out with zero if we hit our filter, or path is zero length
        if (pathLength == 0 || devLength / pathLength > maxPathToDevRatio){
			score.add(this, 0.0f, d);
            return;
		}
		
		// Looking good, so let's calculate real result.
		float pathToDevLength = pathStart.distance(devStart);
		float devToPathLength = devEnd.distance(pathEnd);
		float totalLength = pathToDevLength + devLength + devToPathLength;
		
		assert (totalLength * 1.01f >= pathLength); // allow for rounding errors 

		// Score declines to zero at maxPathToDevRatio
		float ratio = totalLength / pathLength;
		float scoreFactor = 1f - ( ratio / maxPathToDevRatio );
        if (scoreFactor > 1f) scoreFactor = 1f;
		float convertedScore = scoreMapper.getScore( scoreFactor );
		score.add(this, convertedScore, d);
    }


    /**
     * Called when searching for path's that match a supplied deviation
     * @param na - IAttributeConstraint for range of values of pathStart  
     */
    @Override
	public void scoreNodeToSearch(Score score, Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
        assert( d == Direction.reverse ); // Always the case

    	IAttributeConstraint na = c.findAttr(scorerAttrId);
		if (na == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}
        
        DimensionsRangeConstraint pathStart = (DimensionsRangeConstraint) na;
        assert( pathStart.getAttrId() == scorerAttrId );

        LogFactory.getLogger(getClass()).severe("Review this. Think it's fixed");
//        if (true) return; // FIXME: THERE'S A BUG DOWN HERE SOMEWHERE (comment this line out and you'll see the result)

        IAttributeConstraint annotation2 = c.findAttr(scoreSecondAttrId);
        if (annotation2 == null) return; // Need this too
        DimensionsRangeConstraint pathEnd = 
            (DimensionsRangeConstraint) annotation2;

        EcefVector devStart = (EcefVector) searchAttrs.findAttr(otherAttrId);
        if (devStart == null) return; // Nothing to score against
        
        EcefVector devEnd = (EcefVector) searchAttrs.findAttr(otherSecondAttrId);
        if (devEnd == null) return; // Nothing to score against
        
        // Preconditions satisfied, so we can plough on. 
        float maxPathLength = pathStart.getMaxDistance(pathEnd); // This can be zero, but it doesn't score under this scorer
        
        // Find length of other path (the one that forms part of the deviation)
        float devLength = devStart.distance(devEnd); // This can be zero!
        
        // Okay.. we want to find maximum possible score (which is the one with the 
        // highest total length.
        
        
        // Looking good, so let's calculate best possible score.
        float maxPathToDevLength = pathStart.getMaxDistance(devStart); 
        float maxDevToPathLength = pathEnd.getMaxDistance(devEnd);
        float maxTotalLength = maxPathToDevLength + devLength + maxDevToPathLength;
        
        
        // Score declines to zero at maxPathToDevRatio
        float ratio = maxTotalLength / maxPathLength;
        float scoreFactor = 1f - ( ratio / maxPathToDevRatio );
        float convertedScore = scoreMapper.getScore( scoreFactor );
        score.add(this, convertedScore, d);

    }
    

    /**
     * Forwards scoring of 'want' in search, to nodes in index.
     * Here we need to do path deviation based shortest possible distance between the start
     * and end points, and the respective nodes.  The deviation may be zero... so need to 
     * watch out for this case.
     * 
     */
    @Override
	public void scoreSearchToNode(Score score, Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        IAttribute attr = scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

		assert( d == Direction.forwards ); // Always the case
        assert( attr.getAttrId() == scorerAttrId );
        assert (attr instanceof EcefVector);
        
        //if (true) return; // FIXME: just testing - uncomment this to avoid scoring node

        EcefVector pathEnd = (EcefVector) scoreAttrs.findAttr(scoreSecondAttrId);
        if (pathEnd == null) return; // Nothing to score against

    
        IAttributeConstraint annotation = c.findAttr(otherAttrId);
        if (annotation == null) return; // Nothing to score against
        DimensionsRangeConstraint devStart = 
            (DimensionsRangeConstraint) annotation;

        IAttributeConstraint annotation2 = c.findAttr(otherSecondAttrId);
        if (annotation2 == null) return; // Need this too
        DimensionsRangeConstraint devEnd = 
            (DimensionsRangeConstraint) annotation2;

        // Preconditions satisfied, so we can plough on. 
        EcefVector pathStart = (EcefVector)attr;
        float pathLength = pathStart.distance(pathEnd); // This can be zero, but it doesn't score under this scorer
        
        // Find length of other path (the one that forms part of the deviation)
        float minDevLength = devStart.getDistance(devEnd); // This can be zero!
        
        // This'll give divide by zero
        if (pathLength == 0){
            score.add(this, 0.0f, d);
            return;
        }
        
        // Looking good, so let's calculate best possible score.
        float minPathToDevLength = devStart.getDistance(pathStart); 
        float minDevToPathLength = devEnd.getDistance(pathEnd);
        float minTotalLength = minPathToDevLength + minDevLength + minDevToPathLength;
        
        // If minTotalLength is less than pathLength, then it is possible for score to be 1.0 
        // for this node, so give it full marks
        if (minTotalLength <= pathLength){
            score.add(this, 1.0f, d);
            return;
        }
        
        // Score declines to zero at maxPathToDevRatio
        float ratio = minTotalLength / pathLength;
        float scoreFactor = 1f - ( ratio / maxPathToDevRatio );
        float convertedScore = scoreMapper.getScore( scoreFactor );
        score.add(this, convertedScore, d);
    }

    
    public ScoreMapper getScoreMapper() {
        return scoreMapper;
    }

    public void setScoreMapper(ScoreMapper scoreMapper) {
        this.scoreMapper = scoreMapper;
    }

    
    
}
