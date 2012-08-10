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
import org.fuzzydb.attrs.Score.Direction;
import org.fuzzydb.attrs.internal.IConstraintMap;
import org.fuzzydb.attrs.internal.TwoByTwoAttrScorer;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.ScoreMapper;


/**
 * This scorer deals with matching of the number of available seats, the number of passengers looking for a lift,
 * and the lift type (want lift, offering lift)
 * For example if someone 
 * 
 * Scenarios are typically as follows:
 * - If search(type) = offeringLift (score forwards)
 * - If search(type) = both (score forward and reverse and use best) - TODO
 * - If search(type) wantLift (score reverse)
 * 
 * TODO: Change NumberOfPeople to an Integer, OR sort out Enums, so that they're ordered
 * 
 * @author Neale
 */
public class SeatsScorer extends TwoByTwoAttrScorer {

    private static final long serialVersionUID = 8753919170439779617L;

    private static final int liftTypeOfferIndex = 0;
    private static final int liftTypeWantIndex = 1;
    private static final int liftTypeBothIndex = 2;
    
    protected ScoreMapper scoreMapper;
    
    /** Default ctor for serialization libraries */
    private SeatsScorer() {
        super(1, 1, 1, 1);
    }

    /**
     * @param scoreAttrId
     * @param scoreSecondAttrId
     * @param otherAttrId
     * @param otherSecondAttrId
     * @param scoreMapper
     * @param enumDefs Defs for liftType, which is the second attr
     */
    public SeatsScorer(int scoreAttrId, int scoreSecondAttrId, 
    		int otherAttrId, int otherSecondAttrId,
    		ScoreMapper scoreMapper, String[] enumDefs) {
		super(scoreAttrId, scoreSecondAttrId, otherAttrId, otherSecondAttrId);
        this.scoreMapper = scoreMapper;
        
        // Check our assumptions are correct
        assert( enumDefs[liftTypeOfferIndex].equals("OfferingLift"));
        assert( enumDefs[liftTypeWantIndex].equals("WantLift"));
        assert( enumDefs[liftTypeBothIndex].equals("Both"));
	}

