package org.fuzzydb.util;

import org.fuzzydb.util.AsymptoticScoreMapper;
import org.fuzzydb.util.ScoreMapper;
import org.junit.Test;

import static org.junit.Assert.*;

public class AsymptoticScoreMapperTest {

	private ScoreMapper m1 = new AsymptoticScoreMapper(3f, 0.4f);
	
	@Test
	public void testGetScore() {
		float scoreFull = m1.getScore(1f);
		assertEquals("scoreFull = 1f", scoreFull, 1f, 0.001f);
		float scoreAtBoundary = m1.getScore(0f);
		assertEquals("scoreAtBoundary = 0.4f", scoreAtBoundary, 0.4f, 0.001f);
		
		float scoreOutside = m1.getScore(-0.0001f);
		assertTrue("scoreOutsideBoundary < 0.4f", scoreOutside < 0.4f );

		float scoreInside = m1.getScore(0.0001f);
		assertTrue("scoreInsideBoundary > 0.4f", scoreInside > 0.4f );

	}

}
