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
package org.fuzzydb.attrs.internal;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.fuzzydb.attrs.Score;
import org.fuzzydb.attrs.Scorer;
import org.fuzzydb.core.LogFactory;
import org.springframework.util.Assert;







/**
 * Clonable node score tally.
 * This class keeps track of which scorers generated which components of the score.
 * This allows nodes in the tree to only list the split attributes, and we will calculate
 * a correct score providing this object has been passed all the way down the tree.
 * If the same scorer fires several times in the same direction, that is only counted as one score and the final value is used.
 * 
 * TODO: NodeScore (misnomer) is used as it retains details of each individual score
 */
public class NodeScore extends Score implements Serializable {
	
    private static final long serialVersionUID = 5484270146327213313L;

    // NOTE: FindBugs will warn on transients below.  If we can guarantee that scorer names are unique (which we
    // currently don't, as ScoreConfiguration stores scorers in a list, not a map
    protected transient HashMap<Scorer, Float> forwardsScores;
    protected transient HashMap<Scorer, Float> reverseScores;
    
    // these are populated on transmission to the client
    protected HashMap<String, Float> labelledForwardsScores; 
    protected HashMap<String, Float> labelledReverseScores; 
    
		
	/**Create a new score, with the initial value 1.
	 * 
	 */
	public NodeScore() {
		super();
		forwardsScores = new HashMap<Scorer, Float>();
		reverseScores = new HashMap<Scorer, Float>();
	}
	
	/**Copy Constructor. This score will be a deep copy of the parameter. 
	 * @param rhs
	 */
	@Deprecated // and low perf!
	public NodeScore(NodeScore rhs) {
		super(rhs);
		// This performs a shallow map copy, which satisfies the deep-copy contract, 
		// as both Scorer and Float are immutable or used immutably.
		forwardsScores = new HashMap<Scorer, Float>(rhs.forwardsScores);
		reverseScores = new HashMap<Scorer, Float>(rhs.reverseScores);
	}
	

	/* (non-Javadoc)
	 * @see likemynds.db.indextree.Score#add(likemynds.db.indextree.Scorer, float, likemynds.db.indextree.Score.Direction)
	 */
	@Override
	public void add(Scorer s, float score, Direction d) {

        switch (d) {
		case forwards:
			//assert(forwardsScores.get(s) == null || forwardsScores.get(s) >= score); // Score should get smaller down the tree, not bigger
			forwardsScores.put(s, score);
			break;
		case reverse:
			//assert(reverseScores.get(s) == null || reverseScores.get(s) >= score); // Score should get smaller down the tree, not bigger
			reverseScores.put(s, score);
			break;
		}
		invalidate();
	}

	/* (non-Javadoc)
	 * @see likemynds.db.indextree.Score#update()
	 */
	@Override
	protected void update() {
        assert(forwardsScores != null);
        assert(reverseScores != null);
        
        float forwardsProduct = 1.0f;
        float forwardsCount = 0;
        float reverseProduct = 1.0f;
        float reverseCount = 0;
        float product = 1.0f;
        float count = 0;
		
		for (Entry<Scorer, Float> entry : forwardsScores.entrySet()) {
            Scorer s = entry.getKey();
            float score = entry.getValue();
            
            float weight = s.getWeight();
            float calcScore = 1 - weight + (weight * score);        
            
            forwardsProduct *= calcScore;
            product *= calcScore;
            
            // Add weight to counts if not a filter
            if ( !s.isFilter() ) {
            	forwardsCount += weight;
            	count += weight;
            }
		}

		
        for (Entry<Scorer, Float> entry : reverseScores.entrySet()) {
            Scorer s = entry.getKey();
            float score = entry.getValue();
            
            float weight = s.getWeight();
            float calcScore = 1 - weight + (weight * score);        
            
            reverseProduct *= calcScore;
            product *= calcScore;

            // Add weight to counts if not a filter
            if ( !s.isFilter() ) {
            	reverseCount += weight;
            	count += weight;
            }
		}
	
		linear = linearise(product, count);
        forwardsLinear = linearise(forwardsProduct, forwardsCount);
        reverseLinear = linearise(reverseProduct, reverseCount);

        if (linear == 0) {
            nonMatches++;
        }
        super.update();
    }
	
	/**
	 * Return the number of scores that have been recorded (forwards + reverse)
	 */
	public float getCount() {
		// collections remain null until first entry is needed, so must handle null
		return (forwardsScores == null ? 0 : forwardsScores.size()) + 
				(reverseScores == null ? 0 : reverseScores.size());
	}

	/**
	 * Compares this to otherScore, and returns true if all individual scores are l
	 * the scores within otherScore
	 * This is useful for testing that the score for a each item in a leaf is always less than or equal to the parent node. 
	 * @param otherScore
	 * @return
	 */
	public boolean allScoresLowerThan(NodeScore otherScores) {
		return oneWayAllScoresLower(forwardsScores, otherScores.forwardsScores)
		&& oneWayAllScoresLower(reverseScores, otherScores.reverseScores);
	}		

	private boolean oneWayAllScoresLower(HashMap<Scorer, Float> scores, HashMap<Scorer, Float> otherScores) {
		for( Entry<Scorer, Float> entry : scores.entrySet() ){
			Scorer s = entry.getKey();
			Float otherScore = otherScores.get(s);
			if (otherScore == null){
				continue;
			}
			if (entry.getValue().floatValue() > otherScore ){
				LogFactory.getLogger(NodeScore.class).error("Attr " + entry.getKey().getScorerAttrId() + " validation failure. " 
						+ entry.getValue().floatValue() + " should not be greater than " + otherScore + ".   Scorer = " + entry.getKey().getClass().getName() );
				return false; // should be <=
			}
		}
		return true;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		getForwardScores();
		populate(labelledForwardsScores, forwardsScores);

		labelledReverseScores = new HashMap<String, Float>();
		populate(labelledReverseScores, reverseScores);

		out.defaultWriteObject();
	}

	public Map<String,Float> getForwardScores() {
		if (labelledForwardsScores == null){
			labelledForwardsScores = new HashMap<String, Float>();
		}
		return labelledForwardsScores;
	}

	public Map<String,Float> getReverseScores() {
		if (labelledReverseScores == null){
			labelledReverseScores = new HashMap<String, Float>();
		}
		return labelledReverseScores;
	}

	private void populate(HashMap<String, Float> dest, HashMap<Scorer, Float> src) {
		for (Entry<Scorer, Float> entry : src.entrySet()) {
			String name = entry.getKey().getName();
			if (name == null){
				name = String.valueOf(entry.getKey().getScorerAttrId());
			}
			dest.put(name, entry.getValue());
		}
	}
	
	public Collection<String> getScoreEntryNames() {
		Assert.state(labelledForwardsScores != null, "Cannot call scoreEntryNames() on server");
		
		HashSet<String> result = new HashSet<String>(labelledForwardsScores.keySet());
		result.addAll(labelledReverseScores.keySet());
		return result;
	}
	
	/**
	 * This is supported server side by creating a dummy scorer instance
	 */
	@Override
	public void setScorerAttribute(Direction d, String name, float value) {
		Map<String,Float> map = d == Direction.forwards ? getForwardScores() : getReverseScores();
		map.put(name, value);
	}

	public Float getForwardsScore(String name) {
		return getForwardScores().get(name);
	}

	public Float getReverseScore(String name) {
		return getReverseScores().get(name);
	}
}