    /**
     * Score our item against another item
	 * Score = 100% if what I want can be achieved ... or.. what they want can be achieved.  
	 * It seems that the bi-directional nature needs to be handled in one go, so what we do is only score in 1 direction, 
	 * and have the behaviour be symmetrical, .. I think.
	 * 
	 * We have to do it this way, as either the forward or reverse case being a positive score should mean that we score
	 * something overall, rather than scoring zero.
	 * 
     * @see org.fuzzydb.attrs.Scorer#scoreItemToItems(org.fuzzydb.attrs.Score, org.fuzzydb.attrs.Score.Direction, likemynds.db.indextree.attributes.Attribute, org.fuzzydb.client.core.whirlwind.internal.IAttributeMap, org.fuzzydb.client.core.whirlwind.internal.IAttributeMap)
     */
    @Override
    public void scoreItemToItem(Score score, Direction d, IAttributeMap<IAttribute> otherAttrs, IAttributeMap<IAttribute> scoreAttrs) {

    	if (d == Direction.reverse) {
    		return; // we do both ways on forwards
    	}

    	// If we do not have the number of seats present, then we can't score, so we don't register a score 
    	// NOTE: This might need tweaking
    	EnumExclusiveValue scoreSeatsEnum = (EnumExclusiveValue)scoreAttrs.findAttr(scorerAttrId);
		if (scoreSeatsEnum == null) return; 

		EnumExclusiveValue scoreLiftTypeEnum = (EnumExclusiveValue)scoreAttrs.findAttr(scoreSecondAttrId);
		if (scoreLiftTypeEnum == null) return; 

	
		EnumExclusiveValue otherSeatsEnum = (EnumExclusiveValue) otherAttrs.findAttr(otherAttrId);
		EnumExclusiveValue otherLiftTypeEnum = (EnumExclusiveValue) otherAttrs.findAttr(otherSecondAttrId);
		
		assert(otherSeatsEnum != null);
		assert(otherLiftTypeEnum != null);
		
		int scoreLiftType = scoreLiftTypeEnum.getEnumIndex();
		int otherLiftType = otherLiftTypeEnum.getEnumIndex();
		
		int scoreSeats = scoreSeatsEnum.getEnumIndex() + 1; // Enum is defined 1,2,3
		int otherSeats = otherSeatsEnum.getEnumIndex() + 1; 
		
    	// If we are seeking (e.g. 2 seats), then look for "both" or "offering" where num offered >= 2
		// NOTE: Liftshare website asks how many you can carry when you specify "both"
		if (scoreLiftType == liftTypeWantIndex) {
    		
    		// If they are offering or both, then their numSeats figure will be how many they can carry
			if (otherLiftType != liftTypeWantIndex && otherSeats >= scoreSeats) {
    			score.add(this, 1f, d);
    			return;
    		}
			
			// all other combinations are not a match
    		score.add(this, 0f, d);
    		return;
    	}
    	
    	// If we are offering, then look for "both" or "seeking"
    	if (scoreLiftType == liftTypeOfferIndex ) {
    		// If the other person is offering then it's not a match.
    		if (otherLiftType == liftTypeOfferIndex){
    			score.add(this, 0f, d);
    			return;
    		}

    		// If they want and we have enough seats, it's a match
			if (otherLiftType == liftTypeWantIndex && scoreSeats >= otherSeats) {
    			score.add(this, 1f, d);
    			return;
    		}
			
			// If they are both, then it's a match if our guess can accomodate them
			int otherRequiredSeats = getRequiredSeats(otherSeats);
			if ( otherLiftType == liftTypeBothIndex && scoreSeats >= otherRequiredSeats ) {
    			score.add(this, 1f, d);
    			return;
			}
			
			// all other combinations are not a match
    		score.add(this, 0f, d);
    		return;
    	}
    	
    	
    	// If we are both, then look for either where either num offered seats is enough, or num wanted seats is sufficient
    	// We may have to assume a 4 seater car.

    	// We are both so work out an assumed number of seats we require
		int scoreRequiredSeats = getRequiredSeats(scoreSeats);
    	
    	// If the other person is offering then see if they are offering enough seats for us
		if (otherLiftType == liftTypeOfferIndex && otherSeats >= scoreRequiredSeats) {
			score.add(this, 1f, d);
			return;
		}

		// If they want and we're offering enough seats then good oh.
		if (otherLiftType == liftTypeWantIndex && scoreSeats >= otherSeats) {
			score.add(this, 1f, d);
			return;
		}
		
		// If they are both, then we guess we can fit one way or the other if total offered seats is >= 4.
		// Only failure is 2 people carriers where they are offering 2, but have 3 occupied. They'll have to work that
		// out themselves.
		if (otherLiftType == liftTypeBothIndex && scoreSeats + otherSeats >= 4 ) {
			score.add(this, 1f, d);
			return;
		}

		score.add(this, 0f, d);
		return;
    	
	}

	private int getRequiredSeats(int offeredSeats) {
		// we guess numRequiredSeats by assuming 4 seats in the car, and defaulting to 1 if offering more than 3 seats
		return (offeredSeats > 3) ? 1: 4 - offeredSeats;
	}


    /**
     * Called when searching for 
     * @param na - IAttributeConstraint 
     */
    @Override
	public void scoreNodeToSearch(Score score, Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
        assert( d == Direction.reverse ); // Always the case

        // score 1 for now and don't split on numSeats
        score.add(this, 1f, d);
        return; 
    }
    

    /**
     * Forwards scoring of 'want' in search, to nodes in index.
     */
    @Override
	public void scoreSearchToNode(Score score, Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        assert( d == Direction.forwards ); // Always the case

        // score 1 for now and don't split on numSeats
    	score.add(this, 1f, d);
        return; 
    }

    
    public ScoreMapper getScoreMapper() {
        return scoreMapper;
    }

    public void setScoreMapper(ScoreMapper scoreMapper) {
        this.scoreMapper = scoreMapper;
    }

    
    
}
