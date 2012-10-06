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
package org.fuzzydb.attrs;

import java.util.ArrayList;
import java.util.Collection;

import org.fuzzydb.client.whirlwind.IndexStrategy;

/**
 * Manual hints for relative likelihood of an attribute being used when searching.
 */
public class ManualIndexStrategy implements IndexStrategy {

    private static final long serialVersionUID = 1L;

    private String name;

    private final ArrayList<AttributePriority> priorities = new ArrayList<AttributePriority>();

    
    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private ManualIndexStrategy() {
    }
    
    public ManualIndexStrategy( String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void add(AttributePriority priority) {
        priorities.add(priority);
    }

    //	public Map<String, AttributePriority> getPriorities() {
    public Collection<AttributePriority> getPriorities() {
        return priorities;
    }


}
