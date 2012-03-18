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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumPreferenceMap;
import com.wwm.attrs.enums.OptionsSource;
import com.wwm.attrs.internal.ScoreConfigurationManager;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.whirlwind.IndexStrategy;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;


public class WhirlwindConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String GLOBAL_NAME = "*"; // Name for all trees in a namespace

    public static final String CLASSNAME_FIELD = "classname";
    @SuppressWarnings("unused") // It is read, but via reflection when building index
    // @Key(unique=true)
    private final String classname;	// This string and field name should match

    private Map<String,IndexStrategy> indexStrategies = new HashMap<String,IndexStrategy>(1); // only likely to have one for a while to come

    private Map<String, EnumDefinition> enumDefinitions = new HashMap<String, EnumDefinition>();
    private Map<String, EnumPreferenceMap> enumMaps = new HashMap<String, EnumPreferenceMap>();

    private ScoreConfigurationManager scoreConfigurationManager = new ScoreConfigurationManager();

    /**
     * Constructs a global default configuration, used by all trees in a store
     */
    public WhirlwindConfiguration () {
        classname = GLOBAL_NAME;
    }

    /**
     * Constructs a configuration for a particular index identified by it's class
     * @param clazz The class of index object this configuration will affect
     */
    public <T extends IAttributeContainer> WhirlwindConfiguration (Class<T> clazz) {
        classname = clazz.getName();
    }

    /**
     * Each Class can have one or more Whirlwind indexes built for it, and the selection of the best index to
     * use will then be done elsewhere by considering the ScorerConfig requested, and what attributes are present
     * in the SearchSpec.
     * When the selection of which index will be done when doing IndexManager.query(), and will be decided from within
     * IndexManager, as only it knows what indexes are available.
     * How to accomplish the query most effectively will be a QueryStrategy, and how to build an index is an IndexStrategy.
     */
    public Collection<IndexStrategy> getIndexStrategies() {
        return indexStrategies.values();
    }


    /**
     * Scorer configs are delegated to the ScoreConfigurationManager
     */
    public ScoreConfigurationManager getScoreConfigManager() {
        return scoreConfigurationManager ;
    }


    /**
     * Provide Attribute decorators so that Attribute.toString() can decode the attribute in a
     * meaningful manner.  Having this work for the Java debugger might be a challenge.. as it
     * will have to guess the Store and Namespace.
     * @param name
     */
    public void exportDecorators(String name) {

    }

    /**
     * Adds the strategy, replacing an existing one if it is of the same name
     * (given by {@link IndexStrategy#getName()}
     * @param strategy
     */
    public void addStrategy(IndexStrategy strategy) {
        indexStrategies.put(strategy.getName(), strategy);
    }


    /**
     * Add/replace named EnumDefinition
     */
    public void add(String name, EnumDefinition def) {
        enumDefinitions.put(name, def);
    }

    public Set<Entry<String, EnumDefinition>> getEnumDefinitions() {
        return enumDefinitions.entrySet();
    }

    public OptionsSource getEnumDefinition(String name) {
        return enumDefinitions.get(name);
    }

    // Enum Preference Maps

    public void add(String name, EnumPreferenceMap map) {
        enumMaps.put(name, map);
    }

    public Set<Entry<String, EnumPreferenceMap>> getEnumPreferenceMaps() {
        return enumMaps.entrySet();
    }

    public EnumPreferenceMap getEnumPreferenceMap(String name) {
        return enumMaps.get(name);
    }

    /**
     * 
     * @deprecated - Not sure we need this any more.  Left in for AppLayerOther code for the moment...
     * I think I've eliminated this from Db2 internally... well seems to work!
     * Could be impl'd as part of AttrDefinitionMgr
     */
    @Deprecated
    public void configureAttribute(int id, Object object, IAttributeConstraint constraint) {
        // do nothing
    }

    public void setDecorator(int id, IDecorator decorator) {
        // do nothing - we could add decorators later on. They were useful in Db1.
        // Could be impl'd as part of AttrDefinitionMgr
    }


}
