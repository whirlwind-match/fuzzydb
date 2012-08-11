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
package com.wwm.indexer.internal;

import java.util.Map;
import java.util.TreeMap;

import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.BooleanAttribute;
import org.fuzzydb.dto.attributes.FloatAttribute;

import com.wwm.indexer.Record;

/**
 * Base class providing attribute functionality.
 */
public abstract class BaseRecord implements Record {

    private Map<String, Attribute<?>> attributes = new TreeMap<String, Attribute<?>>();

	public Map<String, Attribute<?>> getAttributes() {
		return attributes;
	}
	
    public void setAttributes(Map<String, Attribute<?>> attributes) {
        this.attributes = attributes;
    }

	public void put(String name, boolean b) {
		attributes.put(name, new BooleanAttribute(name, b));
	}

	public void put(String name, float f) {
		attributes.put(name, new FloatAttribute(name, f));
	}

	// FIXME: Add puts for other attr types, such as date, non index string etc

	
	public boolean getBoolean(String name) {
		BooleanAttribute attr = (BooleanAttribute) attributes.get(name);
		return attr.getValue();
	}
	
	public float getFloat(String name) {
		FloatAttribute attr = (FloatAttribute) attributes.get(name);
		return attr.getValue();
	}
	
}
