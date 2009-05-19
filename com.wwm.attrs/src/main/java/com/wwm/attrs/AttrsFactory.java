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
package com.wwm.attrs;


import com.wwm.attrs.internal.CardinalAttributeMapImpl;
import com.wwm.attrs.search.SearchSpecImpl;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.IAttribute;


public final class AttrsFactory {

	public static CardinalAttributeMap<IAttribute> getCardinalAttributeMap() {
	    return new CardinalAttributeMapImpl();
	}

	public static <E extends IAttributeContainer> SearchSpec createSearchSpec( Class<E> clazz ){
	    return new SearchSpecImpl( clazz );
	}
}
