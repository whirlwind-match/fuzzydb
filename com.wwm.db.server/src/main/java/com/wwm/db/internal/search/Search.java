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
package com.wwm.db.internal.search;

import java.util.ArrayList;

import com.wwm.attrs.search.SearchSpecImpl;


/** 
 * An in-progress search (all Search objects are internal to the database)
 * 
 * Possible implementations include:<br>
 * - OrderedSearch (implemented) 
 * - A distributed search across a distributed DB
 * - An unordered search, where we just want results which exceed a minimum score.  This could be a useful optimisation for newsletters
 */
public interface Search {

	/**
	 * @param limit The maximum number or results to obtain (page size) 
	 * @return ArrayList of NextItem up to a maximum number specified by limit
	 */
	public ArrayList<NextItem> getNextResults(int limit);

	/**
     * This method may do appreciable work
	 * @return true if there is at least one non-zero scoring index item remaining
	 */
	public boolean isMoreResults();

    /**
     * Get search spec (this is needed when scoring results)
     * @return Returns the spec.
     */
    public SearchSpecImpl getSpec();

    /**
     * Return whether this search requires the nominee to be returned
     * FIXME: (nu->ac) - Should nominee flag be on the search spec.. everything else is.
     */
	public boolean isNominee();

}