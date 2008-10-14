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
package com.wwm.attrs.search;


import java.io.Serializable;


import com.wwm.attrs.AttributeMapFactory;
import com.wwm.attrs.internal.CardinalAttributeMapImpl;
import com.wwm.db.core.Settings;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.marker.IWhirlwindItem;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;

/**
 * Defines a search.
 * A search is composed of many attributes, such as haveSex, wantSex, myLocation, 
 * locationPreference etc.
 * The scoring of items to be returned by a search is based on a ScoringConfiguration,
 * which may be the default, or may be a custom one (e.g. advertising may be different, as
 * might a search that says, "find similar". 
 * @param <T>
 */
public class SearchSpecImpl implements SearchSpec, Serializable {
	private static final long serialVersionUID = 1L;
	private IAttributeMap<IAttribute> attributes = AttributeMapFactory.newInstance(IAttribute.class);
	private Class<? extends IAttributeContainer> clazz;
	private String scorerConfig = "default";
	private float scoreThreshold = Settings.getInstance().getDefaultScoreThreshold();
	private int targetNumResults = Settings.getInstance().getDefaultTargetNumResults();
    private int maxNonMatches = 0; // Default is to not show anything where there are non-matching attributes
	private SearchMode searchMode;
	
	public SearchSpecImpl(Class<? extends IAttributeContainer> clazz) {
		this.clazz = clazz;
		this.searchMode = SearchMode.TwoWay;
	}

	public SearchSpecImpl(Class<? extends IAttributeContainer> clazz, SearchMode searchMode) {
		this.clazz = clazz;
		this.searchMode = searchMode;
	}

	public SearchSpecImpl(Class<? extends IAttributeContainer> clazz, String scorerConfig) {
		this.clazz = clazz;
		this.scorerConfig = scorerConfig;
		this.searchMode = SearchMode.TwoWay;
	}

	public SearchSpecImpl(Class<? extends IWhirlwindItem> clazz, String scorerConfig, SearchMode searchMode) {
		this.clazz = clazz;
		this.scorerConfig = scorerConfig;
		this.searchMode = searchMode;
	}
	
	
	/**
	 * @return Returns the attributes.
	 */
	public IAttributeMap<IAttribute> getAttributeMap() {
		return attributes;
	}

	public IAttributeContainer getAttributes() {
		return new CardinalAttributeMapImpl(attributes);
	}
	
	public void setAttributes(IAttributeContainer attributeMap) {
		attributes = attributeMap.getAttributeMap();
	}
	
	public Class<? extends IAttributeContainer> getClazz() {
		return clazz;
	}


    public int getMaxNonMatches() {
        return maxNonMatches;
    }

    public void setMaxNonMatches( int max ) {
        maxNonMatches = max;
    }
    
    public float getScoreThreshold() {
		return scoreThreshold;
	}
	
	public void setScoreThreshold(float scoreThreshold) {
		this.scoreThreshold = scoreThreshold;
	}

	public int getTargetNumResults() {
		return targetNumResults;
	}

	public void setTargetNumResults(int targetNumResults) {
		this.targetNumResults = targetNumResults;
	}

    /**
     * Get the name to be used to lookup the scorer configuration.
     * This is done by name, as the scorer configuration class will not be
     * available at the database client.
     * @return String - name of scorer configuration
     */
    public String getScorerConfig() {
        return scorerConfig;
    }

	public void setScorerConfig(String scorerConfig) {
		this.scorerConfig = scorerConfig;
	}

	public SearchMode getSearchMode() {
		return searchMode;
	}

    public void setSearchMode(SearchMode searchMode) {
        this.searchMode = searchMode;
    }
}
