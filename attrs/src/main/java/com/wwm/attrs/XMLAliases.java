package com.wwm.attrs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;
import com.wwm.attrs.bool.BooleanScorer;
import com.wwm.attrs.bool.BooleanSplitConfiguration;
import com.wwm.attrs.dimensions.DimensionSplitConfiguration;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumExclusiveScorerExclusive;
import com.wwm.attrs.enums.EnumExclusiveScorerPreference;
import com.wwm.attrs.enums.EnumExclusiveSplitConfiguration;
import com.wwm.attrs.enums.MultiEnumScorer;
import com.wwm.attrs.enums.SeatsScorer;
import com.wwm.attrs.internal.ScoreConfiguration;
import com.wwm.attrs.location.LocationAndRangeScorer;
import com.wwm.attrs.location.PathDeviationScorer;
import com.wwm.attrs.location.VectorDistanceScorer;
import com.wwm.attrs.simple.FloatRangePreferenceScorer;
import com.wwm.attrs.simple.FloatSplitConfiguration;
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
        scorerAliases.put("VectorDistanceScorer", VectorDistanceScorer.class);

        scorerAliases.put("SeatsScorer", SeatsScorer.class);
        scorerAliases.put("PathDeviationScorer", PathDeviationScorer.class);

        scorerAliases.put("LinearScoreMapper", LinearScoreMapper.class);
        scorerAliases.put("AsymptoticScoreMapper", AsymptoticScoreMapper.class);
	}
	
	public static Map<String, Class<?>> getScorerAliases() {
		return scorerAliases;
	}

	public static void applyScorerAliases(XStream xStream) {
        // ScoreConfiguration
        xStream.alias("ScoreConfiguration", ScoreConfiguration.class);
        xStream.useAttributeFor(ScoreConfiguration.class, "name");
        // ensure contained elements are added to scorersList
        xStream.addImplicitCollection(ScoreConfiguration.class, "scorersList");

        // Scorers
        xStream.alias("Scorer", Scorer.class);
        xStream.useAttributeFor(Scorer.class, "name");

        // Add all the scorer aliases
	    for (Entry<String, Class<?>> entry : getScorerAliases().entrySet() ) {
			xStream.alias(entry.getKey(), entry.getValue());
		}
	}

	static public void applyEnumAliases(XStream xStream) {
	    // ScoreConfiguration
	    xStream.alias("EnumDefinition", EnumDefinition.class);
	    xStream.useAttributeFor(EnumDefinition.class, "name");
	    // ensure contained elements are added to scorersList
	    xStream.addImplicitCollection(EnumDefinition.class, "strValues");
	}

	static public void applyIndexConfigAliases(XStream xStream) {
	    // IndexStrategy
	    xStream.alias("ManualPriorities", ManualIndexStrategy.class);
	    xStream.useAttributeFor(ManualIndexStrategy.class, "name");
	    // ensure contained elements are added to splitConfigurations
	    xStream.addImplicitCollection(ManualIndexStrategy.class, "splitConfigurations");
	
	    // Split config stuff
	    xStream.alias("Splitter", SplitConfiguration.class);
	    xStream.alias("BooleanSplitConfiguration", BooleanSplitConfiguration.class);
	    xStream.alias("DimensionSplitConfiguration", DimensionSplitConfiguration.class);
	    xStream.alias("EnumExclusiveSplitConfiguration", EnumExclusiveSplitConfiguration.class);
	    // xStream.alias("EnumMultiValueSplitConfiguration", EnumMultiValueSplitConfiguration.class);
	    xStream.alias("FloatSplitConfiguration", FloatSplitConfiguration.class);
	    // xStream.alias("RangeSplitConfiguration", RangeSplitConfiguration.class);
	}
	
	
}
