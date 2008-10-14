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

import java.util.HashMap;
import java.util.Map;


import com.wwm.attrs.bool.BooleanValue;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumMultipleValue;
import com.wwm.attrs.location.EcefVector;
import com.wwm.attrs.location.RangePreference;
import com.wwm.attrs.simple.FloatHave;

/**
 * Maintains maps to give us lookup of required class when we want to lookup
 * an attributeId, but do not have the attribute type.  Instead, for things
 * like scorer configurations, we can look up based on context:
 * e.g. VectorDistanceScorer/scoreAttrId requires EcefVector
 * 
 * TODO: Ensure that this gets adapted, if necessary to support compact scorers.
 * This should be done by each name below being aliased to the different scorer implementations for (byteencoding/layoutencoding)
 */
public class AttrIdClassMapper {

    static private Map<String, Map<String,Class<?>>> scorerNameMap = new HashMap<String, Map<String,Class<?>>>();
    static {
        addScorerMappings();
        addSplitConfigMappings();
    }

    private static void addScorerMappings() {
        Map<String, Class<?>> currentAttrNameMap;
        // EnumSingleValueScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(1); // expect 1 entry
        currentAttrNameMap.put("scorerAttrId", EnumExclusiveValue.class);
        scorerNameMap.put("EnumSingleValueScorer", currentAttrNameMap);

        // BooleanScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(2); // expect 2 entries
        currentAttrNameMap.put("scorerAttrId", BooleanValue.class);
        currentAttrNameMap.put("otherAttrId", BooleanValue.class);
        scorerNameMap.put("BooleanScorer", currentAttrNameMap);

        // EnumExclusiveScorerExclusive
        currentAttrNameMap = new HashMap<String,Class<?>>(2); // expect 2 entries
        currentAttrNameMap.put("scorerAttrId", EnumExclusiveValue.class);
        currentAttrNameMap.put("otherAttrId", EnumExclusiveValue.class);
        scorerNameMap.put("EnumExclusiveScorerExclusive", currentAttrNameMap);
        scorerNameMap.put("EnumMatchScorer", currentAttrNameMap);

        // MultiEnumScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(2); // expect 2 entries
        currentAttrNameMap.put("scorerAttrId", EnumMultipleValue.class);
        currentAttrNameMap.put("otherAttrId", EnumMultipleValue .class);
        scorerNameMap.put("MultiEnumScorer", currentAttrNameMap);
        
        
        // EnumScoresMapScorer (was EnumExclusiveScorerPreference)
        currentAttrNameMap = new HashMap<String,Class<?>>(2); // expect 2 entries
        currentAttrNameMap.put("scorerAttrId", EnumExclusiveValue.class);
        currentAttrNameMap.put("otherAttrId", EnumExclusiveValue.class);
        scorerNameMap.put("EnumScoresMapScorer", currentAttrNameMap);

        // FloatRangePreferenceScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(2); // expect 2 entries
        currentAttrNameMap.put("scorerAttrId", FloatHave.class);
        currentAttrNameMap.put("otherAttrId", FloatHave.class);
        scorerNameMap.put("FloatRangePreferenceScorer", currentAttrNameMap);

        // LocationAndRangeScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(3); // expect 3 entries
        currentAttrNameMap.put("scorerAttrId", EcefVector.class);
        currentAttrNameMap.put("scorerRangeAttrId", FloatHave.class);
        currentAttrNameMap.put("otherAttrId", EcefVector.class);
        scorerNameMap.put("LocationAndRangeScorer", currentAttrNameMap);

        // RangePreferenceScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(2); // expect 2 entries
        currentAttrNameMap.put("scorerAttrId", RangePreference.class);
        currentAttrNameMap.put("otherAttrId", EcefVector.class);
        scorerNameMap.put("RangePreferenceScorer", currentAttrNameMap);

        // VectorDistanceScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(2); // expect 2 entries
        currentAttrNameMap.put("scorerAttrId", EcefVector.class);
        currentAttrNameMap.put("otherAttrId", EcefVector.class);
        scorerNameMap.put("VectorDistanceScorer", currentAttrNameMap);

        // SimilarFloatValueScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(2); // expect 2 entries
        currentAttrNameMap.put("scorerAttrId", FloatHave.class);
        currentAttrNameMap.put("otherAttrId", FloatHave.class);
        scorerNameMap.put("SimilarFloatValueScorer", currentAttrNameMap);

        // PathDeviationScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(4); // expect 4 entries
        currentAttrNameMap.put("scorerAttrId", EcefVector.class);
        currentAttrNameMap.put("otherAttrId", EcefVector.class);
        currentAttrNameMap.put("scoreSecondAttrId", EcefVector.class);
        currentAttrNameMap.put("otherSecondAttrId", EcefVector.class);
        scorerNameMap.put("PathDeviationScorer", currentAttrNameMap);

        // SeatsScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(4); // expect 4 entries
        currentAttrNameMap.put("scorerAttrId", EnumExclusiveValue.class);
        currentAttrNameMap.put("otherAttrId", EnumExclusiveValue.class);
        currentAttrNameMap.put("scoreSecondAttrId", EnumExclusiveValue.class);
        currentAttrNameMap.put("otherSecondAttrId", EnumExclusiveValue.class);
        scorerNameMap.put("SeatsScorer", currentAttrNameMap);

        // WeightedSumScorer
        currentAttrNameMap = new HashMap<String,Class<?>>(3); // expect 3 entries
        currentAttrNameMap.put("scorerAttrId", FloatHave.class);
        currentAttrNameMap.put("otherAttrId", FloatHave.class);
        currentAttrNameMap.put("scoreAttr3Id", FloatHave.class);
        scorerNameMap.put("WeightedSumScorer", currentAttrNameMap);
    }

