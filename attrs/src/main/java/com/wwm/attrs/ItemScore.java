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

/**
 * FIXME: Rename to SummaryScore
 * 
 * A score implementation that just provides forwards and reverse scores, without a breakdown
 * of the scores or each attribute.
 * See NodeScore/FullScore for that functionality.
 */
public class ItemScore extends Score {

    private static final long serialVersionUID = 1347274595651474698L;

    protected float forwardsproduct = 1.0f;
    protected float forwardscount = 0;
    protected float reverseproduct = 1.0f;
    protected float reversecount = 0;
    protected float product = 1.0f;
    protected float count = 0f;
	
	@Override
	protected void update() {
        this.linear = linearise(product, count);
        this.forwardsLinear = linearise(forwardsproduct, forwardscount);
        this.reverseLinear = linearise(reverseproduct, reversecount);
        super.update();
	}

	@Override
	public void add(Scorer s, float score, Direction d) {
        
        float filter = s.isFilter() ? 1.0f : 0.0f;
        float weight = s.getWeight();

        float weightsum = weight * (1 - filter);
        float calcScore = 1 - weight + (weight * score);
        
        if (calcScore == 0) {
            nonMatches++;
        }

        if (d == Direction.forwards) {
            forwardsproduct *= calcScore;
            forwardscount += weightsum;
        } else {
            reverseproduct *= calcScore;
            reversecount += weightsum;
        }
        product *= calcScore;
        count += weightsum;

		invalidate();
	}
	
	
	/**
	 * Allow count to be seen.  Required for testing, but may be relevant in other ways.
	 * @return count - number of scorers that have so far contributed to this score.
	 */
	public float  getCount() {
		return count;
	}
}
