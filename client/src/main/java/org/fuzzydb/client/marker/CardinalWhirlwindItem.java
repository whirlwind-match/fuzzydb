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

import org.fuzzydb.client.whirlwind.CardinalAttributeMap;

import com.wwm.db.whirlwind.internal.IAttribute;

/**
 * @deprecated It's unused so we'll mark it as such
 */
@Deprecated
public interface CardinalWhirlwindItem extends IWhirlwindItem {

	// Removed to allow to compile while using LegacyWhirlwindItem to get things moving
	// public CardinalAttributeMap getAttributes();
	
	public void setAttributes(CardinalAttributeMap<IAttribute> attributes);

}
