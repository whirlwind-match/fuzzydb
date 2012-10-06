package org.fuzzydb.attrs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.fuzzydb.attrs.bool.BooleanScorer;
import org.fuzzydb.attrs.bool.BooleanPriority;
import org.fuzzydb.attrs.dimensions.DimensionPriority;
import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.attrs.enums.EnumExclusiveScorerExclusive;
import org.fuzzydb.attrs.enums.EnumExclusiveScorerPreference;
import org.fuzzydb.attrs.enums.SingleEnumPriority;
import org.fuzzydb.attrs.enums.EnumSingleValueScorer;
import org.fuzzydb.attrs.enums.MultiEnumScorer;
import org.fuzzydb.attrs.enums.OptionsScorer;
import org.fuzzydb.attrs.enums.SeatsScorer;
import org.fuzzydb.attrs.internal.ScoreConfiguration;
import org.fuzzydb.attrs.location.LocationAndRangeScorer;
import org.fuzzydb.attrs.location.PathDeviationScorer;
import org.fuzzydb.attrs.location.VectorDistanceScorer;
import org.fuzzydb.attrs.simple.FloatRangePreferenceScorer;
import org.fuzzydb.attrs.simple.FloatPriority;
import org.fuzzydb.attrs.simple.SimilarFloatValueScorer;
import org.fuzzydb.attrs.simple.WeightedSumScorer;
import org.fuzzydb.util.AsymptoticScoreMapper;
import org.fuzzydb.util.LinearScoreMapper;

import com.thoughtworks.xstream.XStream;

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
        scorerAliases.put("EnumSingleValueScorer", EnumSingleValueScorer.class);
        
        scorerAliases.put("MultiEnumScorer", MultiEnumScorer.class);
        scorerAliases.put("OptionsScorer", OptionsScorer.class);
        
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
	    xStream.addImplicitCollection(ManualIndexStrategy.class, "priorities");
	
	    // Split config stuff
	    xStream.alias("Priority", AttributePriority.class);
	    xStream.alias("BooleanPriority", BooleanPriority.class);
	    xStream.alias("DimensionPriority", DimensionPriority.class);
	    xStream.alias("SingleEnumPriority", SingleEnumPriority.class);
	    // xStream.alias("MultiEnumPriority", MultiEnumPriority.class);
	    xStream.alias("FloatPriority", FloatPriority.class);
	    // xStream.alias("RangePriority", RangePriority.class);
	}
	
	
}
