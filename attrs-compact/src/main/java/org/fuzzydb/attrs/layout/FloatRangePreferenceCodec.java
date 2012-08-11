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
package org.fuzzydb.attrs.layout;

import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.attrs.simple.FloatRangePreference;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.util.ByteArray;




class FloatRangePreferenceCodec extends IDimensionCodec {

	
	static FloatRangePreferenceCodec instance = null;
	
	public static synchronized FloatRangePreferenceCodec getInstance() {
		if (instance == null) {
			instance = new FloatRangePreferenceCodec();
		}
		return instance;
	}


	@Override
	public IAttribute getDecoded(LayoutAttrMap<IAttribute> map, int attrId) {
		// FIXME: Need a way to deal with missing attribute - probably something like
		int index = map.getIndexQuick(attrId);
		if (!map.hasAttribute(attrId)){
			return null;
		}
		FloatRangePreference attr = new FloatRangePreference( attrId, 0f, 0f, 0f );
		populateIDimensions(map, index, attr);		
		return attr;
	}


	public static EcefVector getValue(ByteArray bytes, int index) {
		throw new UnsupportedOperationException(); // Need to work out what we need
		// might be able to do same for all IDimensions
//		return bytes.getFloat(index + FRP_VALUE_OFFSET);
	}


}
