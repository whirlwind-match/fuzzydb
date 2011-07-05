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
package com.wwm.attrs;


import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;


/**Abstract class representing a score, derived classes calculate a linearised score based on any number of supplied
 * component scores.
 * @author ac
 *
 */
public abstract class Score implements com.wwm.model.attributes.Score, Comparable<Score>, Serializable {

    static final long serialVersionUID = 5484270146323132L;

    protected float linear;
    protected float forwardsLinear;
    protected float reverseLinear;
    private boolean linearKnown;


    /** Number of scores added that didn't match */
    protected int nonMatches = 0;


    /**
     * The direction to manipulate the score in.
     */
    public enum Direction {
        forwards, reverse
    }

    public Score() {
        super();
        linear = 1.0f;
        linearKnown = false;
    }

    public Score(Score rhs) {
        super();
        linear = rhs.linear;
        linearKnown = rhs.linearKnown;
    }

    /**Calculates a linear score from a nonlinear product and component count
     * @param product The nonlinear product to process
     * @param count The number of component scores that went into the product
     * @return the linearised score
     */
    protected float linearise(float product, float count) {
        if (count > 1) {
            product = (float) Math.pow(product, 1.0 / count);
        }
        //        final float precision = 0.00001f; // round up to next multiple of this
        //        return precision * (int)( 1 + product / precision );
        return product;
    }

    /**
     * Make sure this instance is updated BEFORE serialisation occurs as the
     * data that calculates the results may not be serialised.
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        total();
        out.defaultWriteObject();
    }

    /**Calculates the linearised score product
     * @return The linear score total
     */
    public float total() {
        if (!linearKnown) {
            update();
            linearKnown = true;
        }
        return linear;
    }

    public float forwardsTotal() {
        if (!linearKnown) {
            update();
            linearKnown = true;
        }
        return forwardsLinear;
    }

    public float reverseTotal() {
        if (!linearKnown) {
            update();
            linearKnown = true;
        }
        return reverseLinear;
    }

    /**Add a component score
     * @param s The scorer generating the component score
     * @param score The component score
     * @param d The direction the scorer is being run in
     */
    public abstract void add(Scorer s, float score, Direction d);

    public void addNull(Scorer s, Direction d) {
        if (s.isScoreNull()) {
        	add(s,s.getScoreOnNull(), d);
        }
    }

    protected void update() {
    	linearKnown = true;
    }
    protected void invalidate() {
        linearKnown = false;
    }

    public int getNonMatches() {
        if (!linearKnown) {
            update();
            linearKnown = true;
        }
        return nonMatches;
    }

    /**
     * Result of comparing two scores is that first they must score according
     * to the lowest number of nonMatches, and then according to the
     * score.
     * @param o
     * @return
     */
    public int compareTo(Score rhs) {
        if ( getNonMatches() < rhs.getNonMatches() ) {
            return 1; // lower value is earlier in sort order
        }
        if ( getNonMatches() > rhs.getNonMatches() ) {
            return -1;
        }

        if (total() > rhs.total()) {
            return 1;
        }
        if (total() < rhs.total()) {
            return -1;
        }

        return 0;
    }


    @Override
    public String toString() {

    	if (getNonMatches() == 0) {
    		return String.valueOf( total() );
    	} else {
    		return total() + "(" + getNonMatches() + " non-matches)";
    	}
    }

    /**
     * Set an attribute.  This is to be used for at least, Distance, but, in
     * something like PathDeviationScorer, could be used to record:
     * DriverDistance, TotalDetour
     */
    public void setScorerAttribute(Direction d, String name, float value){
    	throw new UnsupportedOperationException();
    }
    
	public float getForwardsScore(String name) {
    	throw new UnsupportedOperationException();
	}

	public float getReverseScore(String name) {
    	throw new UnsupportedOperationException();
	}

	public Collection<String> getScorerAttrNames() {
    	throw new UnsupportedOperationException();
	}
}
