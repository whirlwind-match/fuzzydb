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
package com.wwm.indexer;

import java.util.Date;
import java.util.Map;

import com.wwm.model.attributes.Attribute;

public interface Record {

    /**
     * @return A collection of attributes
     */
    Map<String, Attribute> getAttributes();

    /**
     * @return A string containing additional information, not directly
     *         searchable but useful for rendering the search results page.
     */
    String getTitle();

    /**
     * @return A unique string identifying this record.
     */
    String getPrivateId();

    Date getUpdatedDate();

    /**
     * Add a BooleanAttribute to the attributes of this record
     * @param name
     * @param b
     */
	void put(String name, boolean b);

    /**
     * Add a FloatAttribute to the attributes of this record
     * @param name
     * @param f
     */
	void put(String name, float f);

	/**
	 * Get the named attribute and extract it's boolean value
	 * @param name
	 * @return boolean - ClassCastException is thrown if try this on the wrong type
	 */
	boolean getBoolean(String name);

	/**
	 * Get the named attribute and extract it's float value
	 * @param name
	 * @return float - ClassCastException is thrown if try this on the wrong type
	 */
	float getFloat(String string);
}
