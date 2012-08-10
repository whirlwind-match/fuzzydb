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
package com.wwm.db.internal.index;

import org.fuzzydb.attrs.IScoreConfiguration;
import org.fuzzydb.attrs.WhirlwindConfiguration;
import org.fuzzydb.client.marker.IWhirlwindItem;

import com.wwm.db.internal.search.Search;
import com.wwm.db.whirlwind.SearchSpec;


public interface IndexImplementation {
	
	<T extends IWhirlwindItem> void detectIndex( WhirlwindIndexManager<T> indexManager, WhirlwindConfiguration conf );

	<T extends IWhirlwindItem> Search getSearch(SearchSpec searchSpec, IScoreConfiguration mergedScorers,
			IScoreConfiguration config, boolean wantNominee, WhirlwindIndexManager<T> indexManager);

}
