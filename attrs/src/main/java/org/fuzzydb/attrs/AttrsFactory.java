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


import org.fuzzydb.attrs.search.SearchSpecImpl;
import org.fuzzydb.client.whirlwind.CardinalAttributeMap;

import com.wwm.db.core.Settings;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.IAttribute;

/**
// * Source of implementations of various items such as attribute maps.
 * 
 * @author Neale Upstone
 *
 */
public final class AttrsFactory {

	@SuppressWarnings("unchecked")
	public static CardinalAttributeMap<IAttribute> getCardinalAttributeMap() {
		String mapClassName = Settings.getInstance().getAttributeMapClassName();
		try {
			return (CardinalAttributeMap<IAttribute>) Class.forName(mapClassName).newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
//	    return new CardinalAttributeMapImpl();
//	    return new CompactCardinalAttributeMap();
	}

	public static <E extends IAttributeContainer> SearchSpec createSearchSpec( Class<E> clazz ){
	    return new SearchSpecImpl( clazz );
	}
}
