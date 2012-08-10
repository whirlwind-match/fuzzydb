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
package com.wwm.db.internal.whirlwind;

import java.util.ArrayList;

import org.fuzzydb.attrs.search.SearchSpecImpl;

import com.wwm.db.internal.search.NextItem;
import com.wwm.db.internal.search.Search;



public class EmptySearch implements Search {

    private static final EmptySearch instance = new EmptySearch();
    
    // Empty array list to return when asked
    private static final ArrayList<NextItem> zeroItems = new ArrayList<NextItem>(0);
    
    // Private constructor as only ever one instance per classloader
    private EmptySearch(){
        // empty
    }
    
    public static Search getInstance() {
        return instance;
    }


    public ArrayList<NextItem> getNextResults(int limit) {
        return zeroItems;
    }

    
    public SearchSpecImpl getSpec() {
        return null; // Only needed internally.
    }

    
    public boolean isMoreResults() {
        return false;
    }

    
    public boolean isNominee() {
        return false;  // Doesn't matter if user wants nominee or not, as we have no results.
    }

}
