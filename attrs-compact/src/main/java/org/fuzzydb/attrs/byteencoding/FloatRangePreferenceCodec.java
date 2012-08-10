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
package org.fuzzydb.attrs.byteencoding;

import org.fuzzydb.attrs.simple.FloatRangePreference;
import org.fuzzydb.attrs.simple.IFloatRangePreference;
import org.fuzzydb.core.whirlwind.internal.IAttribute;

import com.wwm.util.ByteArray;



public class FloatRangePreferenceCodec extends IDimensionCodec {

	
	static FloatRangePreferenceCodec instance = null;
	
	public static synchronized CompactAttrCodec getInstance() {
		if (instance == null) {
			instance = new FloatRangePreferenceCodec();
		}
		return instance;
	}


	@Override
	public IAttribute getDecoded(ByteArray bytes, int index) {

		int attrId = getAttrId( getHeaderWord( bytes, index) );
		
		FloatRangePreference attr = new FloatRangePreference( attrId );
		populateIDimensions(bytes, index, attr);		
		return attr;
	}


	public static float getMin(ByteArray bytes, int index) {
		return IDimensionCodec.getDimension(bytes, index, IFloatRangePreference.PREF)
		- IDimensionCodec.getDimension(bytes, index, IFloatRangePreference.LOW_TO_PREF_DIFF);
	}

	public static float getMax(ByteArray bytes, int index) {
		return IDimensionCodec.getDimension(bytes, index, IFloatRangePreference.PREF)
		+ IDimensionCodec.getDimension(bytes, index, IFloatRangePreference.PREF_TO_HIGH_DIFF);
	}

	public static float getPref(ByteArray bytes, int index) {
		return IDimensionCodec.getDimension(bytes, index, IFloatRangePreference.PREF);
	}

}