    private static void addSplitConfigMappings() {
        Map<String, Class<?>> currentAttrNameMap;

        // BooleanSplitConfiguration
        currentAttrNameMap = new HashMap<String,Class<?>>(1); // expect 1 entry
        currentAttrNameMap.put("id", BooleanValue.class);
        scorerNameMap.put("BooleanSplitConfiguration", currentAttrNameMap);

        // DimensionSplitConfiguration
        currentAttrNameMap = new HashMap<String,Class<?>>(1); // expect 1 entry
        currentAttrNameMap.put("id", EcefVector.class);
        scorerNameMap.put("DimensionSplitConfiguration", currentAttrNameMap);

        // EnumExclusiveSplitConfiguration
        currentAttrNameMap = new HashMap<String,Class<?>>(1); // expect 1 entry
        currentAttrNameMap.put("id", EnumExclusiveValue.class);
        scorerNameMap.put("EnumExclusiveSplitConfiguration", currentAttrNameMap);

        // EnumMultiValueSplitConfiguration - TODO: Migrate to DB2, or discard
        currentAttrNameMap = new HashMap<String,Class<?>>(1); // expect 1 entry
        currentAttrNameMap.put("id", EnumMultipleValue.class);
        scorerNameMap.put("EnumMultiValueSplitConfiguration", currentAttrNameMap);

        // FloatSplitConfiguration
        currentAttrNameMap = new HashMap<String,Class<?>>(1); // expect 1 entry
        currentAttrNameMap.put("id", FloatHave.class);
        scorerNameMap.put("FloatSplitConfiguration", currentAttrNameMap);

        // RangeSplitConfiguration - TODO: Migrate to DB2, or discard
        currentAttrNameMap = new HashMap<String,Class<?>>(1); // expect 1 entry
        currentAttrNameMap.put("id", EcefVector.class);
        scorerNameMap.put("RangeSplitConfiguration", currentAttrNameMap);
    }

    /**
     * Determine class based in where an attribute is used.
     * e.g. VectorDistanceScorer/scoreAttrId requires an EcefVector attribute.
     * @param className
     * @param attrIdFieldName
     * @return
     */
    public static Class<?> getAttrClass(String className, String attrIdFieldName) {
        // Note: For performance, we avoid any write, or object creation operations, by using two maps,
        // instead of a single map with a concatenated string.
        Map<String, Class<?>> map = scorerNameMap.get(className);
        if (map == null) {
            return null;
        }
        return map.get(attrIdFieldName);
    }

}
