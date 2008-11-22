package com.wwm.attrs;

import java.util.HashMap;
import java.util.Map;

import com.wwm.attrs.bool.BooleanScorer;
import com.wwm.attrs.enums.EnumExclusiveScorerExclusive;
import com.wwm.attrs.enums.EnumExclusiveScorerPreference;
import com.wwm.attrs.enums.MultiEnumScorer;
import com.wwm.attrs.enums.SeatsScorer;
import com.wwm.attrs.location.LocationAndRangeScorer;
import com.wwm.attrs.location.PathDeviationScorer;
import com.wwm.attrs.location.RangePreferenceScorer;
import com.wwm.attrs.location.VectorDistanceScorer;
import com.wwm.attrs.simple.FloatRangePreferenceScorer;
import com.wwm.attrs.simple.SimilarFloatValueScorer;
import com.wwm.attrs.simple.WeightedSumScorer;
import com.wwm.util.AsymptoticScoreMapper;
import com.wwm.util.LinearScoreMapper;

public class XMLAliases {

	
	static private Map<String, Class<?>> scorerAliases = new HashMap<String,Class<?>>();
	
	static {
		initScorerAliases();
	}

	private static void initScorerAliases() {
        scorerAliases.put("BooleanScorer", BooleanScorer.class);
        scorerAliases.put("EnumExclusiveScorerExclusive", EnumExclusiveScorerExclusive.class);
        scorerAliases.put("EnumMatchScorer", EnumExclusiveScorerExclusive.class);
        scorerAliases.put("EnumScoresMapScorer", EnumExclusiveScorerPreference.class);
        
        scorerAliases.put("MultiEnumScorer", MultiEnumScorer.class);
        
        scorerAliases.put("SimilarFloatValueScorer", SimilarFloatValueScorer.class);
        scorerAliases.put("FloatRangePreferenceScorer", FloatRangePreferenceScorer.class);
        scorerAliases.put("WeightedSumScorer", WeightedSumScorer.class);

        scorerAliases.put("LocationAndRangeScorer", LocationAndRangeScorer.class);
        scorerAliases.put("PathDeviationScorer", PathDeviationScorer.class);
        scorerAliases.put("RangePreferenceScorer", RangePreferenceScorer.class);
        scorerAliases.put("VectorDistanceScorer", VectorDistanceScorer.class);

        scorerAliases.put("SeatsScorer", SeatsScorer.class);
        scorerAliases.put("PathDeviationScorer", PathDeviationScorer.class);

        scorerAliases.put("LinearScoreMapper", LinearScoreMapper.class);
        scorerAliases.put("AsymptoticScoreMapper", AsymptoticScoreMapper.class);
	}
	
	public static Map<String, Class<?>> getScorerAliases() {
		return scorerAliases;
	}
	
	
}
