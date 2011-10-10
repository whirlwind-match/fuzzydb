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
package com.wwm.attrs.internal;

import java.io.Serializable;
import java.util.ArrayList;
import com.wwm.attrs.IScoreConfiguration;
import com.wwm.attrs.Score;
import com.wwm.attrs.Scorer;
import com.wwm.attrs.Score.Direction;
import com.wwm.db.whirlwind.SearchSpec.SearchMode;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;



public class ScoreConfiguration implements IScoreConfiguration, Serializable {

    private static final long serialVersionUID = 1L;

    private String name; // we have name here to ease XML-based configuration
    // FIXME: Make this a Map, and ensure we don't have duplicates, by name of scorer
    private ArrayList<Scorer> scorersList = new ArrayList<Scorer>();

    
    /**
     * Add a scorer to this configuration.
     * 
     * @param scorer - the scorer to use for the preference (attribute) that it is configured with.
     */
	public void add(Scorer scorer) {
		scorersList.add( scorer );
	}

	public ArrayList<Scorer> getScorers() {
        return scorersList;
    }
    
	public String getName() {
		return name;
	}

    /**
     * Score all attributes that we have scorers defined for
     * @param score
     * @param d
     * @param scoreAttrs
     * @param c
     */
	private void scoreAll(Score score, Direction d, IAttributeMap<IAttribute> scoreAttrs, IAttributeMap<IAttribute> c) {
        for ( Scorer scorer : scorersList ) {
            int attrId = scorer.getScorerAttrId();
            IAttribute ia = scoreAttrs.findAttr(attrId);
            if (ia == null && scorer instanceof TwoAttrScorer) {
				continue; // Attribute might not exist
			}
            if (ia instanceof Attribute) {
                scoreItemToItems(score, d, scorer, c, scoreAttrs);
            } else {
            	throw new RuntimeException("ScoreConfiguration.scoreAll() executing code I thought was dead");
            	// scoreNodeToSearch(score, d, scorer, (IAttributeMap<IAttributeConstraint>) c, scoreAttrs);
            }
        }
    }

    /**
     * Score all attributes that we have scorers defined for
     * @param score
     * @param d
     * @param constraints
     * @param c
     */
    private void scoreAllAnnotations(Score score, Direction d, IAttributeMap<IAttributeConstraint> constraints, IAttributeMap<IAttribute> searchAttrs) {
        for ( Scorer scorer : scorersList ) {
            int attrId = scorer.getScorerAttrId();
            IAttributeConstraint ia = constraints.findAttr(attrId);
            if (ia == null && scorer instanceof TwoAttrScorer) {
				continue; // Attribute might not exist
			}
            scoreNodeToSearch(score, d, scorer, constraints, searchAttrs);
        }
    }

    /** 
	 * Support client side scoring both ways according to what search mode is set (added when migrating AppLayer)
	 */
	public Score scoreAllItemToItem(IAttributeMap<IAttribute> searchAttrs, IAttributeMap<IAttribute> itemAttrs, SearchMode searchMode) {
		final Score score = new NodeScore();
		
		if (searchMode == SearchMode.Forwards || searchMode == SearchMode.TwoWay) {
			scoreAll(score, Direction.forwards, searchAttrs, itemAttrs );
		}
		if (searchMode == SearchMode.Reverse || searchMode == SearchMode.TwoWay) {
			scoreAll(score, Direction.reverse, itemAttrs, searchAttrs );
		}
		return score;
	}
    

    public void scoreAllItemToItemBothWays(Score newScore, IAttributeMap<IAttribute> searchAttrs, IAttributeMap<IAttribute> itemAttrs) {
		this.scoreAll( newScore, Direction.forwards, searchAttrs, itemAttrs );
		this.scoreAll( newScore, Direction.reverse, itemAttrs, searchAttrs );
	}

    /* (non-Javadoc)
	 * @see likemynds.db.indextree.IScoreConfiguration#scoreAllItemToItems(likemynds.db.indextree.Score, likemynds.db.indextree.Score.Direction, com.wwm.db.core.whirlwind.internal.IAttributeMap, com.wwm.db.core.whirlwind.internal.IAttributeMap)
	 */
    public void scoreAllItemToItems(Score score, Direction d, IAttributeMap<IAttribute> scoreAttrs, IAttributeMap<IAttribute> c) {
        for ( Scorer scorer : scorersList ) {
            int attrId = scorer.getScorerAttrId();
            IAttribute ia = scoreAttrs.findAttr(attrId);
            if (ia == null && scorer instanceof TwoAttrScorer) {
				continue; // Attribute might not exist
			}
            scoreItemToItems(score, d, scorer, c, scoreAttrs);
        }
    }

	private void scoreItemToItems(Score score, Score.Direction d, Scorer scorer, 
    		IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {
        assert (scorer != null);
        
        if (scorer.getCanScore(d)) {
            scorer.scoreItemToItem(score, d, c, scoreAttrs);
        }
    }
    
    
	private void scoreNodeToSearch(Score score, Score.Direction d, Scorer scorer, 
    		IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> scoreAttrs) {
        assert (scorer != null);

        if (scorer.getCanScore(d)) {
        	// note: we cast so that all scorers can be implemented with stronger generic typing
            scorer.scoreNodeToSearch(score, d, c, scoreAttrs);
        }
    }


	/**
	 * Score all attributes that we have scorers defined for.
	 */
    private void scoreSearchToNode(Score score, Direction d, IAttributeMap<? extends IAttribute> attrs, IConstraintMap c) {

        for ( Scorer scorer : scorersList ) {
            scoreSearchToNode(score, d, scorer, c, attrs);
        }        
    }

	private void scoreSearchToNode(Score score, Score.Direction d, Scorer scorer, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        assert (scorer != null);

        if (scorer.getCanScore(d)) {
            scorer.scoreSearchToNode(score, d, c, scoreAttrs);
        }
    }

	/**
     *  Top level used for expanding nodes from WorkQ. Scores all attributes, forwards, reverse or TwoWay depending on mode.
     */
	public void scoreSearchToNodeBothWays(Score currentScore, IConstraintMap nodeAttributes, SearchMode mode, IAttributeMap<IAttribute> searchAttrs) {
		if (mode == SearchMode.Forwards || mode == SearchMode.TwoWay) {
			// inlined this.score(score, Score.Direction.forwards, nodeAttributes, scoreConfig); // TODO: Note: This breaks if forward and reverse are swapped.
		    this.scoreSearchToNode( currentScore, Score.Direction.forwards, searchAttrs, nodeAttributes);
		}
		if (mode == SearchMode.Reverse || mode == SearchMode.TwoWay) {
			// inlined nodeAttributes.score(score, Score.Direction.reverse, this, scoreConfig);
			if (nodeAttributes != null) {
		        this.scoreAllAnnotations( currentScore, Score.Direction.reverse, nodeAttributes, searchAttrs);
		    }
		}
	}

	/**
	 * Assert that this score configuration is valid (i.e. that it contains validly
	 * configured scorers)
	 */
	public void assertValid() {
		for (Scorer scorer : scorersList) {
			scorer.assertValid();
		}
	}
	

    
    
    
}