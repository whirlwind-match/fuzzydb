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
package com.wwm.model.attributes;

/**
 * Marker interface for attributes that should not be put into Whirlwind Index
 * (these are typically items that the user wants to get back with their
 * search results, rather than hitting their own database for the information).
 */
public interface NonIndexedAttribute {

}
