/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.db.whirlwind;

import com.wwm.db.marker.IAttributeContainer;



public interface SearchSpec {

	public enum SearchMode { Forwards, Reverse, TwoWay };

	// TODO add scorer overrides
	// TODO (nu -> ac) review 
	
	// (NU) Removed for now, it's a convenience method: public void add(long index, Object attr);

	public void setAttributes(IAttributeContainer attributeMap);

	public IAttributeContainer getAttributes();

	
	public Class<? extends IAttributeContainer> getClazz();

	
	public int getMaxNonMatches();

	public void setMaxNonMatches(int max);

	public float getScoreThreshold();

	public void setScoreThreshold(float scoreThreshold);

	public int getTargetNumResults();

	public void setTargetNumResults(int targetNumResults);

	public String getScorerConfig();

	public void setScorerConfig(String scorerConfig);

	public SearchMode getSearchMode();

	public void setSearchMode(SearchMode searchMode);
	
}
