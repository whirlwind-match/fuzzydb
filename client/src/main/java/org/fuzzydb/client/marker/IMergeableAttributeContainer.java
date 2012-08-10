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

import com.wwm.db.marker.IAttributeContainer;


/**
 * An object that can be persisted and scored against.
 * For efficiency, we want anything persisted to implement MergeableContainer, as otherwise we
 * will get duplicates of things like strings as we read and write it from comms or persistent storage.
 * 
 * @author Neale
 */
public interface IMergeableAttributeContainer extends MergeableContainer, IAttributeContainer { 

}
