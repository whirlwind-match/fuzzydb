package com.wwm.attrs.internal;

import com.wwm.util.LinearScoreMapper;
import com.wwm.util.ScoreMapper;

public abstract class MappedTwoAttrScorer extends TwoAttrScorer {

	private static final long serialVersionUID = 1L;
	
	private static final LinearScoreMapper defaultMapper = new LinearScoreMapper();
	
	private ScoreMapper scoreMapper;

	public MappedTwoAttrScorer(int scoreAttrId, int otherAttrId, ScoreMapper scoreMapper) {
		super(scoreAttrId, otherAttrId);
		this.scoreMapper = scoreMapper;
	}

	public ScoreMapper getScoreMapper() {
	    return scoreMapper;
	}

	public void setScoreMapper(ScoreMapper scoreMapper) {
	    this.scoreMapper = scoreMapper;
	}

    /**
     * Get the mapped score (between 0 and 1 inclusive) for the given scoreFactor
     * @param scoreFactor float <= 1.0 (negative values allowed)
     * Negative values indicate outside of preferred range.
     */
	protected float getMappedScore(float scoreFactor) {
		return scoreMapper != null ? scoreMapper.getScore(scoreFactor) : defaultMapper.getScore(scoreFactor);
	}
}