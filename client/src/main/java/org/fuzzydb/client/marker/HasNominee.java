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
package org.fuzzydb.client.marker;

/**
 * Marker interface intended for use on Whirlwind objects to add a Nominee.
 */
public interface HasNominee {

	public Object getNominee();

	/**
	 * Set the item to return from a search when a search is done when nominee=true. Otherwise this item is returned.
	 * @param o
	 */
	public void setNominee(Object nominee);
}
